package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComboBoxPredicate
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectRegistry
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.conf.HostMachineConfigV2
import java.awt.Dimension
import javax.swing.*

class CreateOrUpdateHostMachineDialogUI(oldState: HostMachineConfigV2?, private val onOk: (HostMachineConfigV2) -> Unit) : DialogWrapper(false) {

    private val state = oldState ?: HostMachineConfigV2()

    private lateinit var root: DialogPanel

    private var jvmProviderConfigUI: JvmProviderConfigUI = JvmProviderConfigUI(state.providers)

    private val providers = service<HostMachineConnectRegistry>().getProviders()

    private val formMap = mutableMapOf<String, FormComponent<HostMachineConnectConfig>>()

    private var connectType: String? = providers[0].getName()

    init {
        title = "Create Or Update Host Machine"
        init()
    }

    override fun createCenterPanel(): JComponent {
        val root = panel {
            group("Basic Config") {
                row {
                    textField().label("Name").bindText(state::name).align(Align.FILL)
                }
            }
            lateinit var connectComboBox: ComboBox<String>
            group("Connect Config") {
                row {
                    val box = comboBox(providers.map { pv -> pv.getName() }).bindItem(this@CreateOrUpdateHostMachineDialogUI::connectType).label("Connect type")
                    connectComboBox = box.component
                }

                for (provider in providers) {
                    row {
                        val form = provider.createForm(state.connect)
                        formMap[provider.getName()] = form
                        cell(form.getComponent())
                    }.visibleIf(ComboBoxPredicate(connectComboBox) { v -> v == provider.getName() })
                }
            }
            row {
                cell(jvmProviderConfigUI.getComponent()).align(Align.FILL)
            }
        }
        root.registerValidators {}
        this.root = root
        val pane = JBScrollPane(root, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER)
        pane.preferredSize = Dimension(500, 500)
        pane.border = BorderFactory.createEmptyBorder()
        return pane
    }



    override fun doOKAction() {
        root.apply()
        if (root.validateAll().isEmpty()) {
            state.connect = formMap[connectType!!]!!.apply() ?: return
            state.providers = jvmProviderConfigUI.apply() ?: return
            super.doOKAction()
            onOk(state)
        }
    }

}