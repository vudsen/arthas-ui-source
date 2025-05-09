package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComboBoxPredicate
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.common.validation.TextComponentValidators
import io.github.vudsen.arthasui.api.conf.HostMachineConfig

class JvmConnectSetupUI(parentDisposable: Disposable) : AbstractFormComponent<HostMachineConfig>(parentDisposable) {

    private val formMap = mutableMapOf<String, FormComponent<HostMachineConnectConfig>>()

    private val state = HostMachineConfig(System.currentTimeMillis())

    private var connectType: String? = null

    private val connectProviders: List<HostMachineConnectProvider>

    init {
        val manager = service<HostMachineConnectManager>()
        connectProviders = manager.getProviders()
        connectType = connectProviders[0].getName()
    }

    override fun getState(): HostMachineConfig {
        return state
    }

    override fun createDialogPanel(): DialogPanel {
        val panel = panel {
            lateinit var connectComboBox: ComboBox<String>
            group("Basic Config") {
                row {
                    textField().label("Name").validationOnApply(TextComponentValidators()).bindText(state::name).align(Align.FILL)
                }
                row {
                    val box =
                        comboBox(connectProviders.map { pv -> pv.getName() }).bindItem(this@JvmConnectSetupUI::connectType)
                            .label("Connect type")
                    connectComboBox = box.component
                }

            }

            for (provider in connectProviders) {
                row {
                    val form = provider.createForm(state.connect, parentDisposable)
                    formMap[provider.getName()] = form
                    cell(form.getComponent()).align(Align.FILL)
                }.visibleIf(ComboBoxPredicate(connectComboBox) { v -> v == provider.getName() })
            }
        }
        return panel
    }

    override fun apply(): HostMachineConfig? {
        val dialogPanel = panel ?: return null
        dialogPanel.apply()
        if (dialogPanel.validateAll().isNotEmpty()) {
            return null
        }
        state.connect = formMap[connectType]!!.apply() ?: return null
        return state
    }

}