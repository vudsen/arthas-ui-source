package io.github.vudsen.arthasui.bridge.providers

import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.host.LocalHostMachineImpl
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig
import io.github.vudsen.arthasui.bridge.ui.LocalConnectConfigurationForm

class LocalHostMachineConnectProvider : HostMachineConnectProvider {
    override fun getName(): String {
        return "Local"
    }

    override fun createForm(oldEntity: HostMachineConnectConfig?): FormComponent<HostMachineConnectConfig> {
        return LocalConnectConfigurationForm()
    }

    override fun connect(config: HostMachineConnectConfig): HostMachine {
        return LocalHostMachineImpl()
    }

    override fun getConfigClass(): Class<out HostMachineConnectConfig> {
        return LocalConnectConfig::class.java
    }

    override fun isCloseableHostMachine(): Boolean {
        return false
    }

}