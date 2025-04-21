package io.github.vudsen.arthasui.conf

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import io.github.vudsen.arthasui.conf.ui.RootConfigUI
import javax.swing.JComponent

class ArthasUISettingsConfigurable() : Configurable {

    private var lastUI: RootConfigUI? = null

    override fun createComponent(): JComponent {
        val ui = RootConfigUI()
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

        service<ArthasUISettingsPersistent>().updateState(ui.settingState)
    }

    override fun getDisplayName(): String {
        return "Arthas UI Settings"
    }

    override fun disposeUIResources() {
        lastUI?.let { Disposer.dispose(it) }
    }

}