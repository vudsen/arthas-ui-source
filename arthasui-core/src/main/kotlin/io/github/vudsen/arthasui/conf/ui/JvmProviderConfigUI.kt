package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.util.ui.CheckBoxPredicate
import javax.swing.JComponent

class JvmProviderConfigUI(oldStates: List<JvmProviderConfig>): FormComponent<MutableList<JvmProviderConfig>> {


    companion object {
        enum class ProviderType {
            LOCAL,
            DOCKER
        }

        class ProviderHolder(
            var enabled: Boolean,
            var provider: JvmProviderConfig
        )
    }

    private val stateMap: MutableMap<ProviderType, ProviderHolder> = HashMap(oldStates.size)

    private lateinit var root: DialogPanel

    init {
        for (oldState in oldStates) {
            if (oldState is LocalJvmProviderConfig) {
                stateMap[ProviderType.LOCAL] = ProviderHolder(true, oldState.copy())
            } else if (oldState is JvmInDockerProviderConfig) {
                stateMap[ProviderType.DOCKER] = ProviderHolder(true, oldState.copy())
            }
        }

        stateMap.putIfAbsent(ProviderType.LOCAL, ProviderHolder(false, LocalJvmProviderConfig()))
        stateMap.putIfAbsent(ProviderType.DOCKER, ProviderHolder(false, JvmInDockerProviderConfig()))
    }


    override fun getComponent(): JComponent {
        val root = panel {
            group("Jvm Provider Config") {
                group("Local") {
                    val state = stateMap[ProviderType.LOCAL]!!
                    val provider = state.provider as LocalJvmProviderConfig
                    lateinit var predicate: CheckBoxPredicate
                    row {
                        val checkbox = checkBox("Enable").bindSelected(state::enabled)
                        checkbox.comment("Use the jvm in your local host machine")
                        predicate = CheckBoxPredicate(checkbox, state.enabled)
                    }
                    row {
                        textField().bindText(provider::jdkHome).enabledIf(predicate).label("Jdk home").align(Align.FILL)
                    }
                    row {
                        textField().bindText(provider::arthasHome).enabledIf(predicate).label("Arthas home").align(Align.FILL)
                    }
                }
                group("Docker") {
                    val state = stateMap[ProviderType.DOCKER]!!
                    val provider = state.provider as JvmInDockerProviderConfig
                    lateinit var predicate: CheckBoxPredicate
                    row {
                        val checkBox = checkBox("Enable").bindSelected(state::enabled)
                        checkBox.comment("Use the jvm in your docker container")
                        predicate = CheckBoxPredicate(checkBox, state.enabled)
                    }
                    row {
                        checkBox("Use tools in container").enabledIf(predicate).bindSelected(provider::useToolsInContainer)
                    }
                    row {
                        textField().bindText(provider::jdkHome).enabledIf(predicate).label("Jdk home").align(Align.FILL)
                    }
                    row {
                        textField().bindText(provider::arthasHome).enabledIf(predicate).label("Arthas home").align(Align.FILL)
                    }
                }
            }
        }
        this.root = root
        return root
    }

    override fun apply(): MutableList<JvmProviderConfig>? {
        root.apply()
        if (root.validateAll().isNotEmpty()) {
            return null
        }
        val r = mutableListOf<JvmProviderConfig>()
        for (entry in stateMap.entries) {
            if (!entry.value.enabled) {
                continue
            }
            r.add(entry.value.provider)
        }
        return r
    }

}