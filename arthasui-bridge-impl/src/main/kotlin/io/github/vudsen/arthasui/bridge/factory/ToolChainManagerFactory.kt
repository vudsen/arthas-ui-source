package io.github.vudsen.arthasui.bridge.factory

import com.intellij.openapi.components.service
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.api.toolchain.ToolchainManager
import io.github.vudsen.arthasui.bridge.toolchain.DefaultToolChainManager

object ToolChainManagerFactory {




    fun createToolChainManager(template: HostMachineTemplate, settings: ArthasUISettings): ToolchainManager {
        val toolchainManager: ToolchainManager = DefaultToolChainManager(template, findProxy(template.getHostMachineConfig().localPkgSourceId), githubApiMirror)
    }

}