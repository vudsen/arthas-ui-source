package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.bridge.conf.TunnelServerProviderConfig

class TunnelServerProviderForm(oldState: JvmProviderConfig?, parentDisposable: Disposable) : AbstractFormComponent<JvmProviderConfig>(parentDisposable)  {

    private val state: TunnelServerProviderConfig = oldState as? TunnelServerProviderConfig
        ?: TunnelServerProviderConfig(false)


    override fun getState(): JvmProviderConfig {
        return state
    }

    override fun createDialogPanel(): DialogPanel {
        return panel {
            row {
                checkBox("Enable").bindSelected(state::enabled)
            }
        }
    }

}