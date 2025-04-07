package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.ComboBoxPredicate
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.common.validation.TextComponentValidators
import io.github.vudsen.arthasui.conf.HostMachineConfig

class JvmConnectSetupUI(parentDisposable: Disposable) : AbstractFormComponent<HostMachineConfig>(parentDisposable) {

    private val formMap = mutableMapOf<String, FormComponent<HostMachineConnectConfig>>()

    private val state = HostMachineConfig()

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
        return panel {
            row {
                textField().label("Name").validationOnApply(TextComponentValidators())
            }
            lateinit var connectComboBox: ComboBox<String>
            row {
                val box =
                    comboBox(connectProviders.map { pv -> pv.getName() }).bindItem(this@JvmConnectSetupUI::connectType)
                        .label("Connect type")
                connectComboBox = box.component
            }

            for (provider in connectProviders) {
                row {
                    val form = provider.createForm(state.connect, parentDisposable)
                    formMap[provider.getName()] = form
                    cell(form.getComponent())
                }.visibleIf(ComboBoxPredicate(connectComboBox) { v -> v == provider.getName() })
            }
        }
    }

}