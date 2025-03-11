package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComboBoxPredicate
import io.github.vudsen.arthasui.bridge.HostMachineConnectConfigUIProvider
import io.github.vudsen.arthasui.conf.HostMachineConfigV2
import java.awt.Dimension
import javax.swing.*

class CreateOrUpdateHostMachineDialogUI(oldState: HostMachineConfigV2?, private val onOk: (HostMachineConfigV2) -> Unit) : DialogWrapper(false) {

    private val state = oldState ?: HostMachineConfigV2()

    private lateinit var root: DialogPanel

    private var jvmProviderConfigUI: JvmProviderConfigUI = JvmProviderConfigUI(state.providers)

    private val connectConfigurationUIProvider = HostMachineConnectConfigUIProvider(state.connect)

    private var connectType: HostMachineConnectConfigUIProvider.Companion.ConnectType? = connectConfigurationUIProvider.getConnectType(state.connect)

    init {
        title = "Create Or Update Host Machine"
        init()
    }


    @OptIn(ExperimentalStdlibApi::class)
    override fun createCenterPanel(): JComponent {
        val root = panel {
            group("Basic Config") {
                row {
                    textField().label("Name").bindText(state::name).align(Align.FILL)
                }
            }
            lateinit var connectComboBox: ComboBox<HostMachineConnectConfigUIProvider.Companion.ConnectType>
            group("Connect Config") {
                row {
                    val box = comboBox(HostMachineConnectConfigUIProvider.Companion.ConnectType.entries).bindItem(this@CreateOrUpdateHostMachineDialogUI::connectType).label("Connect type")
                    connectComboBox = box.component
                }

                for (entry in HostMachineConnectConfigUIProvider.Companion.ConnectType.entries) {
                    row {
                        connectConfigurationUIProvider.getUI(entry).getComponent() ?.let {
                            cell(it)
                        }
                    }.visibleIf(ComboBoxPredicate(connectComboBox) { v -> v == entry })
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
            state.connect = connectConfigurationUIProvider.getUI(connectType!!).apply() ?: return
            state.providers = jvmProviderConfigUI.apply() ?: return
            super.doOKAction()
            onOk(state)
        }
    }

}