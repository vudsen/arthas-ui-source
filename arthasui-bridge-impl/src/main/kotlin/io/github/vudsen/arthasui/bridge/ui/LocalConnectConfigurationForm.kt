package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.panel
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig
import javax.swing.JComponent
import javax.swing.JPanel

class LocalConnectConfigurationForm(parentDisposable: Disposable) : AbstractFormComponent<HostMachineConnectConfig>(parentDisposable) {

    override fun getState(): HostMachineConnectConfig {
        return LocalConnectConfig()
    }

    override fun createDialogPanel(): DialogPanel {
        return panel {  }
    }

}