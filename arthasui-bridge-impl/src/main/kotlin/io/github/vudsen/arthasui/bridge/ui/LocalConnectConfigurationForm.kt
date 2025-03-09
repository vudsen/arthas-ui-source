package io.github.vudsen.arthasui.bridge.ui

import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig
import javax.swing.JComponent

class LocalConnectConfigurationForm : FormComponent<HostMachineConnectConfig> {
    override fun getComponent(): JComponent? {
        return null
    }

    override fun apply(): LocalConnectConfig {
        return LocalConnectConfig()
    }
}