package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComboBoxPredicate
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.common.validation.TextComponentValidators
import io.github.vudsen.arthasui.conf.HostMachineConfig
import java.awt.Dimension
import javax.swing.*

class UpdateHostMachineDialogUI(
    oldState: HostMachineConfig?,
    private val parentDisposable: Disposable,
    private val onOk: (HostMachineConfig) -> Unit,
) : DialogWrapper(false) {

    private val state = oldState ?: HostMachineConfig()

    private val isCreate = oldState == null

    private lateinit var root: DialogPanel

    private var jvmProviderConfigUI: JvmProviderConfigUI = JvmProviderConfigUI(state.providers, parentDisposable)

    private val providers: List<HostMachineConnectProvider>

    private val formMap = mutableMapOf<String, FormComponent<HostMachineConnectConfig>>()

    private var connectType: String? = null

    init {
        title = "Create Or Update Host Machine"
        val manager = service<HostMachineConnectManager>()
        providers = manager.getProviders()
        oldState ?.let {
            connectType = manager.getProvider(oldState.connect).getName()
        } ?:let {
            connectType = providers[0].getName()
        }

        init()
    }

    override fun createCenterPanel(): JComponent {
        val root = panel {
            group("Basic Config") {
                row {
                    textField()
                        .label("Name")
                        .bindText(state::name)
                        .align(Align.FILL)
                        .validationOnApply(TextComponentValidators())
                        .enabled(isCreate)
                }
            }
            lateinit var connectComboBox: ComboBox<String>
            group("Connect Config") {
                row {
                    val box =
                        comboBox(providers.map { pv -> pv.getName() }).bindItem(this@UpdateHostMachineDialogUI::connectType)
                            .label("Connect type")
                    connectComboBox = box.component
                }

                for (provider in providers) {
                    row {
                        val form = provider.createForm(state.connect, parentDisposable)
                        formMap[provider.getName()] = form
                        cell(form.getComponent())
                    }.visibleIf(ComboBoxPredicate(connectComboBox) { v -> v == provider.getName() })
                }
            }
            group("Provider Config") {
                row {
                    cell(jvmProviderConfigUI.getComponent()).align(Align.FILL)
                }
            }
        }
        root.registerValidators(parentDisposable)
        this.root = root
        val pane = JBScrollPane(
            root,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        )
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