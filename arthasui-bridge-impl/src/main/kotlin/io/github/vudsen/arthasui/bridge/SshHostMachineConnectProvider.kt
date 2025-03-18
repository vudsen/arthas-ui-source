package io.github.vudsen.arthasui.bridge

import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import io.github.vudsen.arthasui.bridge.ui.SshConfigurationForm

class SshHostMachineConnectProvider : HostMachineConnectProvider {
    override fun getName(): String {
        return "SSH"
    }

    override fun createForm(oldEntity: HostMachineConnectConfig?): FormComponent<HostMachineConnectConfig> {
        return SshConfigurationForm(oldEntity)
    }

    override fun connect(config: HostMachineConnectConfig): CloseableHostMachine {
        return RemoteSshHostMachineImpl(config as SshHostMachineConnectConfig)
    }

    override fun getConfigClass(): Class<out HostMachineConnectConfig> {
        return SshHostMachineConnectConfig::class.java
    }

    override fun isCloseableHostMachine(): Boolean {
        return true
    }

}