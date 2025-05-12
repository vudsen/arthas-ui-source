package io.github.vudsen.arthasui.bridge.host

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.arthasui.BridgeTestUtil
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

}