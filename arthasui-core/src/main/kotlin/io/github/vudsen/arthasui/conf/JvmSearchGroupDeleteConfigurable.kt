package io.github.vudsen.arthasui.conf

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.bean.JvmSearchGroup
import io.github.vudsen.arthasui.api.conf.ArthasUISettingsPersistent
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class JvmSearchGroupDeleteConfigurable(private val project: Project,
                                       private val hostMachineConfig: HostMachineConfig,
                                       private val jvmSearchGroup: JvmSearchGroup
) : Configurable {

    override fun createComponent(): JComponent {
        return JPanel(FlowLayout()).apply {
            add(JLabel("Are you sure you want to delete this group?"))
        }
    }

    override fun isModified(): Boolean {
        return true
    }

    override fun apply() {
        val persistent = service<ArthasUISettingsPersistent>()
        val target = persistent.state.hostMachines.find { config -> config == hostMachineConfig } ?: return
        val newList = mutableListOf<JvmSearchGroup>()
        for (searchGroup in target.searchGroups) {
            if (searchGroup.name !== jvmSearchGroup.name) {
                newList.add(searchGroup)
            }
        }
        target.searchGroups = newList
        persistent.notifyStateUpdated()
    }

    override fun getDisplayName(): String {
        return "Delete Search Group"
    }

}