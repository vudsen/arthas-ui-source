package io.github.vudsen.arthasui.bridge.providers

import com.intellij.openapi.Disposable
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.ui.FormComponent

class TunnelServerConnectProvider : HostMachineConnectProvider {

    override fun getName(): String {
        return "Tunnel Server"
    }

    override fun createForm(
        oldEntity: HostMachineConnectConfig?,
        parentDisposable: Disposable
    ): FormComponent<HostMachineConnectConfig> {
        TODO("Not yet implemented")
    }

    override fun connect(config: HostMachineConnectConfig): HostMachine {
        TODO("Not yet implemented")
    }

    override fun getConfigClass(): Class<out HostMachineConnectConfig> {
        TODO("Not yet implemented")
    }

    override fun isCloseableHostMachine(): Boolean {
        return false
    }
}