package io.github.vudsen.arthasui.bridge.toolchain

import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.api.toolchain.ToolchainManager
import io.github.vudsen.arthasui.bridge.bean.PodJvm
import io.github.vudsen.arthasui.bridge.host.K8sHostMachine

/**
 * K8s 工具链管理。仅支持本地下载，然后上传.
 */
class K8sToolChainManager(
    private val hostMachine: K8sHostMachine,
    private val localHostMachine: ShellAvailableHostMachine,
    private val jvm: PodJvm,
    mirror: String?
) : ToolchainManager {

    companion object {
        private const val DATA_DIRECTORY = "/opt/arthas-ui"
        private const val UPLOAD_DIRECTORY = "${DATA_DIRECTORY}/uploads"
    }

    private val localManager = DefaultToolChainManager(localHostMachine, null, mirror)

    private fun preparePkg() {
        val homePath = localManager.getToolChainHomePath(ToolChain.ARTHAS_BUNDLE)
        hostMachine.uploadDirectory(jvm, homePath, UPLOAD_DIRECTORY).ok()


    }

    override fun getToolChainHomePath(toolChain: ToolChain): String {
        val homePath = localManager.getToolChainHomePath(toolChain)
        hostMachine.uploadDirectory(jvm, homePath, UPLOAD_DIRECTORY).ok()
    }

}