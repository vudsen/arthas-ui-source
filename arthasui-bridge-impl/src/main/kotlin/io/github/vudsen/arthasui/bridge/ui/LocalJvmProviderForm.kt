package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.common.ui.CheckBoxPredicate
import io.github.vudsen.arthasui.common.validation.TextComponentValidators

class LocalJvmProviderForm(oldState: JvmProviderConfig?, parentDisposable: Disposable) : AbstractFormComponent<JvmProviderConfig>(parentDisposable) {


    private val state: LocalJvmProviderConfig = if (oldState is LocalJvmProviderConfig) oldState else LocalJvmProviderConfig()

    override fun getState(): JvmProviderConfig {
        return state
    }

    override fun createDialogPanel(): DialogPanel {
        return panel {
            lateinit var predicate: CheckBoxPredicate
            row {
                val checkbox = checkBox("Enable").bindSelected(state::enabled)
                checkbox.comment("Use the jvm in your local host machine")
                predicate = CheckBoxPredicate(checkbox, state.enabled)
            }
            row {
                textField().bindText(state::javaHome).enabledIf(predicate).label("Java home").align(Align.FILL).validationOnApply(TextComponentValidators()).comment("Jre is available too.")
            }
        }
    }


}