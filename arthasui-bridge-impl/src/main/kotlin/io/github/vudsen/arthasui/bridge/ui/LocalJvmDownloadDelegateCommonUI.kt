package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.components.service
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindItem
import io.github.vudsen.arthasui.api.conf.ArthasUISettingsPersistent
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.bridge.conf.LocalConnectConfig

class LocalJvmDownloadDelegateCommonUI(oldDelegateId: Long?) {

    companion object {
        private const val DISABLED = "<Disabled>"
    }

    private val transferEle: List<String>

    private var selectedLocalMachine: String?

    private val localHostMachines: List<HostMachineConfig>

    init {
        val service = service<ArthasUISettingsPersistent>()
        val transferEle = mutableListOf<String>()
        val localHostMachines = mutableListOf<HostMachineConfig>()
        var selectedLocalMachine: String? = DISABLED

        transferEle.add(DISABLED)
        for (hostMachine in service.state.hostMachines) {
            if (hostMachine.connect is LocalConnectConfig) {
                localHostMachines.add(hostMachine)
                transferEle.add(hostMachine.name)
                if (oldDelegateId == hostMachine.id) {
                    selectedLocalMachine = hostMachine.name
                }
            }
        }

        this.localHostMachines = localHostMachines
        this.selectedLocalMachine = selectedLocalMachine
        this.transferEle = transferEle
    }


    fun createComponent(panel: Panel) {
        with(panel) {
            row {
                comboBox(transferEle)
                    .bindItem(this@LocalJvmDownloadDelegateCommonUI::selectedLocalMachine)
                    .label("Transfer local file")
                    .comment("Transfer local package to remote host instead of download it in remote host")
            }
        }
    }

    fun resolveSelectedDelegateId(): Long? {
        if (selectedLocalMachine == null || selectedLocalMachine == DISABLED) {
            return null
        }
        for (hostMachine in localHostMachines) {
            if (hostMachine.name == selectedLocalMachine) {
                return hostMachine.id
            }
        }
        return null
    }

}