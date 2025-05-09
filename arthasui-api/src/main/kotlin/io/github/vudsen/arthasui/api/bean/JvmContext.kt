package io.github.vudsen.arthasui.api.bean

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine

data class JvmContext(
    val template: HostMachine,
    val providerConfig: JvmProviderConfig,
) {
    fun getHostMachineAsShellAvailable(): ShellAvailableHostMachine {
        return template as ShellAvailableHostMachine
    }
}