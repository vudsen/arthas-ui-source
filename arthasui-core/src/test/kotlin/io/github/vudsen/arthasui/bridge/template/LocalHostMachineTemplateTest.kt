package io.github.vudsen.arthasui.bridge.template

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.arthasui.BridgeTestUtil
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.currentOS
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

}