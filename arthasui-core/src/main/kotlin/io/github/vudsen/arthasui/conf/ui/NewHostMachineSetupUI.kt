package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBSlidingPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindItem
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.ComboBoxPredicate
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.common.validation.TextComponentValidators
import io.github.vudsen.arthasui.conf.HostMachineConfig
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.JButton
import javax.swing.JComponent

class NewHostMachineSetupUI(private val parentDisposable: Disposable,
                            private val onOk: (HostMachineConfig) -> Unit)
    : DialogWrapper(false) {

    private val state = HostMachineConfig()

    private val formMap = mutableMapOf<String, FormComponent<HostMachineConnectConfig>>()

    private var connectType: String? = null

    private val connectProviders: List<HostMachineConnectProvider>

    private var jvmProviderConfigUI: JvmProviderConfigUI = JvmProviderConfigUI(state.providers, parentDisposable)

    private var currentIndex = 0

    private lateinit var root: JBSlidingPanel

    private lateinit var myBackAction: Action

    private lateinit var myNextAction: Action

    private val tabCount = 2

    init {
        title = "New Host Machine"
        val manager = service<HostMachineConnectManager>()
        connectProviders = manager.getProviders()
        connectType = connectProviders[0].getName()
        setOKButtonText("Next")
        setCancelButtonText("Back")
        createSouthPanel()
        init()
    }


    /**
     * 设置宿主机名称和连接方式
     */
    private fun page1(): JComponent {
        return panel {
            row {
                textField().label("Name").validationOnApply(TextComponentValidators())
            }
            lateinit var connectComboBox: ComboBox<String>
            row {
                val box =
                    comboBox(connectProviders.map { pv -> pv.getName() }).bindItem(this@NewHostMachineSetupUI::connectType)
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

    /**
     * 设置 jvm providers
     */
    private fun page2(): JComponent {
        return panel {
            row {
                cell(jvmProviderConfigUI.getComponent()).align(Align.FILL)
            }
        }
    }


    override fun createCenterPanel(): JComponent {
        val panel = JBSlidingPanel()
        panel.add("New Host Machine", page1())
        panel.add("Search Locations", page2())
        root = panel
        return panel
    }

    override fun createDefaultActions() {
        super.createDefaultActions()
    }


    override fun createActions(): Array<Action> {
        myBackAction = object : DialogWrapperAction("Back") {

            override fun doAction(p0: ActionEvent?) {
                if (currentIndex > 0) {
                    currentIndex--
                    root.goLeft()
                }
                if (currentIndex == 0) {
                    val source = (p0?.source ?: return) as JButton
                    source.isEnabled = false
                    source.updateUI()
                }
                okAction.isEnabled = true
            }
        }

        myNextAction = object : DialogWrapperAction("Next") {

            init {
                putValue("DefaultAction", true)
            }

            override fun doAction(p0: ActionEvent?) {
                if (currentIndex < tabCount - 1) {
                    currentIndex++
                    root.goRight()
                }
                val source = (p0?.source ?: return) as JButton
                if (currentIndex == tabCount - 1) {
                    putValue(NAME, "Create")
                } else {
                    putValue(NAME, "Next")
                }
                source.updateUI()
                okAction.isEnabled = true

            }
        }

        return arrayOf(myBackAction, myNextAction)
    }

    override fun doOKAction() {
        if (currentIndex < 2) {
            currentIndex++
            root.goRight()
        }
        if (currentIndex == 2) {
            okAction.isEnabled = false
        }
        myBackAction.isEnabled = true
        super.repaint()
//        super.doOKAction()
//        onOk(state)
    }

}