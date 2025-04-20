package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.common.validation.TextComponentValidators

class SshConfigurationForm(oldState: HostMachineConnectConfig?, parentDisposable: Disposable) : AbstractFormComponent<HostMachineConnectConfig>(parentDisposable) {

    private val state: SshHostMachineConnectConfig = if (oldState is SshHostMachineConnectConfig) {
        oldState
    } else {
        SshHostMachineConnectConfig("")
    }

    override fun getState(): HostMachineConnectConfig {
        return state
    }

    override fun createDialogPanel(): DialogPanel {
        return panel {
            group("Connection Config") {
                row {
                    comment("Only Linux is supported!")
                }
                val textComponentValidators = TextComponentValidators()
                row("host") {
                    textField().bindText(state.ssh::host).align(Align.FILL).validationOnApply(textComponentValidators)
                }
                row("port") {
                    textField().bindIntText(state.ssh::port).align(Align.FILL).validationOnApply(textComponentValidators)
                }
                row("username") {
                    textField().bindText(state.ssh::username).align(Align.FILL).validationOnApply(
                        textComponentValidators
                    )
                }
                row("password") {
                    passwordField().bindText(state.ssh::password).validationOnApply(textComponentValidators)
                        .comment("Warning: Your password is stored in a non-encrypted format on your local device.")
                        .align(Align.FILL)
                }
            }
        }
    }



}