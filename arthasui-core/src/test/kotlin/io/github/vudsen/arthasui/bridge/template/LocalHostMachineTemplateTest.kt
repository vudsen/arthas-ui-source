package io.github.vudsen.arthasui.bridge.template

import com.intellij.testFramework.LightPlatformTestCase
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.arthasui.BridgeTestUtil
import io.github.vudsen.arthasui.api.CloseableHostMachine
import org.junit.Assert

class LocalHostMachineTemplateTest :  BasePlatformTestCase() {


    fun testGrep() {
        Assert.assertEquals("world", BridgeTestUtil.createLocalHostMachine().grep("world", "echo", "123\n456\nhello\nworld"))
        val remoteTemplate = BridgeTestUtil.createSshHostMachine(testRootDisposable)
        Assert.assertEquals("world\n", remoteTemplate.grep("world", "echo", "\"123\n456\nhello\nworld\""))

        (remoteTemplate.getHostMachine() as CloseableHostMachine).close()
    }

}