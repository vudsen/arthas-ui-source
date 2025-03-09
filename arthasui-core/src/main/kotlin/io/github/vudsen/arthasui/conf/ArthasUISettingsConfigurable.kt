package io.github.vudsen.arthasui.conf

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import io.github.vudsen.arthasui.conf.ui.RootConfigUI
import javax.swing.JComponent

class ArthasUISettingsConfigurable(private val project: Project) : Configurable {

    private var lastUI: RootConfigUI? = null

    override fun createComponent(): JComponent {
        val ui = RootConfigUI(project)
        lastUI = ui
        return ui.component()
    }

    override fun isModified(): Boolean {
        val state = lastUI ?: return false
        return state.isModified()
    }

    override fun apply() {
        val ui = lastUI ?: return
        ui.component().apply()
        ui.resetModifiedStatus()

        val service = project.getService(ArthasUISettingsPersistent::class.java)

        service.state.hostMachines = ui.settingState.hostMachines
    }

    override fun getDisplayName(): String {
        return "Arthas UI Settings"
    }
}