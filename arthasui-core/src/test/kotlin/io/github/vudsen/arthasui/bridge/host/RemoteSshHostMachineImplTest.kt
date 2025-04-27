package io.github.vudsen.arthasui.bridge.host

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.arthasui.BridgeTestUtil
import org.junit.Assert
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

class RemoteSshHostMachineImplTest : BasePlatformTestCase() {


    fun testCreateInteractiveShell() {
        val echoServerShell = "'#!/bin/bash\\nwhile true; do\\n  read -p \"> \" input  \\n  echo \"[EchoServer] \$input\"\\ndone'"
        val template = BridgeTestUtil.createSshHostMachine(testRootDisposable)

        val shellPath = template.getHostMachineConfig().dataDirectory + "/echoServer.sh"
        template.mkdirs(template.getHostMachineConfig().dataDirectory)
        template.getHostMachine().execute(
            "echo",
            "-e",
            echoServerShell,
            ">",
            shellPath
        ).ok()
        template.getHostMachine().createInteractiveShell("sh", shellPath).use { shell ->
            val out = shell.getOutputStream()
            val reader = BufferedReader(InputStreamReader(shell.getInputStream()))
            out.write("hello world\n".toByteArray(StandardCharsets.UTF_8))
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