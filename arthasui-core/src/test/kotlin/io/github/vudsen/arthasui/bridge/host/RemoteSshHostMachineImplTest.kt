package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.progress.ProgressManager
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.currentOS
import io.github.vudsen.arthasui.api.host.isFileExist
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.bridge.toolchain.DefaultToolChainManager
import io.github.vudsen.arthasui.common.util.ProgressIndicatorStack
import io.github.vudsen.test.BridgeTestUtil
import org.junit.Assert
import java.io.BufferedReader

class RemoteSshHostMachineImplTest : BasePlatformTestCase() {


    fun testCreateInteractiveShell() {
        val echoServerShell = "'#!/bin/bash\\nwhile true; do\\n  read -p \"> \" input  \\n  echo \"[EchoServer] \$input\"\\ndone'"
        val template = BridgeTestUtil.createSshHostMachine(testRootDisposable)

        val shellPath = template.getHostMachineConfig().dataDirectory + "/echoServer.sh"
        template.mkdirs(template.getHostMachineConfig().dataDirectory)
        template.execute(
            "echo",
            "-e",
            echoServerShell,
            ">",
            shellPath
        ).ok()
        template.createInteractiveShell("sh", shellPath).use { shell ->
            val out = shell.getWriter()
            val reader = BufferedReader(shell.getReader())
            out.write("hello world\n")
            out.flush()
            var spin = 0
            while (spin < 5) {
                if (reader.ready()) {
                    break
                } else {
                    Thread.sleep(500)
                }
                spin++
            }
            Assert.assertTrue(spin < 5)
            Assert.assertTrue(reader.ready())
            Assert.assertEquals("[EchoServer] hello world", reader.readLine())
        }
    }

    fun testGrep_windows() {
        if (currentOS() != OS.WINDOWS) {
            return
        }
        Assert.assertEquals(
            "world",
            BridgeTestUtil.createLocalHostMachine().grep("world", "cmd", "/c", "echo 123 && echo 456 && echo hello && echo world").ok()
        )
        Assert.assertEquals(
            "hello world",
            BridgeTestUtil.createLocalHostMachine().grep(arrayOf("world", "hello"), "cmd", "/c", "echo 123 && echo 456 && echo hello world && echo world").ok()
        )
    }

    fun testGrep_linux() {
        if (currentOS() != OS.LINUX) {
            return
        }
        Assert.assertEquals(
            "world",
            BridgeTestUtil.createLocalHostMachine().grep("world", "echo", "123\n456\nhello\nworld").ok()
        )
        Assert.assertEquals(
            "hello world",
            BridgeTestUtil.createLocalHostMachine().grep(arrayOf("world", "hello"), "echo", "123\n456\nhello world\nworld").ok()
        )
        val remoteTemplate = BridgeTestUtil.createSshHostMachine(testRootDisposable)
        Assert.assertEquals(
            "world\n",
            remoteTemplate.grep("world", "echo", "\"123\n456\nhello\nworld\"").ok()
        )
        Assert.assertEquals(
            "hello world\n",
            remoteTemplate.grep(arrayOf("world", "hello"), "echo", "\"123\n456\nhello world\nworld\"").ok()
        )

        (remoteTemplate.getHostMachine() as CloseableHostMachine).close()
    }

    fun testListFiles() {
        val hostMachine = BridgeTestUtil.createSshHostMachine(testRootDisposable)
        hostMachine.mkdirs("/opt/arthas-ui-test/pkg")
        hostMachine.execute("touch", "/opt/arthas-ui-test/hello.txt").ok()
        hostMachine.execute("touch", "/opt/arthas-ui-test/world.txt").ok()
        hostMachine.execute("touch", "/opt/arthas-ui-test/abc123.txt").ok()

        val files = hostMachine.listFiles("/opt/arthas-ui-test")
        Assert.assertEquals(listOf("abc123.txt", "hello.txt", "pkg", "world.txt"), files)
    }

    /**
     * 测试手动安装
     */
    fun testToolChainManualDownload() {
        val hostMachine = BridgeTestUtil.createSshHostMachine(testRootDisposable) {
            withExtraHost("api.github.com", "127.0.0.1")
                .withExtraHost("github.com", "127.0.0.1")
        }

        hostMachine.getHostMachineConfig().dataDirectory = "/opt/arthas-ui-test"

        hostMachine.mkdirs("/opt/arthas-ui-test/${DefaultToolChainManager.DOWNLOAD_DIRECTORY}")
        hostMachine.execute("touch", "/opt/arthas-ui-test/${DefaultToolChainManager.DOWNLOAD_DIRECTORY}/test.txt").ok()
        hostMachine.execute("tar",
            "-czf",
            "/opt/arthas-ui-test/${DefaultToolChainManager.DOWNLOAD_DIRECTORY}/0_0_jattach-linux-x64.tgz",
            "-C",
            "/opt/arthas-ui-test/${DefaultToolChainManager.DOWNLOAD_DIRECTORY}",
            "test.txt"
        ).ok()

        val files = hostMachine.listFiles("/opt/arthas-ui-test/${DefaultToolChainManager.DOWNLOAD_DIRECTORY}")
        Assert.assertEquals(listOf("0_0_jattach-linux-x64.tgz", "test.txt"), files)

        val toolchainManager = DefaultToolChainManager(hostMachine, null)
        val path = toolchainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE)

        Assert.assertEquals("/opt/arthas-ui-test/pkg/0_0_jattach", path)
        Assert.assertTrue(hostMachine.isFileExist("/opt/arthas-ui-test/pkg/0_0_jattach/test.txt"))
    }


    fun testDownloadWithIndicator() {
        val template = BridgeTestUtil.createSshHostMachine(testRootDisposable)

        ProgressManager.getInstance().runProcessWithProgressSynchronously({
            ProgressIndicatorStack.push(ProgressManager.getGlobalProgressIndicator())
            try {
                val dest = template.getHostMachineConfig().dataDirectory + "/sqlite.jar"
                template.mkdirs(template.getHostMachineConfig().dataDirectory)
                template.download(
                    "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.49.1.0/sqlite-jdbc-3.49.1.0.jar",
                    dest
                )
                Assert.assertFalse(template.isFileNotExist(dest))
            } finally {
                ProgressIndicatorStack.pop()
            }
        }, "Test", false, null)

    }

}