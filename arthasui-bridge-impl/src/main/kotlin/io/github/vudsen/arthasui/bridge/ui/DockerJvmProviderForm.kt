package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.common.ui.CheckBoxPredicate

class DockerJvmProviderForm(oldState: JvmProviderConfig?, parentDisposable: Disposable) :
    AbstractFormComponent<JvmProviderConfig>(parentDisposable) {

    private val state: JvmInDockerProviderConfig = if (oldState is JvmInDockerProviderConfig) oldState else JvmInDockerProviderConfig(javaHome = "java")

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
                textField().bindText(state::javaHome)
                    .enabledIf(predicate)
                    .label("Java home")
                    .align(Align.FILL)
                    .comment("Replace the Java home in container")
            }
        }
        return root
    }

}