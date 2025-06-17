package io.github.vudsen.arthasui.bridge.providers

import com.intellij.openapi.Disposable
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.bean.UIContext
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.host.LocalHostMachineImpl
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig
import io.github.vudsen.arthasui.bridge.factory.ToolChainManagerUtil
import io.github.vudsen.arthasui.bridge.ui.LocalConnectConfigurationForm

class LocalHostMachineConnectProvider : HostMachineConnectProvider {
    override fun getName(): String {
        return "Local"
    }

    override fun createForm(
        oldEntity: HostMachineConnectConfig?,
        parentDisposable: Disposable
    ): FormComponent<HostMachineConnectConfig> {
        return LocalConnectConfigurationForm(parentDisposable)
    }



    override fun connect(config: HostMachineConfig): HostMachine {
        return LocalHostMachineImpl(config)
    }

    override fun getConfigClass(): Class<out HostMachineConnectConfig> {
        return LocalConnectConfig::class.java
    }

    override fun isCloseableHostMachine(): Boolean {
        return false
    }

}