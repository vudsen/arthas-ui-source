package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import io.github.vudsen.arthasui.bridge.conf.SshHostMachineConnectConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.conf.ArthasUISettingsPersistent
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig
import io.github.vudsen.arthasui.common.validation.TextComponentValidators

class SshConfigurationForm(oldState: HostMachineConnectConfig?, parentDisposable: Disposable) :
    AbstractFormComponent<HostMachineConnectConfig>(parentDisposable) {


    companion object {
        private const val DISABLED = "<Disabled>"
    }

    private val transferEle: List<String>

    private var selectedLocalMachine: String?

    private val localHostMachines: List<HostMachineConfig>

    private val state: SshHostMachineConnectConfig = if (oldState is SshHostMachineConnectConfig) {
        oldState
    } else {
        SshHostMachineConnectConfig("")
    }

    init {
        val service = service<ArthasUISettingsPersistent>()
        val localHostMachines = mutableListOf<HostMachineConfig>()
        var selectedLocalMachine: String? = DISABLED

        val transferEle = mutableListOf<String>()
        transferEle.add(DISABLED)
        for (hostMachine in service.state.hostMachines) {
            if (hostMachine.connect is LocalConnectConfig) {
                localHostMachines.add(hostMachine)
                transferEle.add(hostMachine.name)
                if (state.localPkgSourceId == hostMachine.id) {
                    selectedLocalMachine = hostMachine.name
                }
            }
        }
        this.selectedLocalMachine = selectedLocalMachine
        this.transferEle = transferEle
        this.localHostMachines = localHostMachines
    }


    override fun getState(): HostMachineConnectConfig {
        if (selectedLocalMachine == null || selectedLocalMachine == DISABLED) {
            state.localPkgSourceId = null
        } else {
            val i = transferEle.indexOf(selectedLocalMachine)
            if (i < 0) {
                throw IllegalStateException("Unreachable code.")
            }
            state.localPkgSourceId = localHostMachines[i - 1].id
        }
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
            group("Feature") {
                row {
                    comboBox(transferEle)
                        .bindItem(this@SshConfigurationForm::selectedLocalMachine)
                        .label("Transfer local file")
                        .comment("Transfer local package to remote host instead of download it in remote host")
                }
            }
        }
    }



}