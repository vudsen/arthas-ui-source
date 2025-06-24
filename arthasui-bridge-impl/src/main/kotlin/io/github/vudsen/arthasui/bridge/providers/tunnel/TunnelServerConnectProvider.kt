package io.github.vudsen.arthasui.bridge.providers.tunnel

import com.intellij.openapi.Disposable
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.bean.UIContext
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.conf.TunnelServerConnectConfig
import io.github.vudsen.arthasui.bridge.host.TunnelServerHostMachine
import io.github.vudsen.arthasui.bridge.ui.TunnelServerConnectForm

class TunnelServerConnectProvider : HostMachineConnectProvider {

    override fun getName(): String {
        return "Tunnel Server"
    }

    override fun createForm(
        oldEntity: HostMachineConnectConfig?,
        parentDisposable: Disposable
    ): FormComponent<HostMachineConnectConfig> {
        return TunnelServerConnectForm(oldEntity, parentDisposable)
    }


    override fun connect(config: HostMachineConfig): HostMachine {
        return TunnelServerHostMachine(config)
    }

    override fun getConfigClass(): Class<out HostMachineConnectConfig> {
        return TunnelServerConnectConfig::class.java
    }


    override fun getConnectionClassForLazyLoad(): Class<out HostMachine>? {
        return null
    }
}