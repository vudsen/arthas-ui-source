package io.github.vudsen.arthasui.bridge.template

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.arthasui.BridgeTestUtil
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.currentOS
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.bridge.toolchain.DefaultToolChainManager
import io.github.vudsen.arthasui.bridge.util.mkdirs
import org.junit.Assert

class LocalHostMachineTemplateTest :  BasePlatformTestCase() {


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
        val template = BridgeTestUtil.createSshHostMachine(testRootDisposable)
        val hostMachine = template.getHostMachine()
        hostMachine.mkdirs("/opt/arthas-ui-test/pkg")
        hostMachine.execute("touch", "/opt/arthas-ui-test/hello.txt").ok()
        hostMachine.execute("touch", "/opt/arthas-ui-test/world.txt").ok()
        hostMachine.execute("touch", "/opt/arthas-ui-test/abc123.txt").ok()

        val files = template.listFiles("/opt/arthas-ui-test")
        Assert.assertEquals(listOf("abc123.txt", "hello.txt", "pkg", "world.txt"), files)
    }

    /**
     * 测试手动安装
     */
    fun testToolChainManualDownload() {
        val template = BridgeTestUtil.createSshHostMachine(testRootDisposable) { ctr ->
            // forbidden download.
            ctr.withExtraHost("api.github.com", "127.0.0.1")
                .withExtraHost("github.com", "127.0.0.1")
        }
        template.getHostMachineConfig().dataDirectory = "/opt/arthas-ui-test"

        template.mkdirs("/opt/arthas-ui-test")
        template.getHostMachine().execute("touch", "/opt/arthas-ui-test/test.txt").ok()
        template.getHostMachine().execute("tar", "-czf", "/opt/arthas-ui-test/arthas-xx.tar.gz", "-C", "/opt/arthas-ui-test", "test.txt").ok()

        val files = template.listFiles("/opt/arthas-ui-test")
        Assert.assertEquals(listOf("arthas-xx.tar.gz", "test.txt"), files)

        val toolchainManager = DefaultToolChainManager(template, template.getHostMachineConfig())
        val path = toolchainManager.getToolChainHomePath(ToolChain.ARTHAS_BUNDLE)

        Assert.assertEquals("/opt/arthas-ui-test/pkg/arthas", path)
        Assert.assertFalse(template.isFileNotExist("/opt/arthas-ui-test/pkg/arthas/test.txt"))
    }

}