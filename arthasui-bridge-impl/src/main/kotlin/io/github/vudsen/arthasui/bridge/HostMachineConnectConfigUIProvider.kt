package io.github.vudsen.arthasui.bridge

import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.ui.LocalConnectConfigurationForm
import io.github.vudsen.arthasui.bridge.ui.SshConfigurationForm

class HostMachineConnectConfigUIProvider(private val oldState: HostMachineConnectConfig) {

    companion object {
        enum class ConnectType {
            LOCAL,
            SSH
        }
    }

    private val forms: Map<ConnectType, FormComponent<HostMachineConnectConfig>>

    init {
        forms = mutableMapOf()
        forms[ConnectType.LOCAL] = LocalConnectConfigurationForm()
        forms[ConnectType.SSH] = SshConfigurationForm(oldState)
    }

    fun getUI(type: ConnectType): FormComponent<HostMachineConnectConfig> {
        return forms[type]!!
    }



}