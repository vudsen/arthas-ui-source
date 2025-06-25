package io.github.vudsen.arthasui.bridge.toolchain

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.arthasui.api.conf.ArthasUISettings
import io.github.vudsen.arthasui.api.conf.ArthasUISettingsPersistent
import io.github.vudsen.test.BridgeTestUtil
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import io.github.vudsen.arthasui.bridge.factory.ToolChainManagerUtil
import org.junit.Assert
import java.io.File

class DefaultToolChainManagerTest :  BasePlatformTestCase() {

    /**
     * 测试离线安装
     */
    fun testOfflineInstall() {
        val remote = BridgeTestUtil.createSshHostMachine(testRootDisposable) {
            withExtraHost("api.github.com", "127.0.0.1")
            withExtraHost("github.com", "127.0.0.1")
            withExtraHost("dl.k8s.io", "127.0.0.1")
        }
        val local = BridgeTestUtil.createLocalHostMachine()
        (remote.getHostMachineConfig().connect as SshHostMachineConnectConfig).localPkgSourceId = local.getHostMachineConfig().id
        val persistent = service<ArthasUISettingsPersistent>()
        val old = persistent.state
        try {
            persistent.loadState(
                ArthasUISettings(
                    mutableListOf(
                        remote.getHostMachineConfig(),
                        local.getHostMachineConfig()
                    )
                )
            )
            // actual test.
            val jattachFiles =
                BridgeTestUtil.createTestTarGzFile(File("${local.getHostMachineConfig().dataDirectory}/${DefaultToolChainManager.DOWNLOAD_DIRECTORY}/jattach-linux-x64.tgz"))
            val arthasFiles =
                BridgeTestUtil.createTestZipFile(File("${local.getHostMachineConfig().dataDirectory}/${DefaultToolChainManager.DOWNLOAD_DIRECTORY}/arthas-bin.zip"))

            local.createFile("${local.getHostMachineConfig().dataDirectory}/${DefaultToolChainManager.DOWNLOAD_DIRECTORY}/kubectl-linux", "hello world")

            val toolChainManager = ToolChainManagerUtil.createToolChainManager(remote)
            Assert.assertEquals(
                jattachFiles,
                remote.listFiles(toolChainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE))
            )
            Assert.assertEquals(
                arthasFiles,
                remote.listFiles(toolChainManager.getToolChainHomePath(ToolChain.ARTHAS_BUNDLE))
            )
            Assert.assertEquals(
                "hello world",
                remote.execute("cat", toolChainManager.getToolChainHomePath(ToolChain.KUBECTL, "1.32.1")).ok()
            )
        } finally {
            File(local.getHostMachineConfig().dataDirectory).deleteRecursively()
            persistent.loadState(old)
        }
    }

}