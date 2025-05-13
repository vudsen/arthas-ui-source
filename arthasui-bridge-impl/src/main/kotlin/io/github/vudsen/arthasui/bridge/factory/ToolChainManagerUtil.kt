package io.github.vudsen.arthasui.bridge.factory

import com.intellij.openapi.components.service
import io.github.vudsen.arthasui.api.conf.ArthasUISettingsPersistent
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.api.toolchain.ToolchainManager
import io.github.vudsen.arthasui.bridge.host.SshLinuxHostMachineImpl
import io.github.vudsen.arthasui.bridge.toolchain.DefaultToolChainManager

object ToolChainManagerUtil {

    val mirror = System.getenv("TOOLCHAIN_MIRROR")

    fun findLocalHostMachine(id: Long?): ShellAvailableHostMachine? {
        if (id == null) {
            return null
        }
        val localHostMachineConfig = service<ArthasUISettingsPersistent>().state.hostMachines.find { v -> v.id == id }
        if (localHostMachineConfig == null) {
            return null
        }
        val local = service<HostMachineConnectManager>().connect(localHostMachineConfig)
        if (local is ShellAvailableHostMachine) {
            return local
        }
        return null
    }

    fun createToolChainManager(current: ShellAvailableHostMachine): ToolchainManager {
        return DefaultToolChainManager(
            current,
            if (current is SshLinuxHostMachineImpl) { findLocalHostMachine(current.getConfiguration().localPkgSourceId) } else { null },
            mirror
        )
    }

}