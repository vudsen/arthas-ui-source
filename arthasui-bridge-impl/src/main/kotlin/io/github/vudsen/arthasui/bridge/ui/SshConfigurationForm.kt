package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.api.ui.FormComponent
import javax.swing.JComponent

class SshConfigurationForm(oldState: HostMachineConnectConfig?) : AbstractFormComponent<HostMachineConnectConfig>() {

    private val state: SshHostMachineConnectConfig = if (oldState is SshHostMachineConnectConfig) {
        oldState
    } else {
        SshHostMachineConnectConfig()
    }

    private val osTypeObservableMutableProperty = object : ObservableMutableProperty<OS> {
        override fun set(value: OS) {
            state.os = value
        }

        override fun afterChange(listener: (OS) -> Unit) {}

        override fun get(): OS {
            return state.os
        }
    }

    override fun getState(): HostMachineConnectConfig {
        return state
    }

    override fun createDialogPanel(): DialogPanel {
        return panel {
            group("Connection Config") {
                row {
                    label("Os type")
                    comboBox(OS.values().toList()).bindItem(osTypeObservableMutableProperty)
                }
                row("host") {
                    textField().bindText(state.ssh::host).align(Align.FILL)
                }
                row("port") {
                    textField().bindIntText(state.ssh::port).align(Align.FILL)
                }
                row("username") {
                    textField().bindText(state.ssh::username).align(Align.FILL)
                }
                row("password") {
                    passwordField().bindText(state.ssh::password)
                        .comment("Warning: Your password is stored in a non-encrypted format on your local device.")
                        .align(Align.FILL)
                }
            }
        }
    }



}