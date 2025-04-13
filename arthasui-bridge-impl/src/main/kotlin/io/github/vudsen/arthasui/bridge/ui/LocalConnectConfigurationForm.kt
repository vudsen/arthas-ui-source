package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig

class LocalConnectConfigurationForm(parentDisposable: Disposable) : AbstractFormComponent<HostMachineConnectConfig>(parentDisposable) {

    private var dataDirectory: String = ""

    override fun getState(): HostMachineConnectConfig {
        return LocalConnectConfig(dataDirectory)
    }

    override fun createDialogPanel(): DialogPanel {
        return panel {
            group("Local Config") {
                row {
                    textField()
                        .bindText(this@LocalConnectConfigurationForm::dataDirectory)
                        .label("Data directory")
                        .comment("The place to save the downloaded toolchain(Optional)")
                        .align(Align.FILL)
                }
            }
        }
    }

}