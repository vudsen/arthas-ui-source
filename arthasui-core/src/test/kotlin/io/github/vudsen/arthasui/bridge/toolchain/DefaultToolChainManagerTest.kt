package io.github.vudsen.arthasui.bridge.toolchain

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.test.BridgeTestUtil
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import org.junit.Assert
import java.io.File

class DefaultToolChainManagerTest :  BasePlatformTestCase() {

    fun testTransferPackage() {
        val template = BridgeTestUtil.createSshHostMachine(testRootDisposable) {
            withExtraHost("api.github.com", "127.0.0.1")
            withExtraHost("github.com", "127.0.0.1")
        }

        val local = BridgeTestUtil.createLocalHostMachine()
        local.mkdirs(template.resolveDefaultDataDirectory())
        val file = File("${local.getHostMachineConfig().dataDirectory}/${DefaultToolChainManager.DOWNLOAD_DIRECTORY}/jattach-linux-x64.tgz")
        try {
            val expectedFiles = BridgeTestUtil.createTestTarGzFile(file)
            val manager = DefaultToolChainManager(template, local)
            val toolChainHomePath = manager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE)
            Assert.assertEquals(expectedFiles, template.listFiles(toolChainHomePath))
        } finally {
            file.delete()
        }

    }

}