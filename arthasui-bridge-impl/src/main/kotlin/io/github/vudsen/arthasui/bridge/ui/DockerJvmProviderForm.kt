package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.common.ui.CheckBoxPredicate

class DockerJvmProviderForm(oldState: JvmProviderConfig?) :
    AbstractFormComponent<JvmProviderConfig>() {

    private val state: JvmInDockerProviderConfig = if (oldState is JvmInDockerProviderConfig) oldState else JvmInDockerProviderConfig()

    override fun getState(): JvmProviderConfig {
        return state
    }

    override fun createDialogPanel(): DialogPanel {
        lateinit var predicate: CheckBoxPredicate
        val root = panel {
            row {
                val checkBox = checkBox("Enable").bindSelected(state::enabled)
                checkBox.comment("Use the jvm in your docker container")
                predicate = CheckBoxPredicate(checkBox, state.enabled)
            }
            row {
                checkBox("Use tools in container").enabledIf(predicate).bindSelected(state::useToolsInContainer)
            }
            row {
                textField().bindText(state::jdkHome).enabledIf(predicate).label("Jdk home").align(Align.FILL)
            }
            row {
                textField().bindText(state::arthasHome).enabledIf(predicate).label("Arthas home").align(Align.FILL)
            }
        }
        return root
    }

}