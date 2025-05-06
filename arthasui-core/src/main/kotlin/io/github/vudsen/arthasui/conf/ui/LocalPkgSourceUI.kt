package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.panel
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig
import io.github.vudsen.arthasui.api.conf.ArthasUISettingsPersistent

class LocalPkgSourceUI(state: Long?, parentDisposable: Disposable) : AbstractFormComponent<Long>(parentDisposable) {

    companion object {
        private const val DISABLED = "<Disabled>"
        const val DISABLED_VALUE = -1L
    }

    private val transferEle: List<String>

    private var selectedLocalMachine: String?

    private val localHostMachines: List<HostMachineConfig>

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
                if (state == hostMachine.id) {
                    selectedLocalMachine = hostMachine.name
                }
            }
        }
        this.selectedLocalMachine = selectedLocalMachine
        this.transferEle = transferEle
        this.localHostMachines = localHostMachines
    }



    override fun getState(): Long {
        if (selectedLocalMachine == null || selectedLocalMachine == DISABLED) {
            return DISABLED_VALUE
        } else {
            val i = transferEle.indexOf(selectedLocalMachine)
            if (i < 0) {
                throw IllegalStateException("Unreachable code.")
            }
            return localHostMachines[i - 1].id
        }
    }

    override fun createDialogPanel(): DialogPanel {
        return panel {
            row {
                comboBox(transferEle)
                    .bindItem(this@LocalPkgSourceUI::selectedLocalMachine)
                    .label("Transfer local file")
                    .comment("Transfer local package to remote host instead of download it in remote host")
            }
        }
    }


}