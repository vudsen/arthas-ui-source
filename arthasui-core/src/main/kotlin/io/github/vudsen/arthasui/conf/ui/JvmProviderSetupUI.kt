package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.common.util.MessagesUtils
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel

private typealias Components = Pair<String, FormComponent<JvmProviderConfig>>

class JvmProviderSetupUI(private val parentDisposable: Disposable) {

    companion object {
        private val logger = Logger.getInstance(JvmProviderSetupUI::class.java)
    }

    private lateinit var loadingDecorator: LoadingDecorator

    private lateinit var container: JComponent

    private val formTabs: MutableList<FormComponent<JvmProviderConfig>> = mutableListOf()

    private val okColor = JBColor.namedColor("Label.foreground")

    private val errorColor = JBColor.namedColor("Label.errorForeground")

    private var tabbedPane: JBTabbedPane? = null

    private var commonDialogPanel: DialogPanel? = null

    private val state: HostMachineConfig = HostMachineConfig()

    /**
     * 重新创建 [container] 中所有的组件
     * @param hostMachine 宿主机
     * @param components 所有的组件。key: 组件名称，value: 组件实例
     */
    private fun recreateContainer(hostMachine: HostMachine, components: List<Components>) {
        container.removeAll()

        if (hostMachine is ShellAvailableHostMachine) {
            container.add(createCommonConfigUI().apply {
                commonDialogPanel = this@apply
                maximumSize = Dimension(Integer.MAX_VALUE, this@apply.preferredSize.height)
            })
        }
        container.add(JBTabbedPane().apply {
            tabbedPane = this@apply
            formTabs.clear()
            for (component in components) {
                addTab(component.first, component.second.getComponent())
                formTabs.add(component.second)
            }
            maximumSize = Dimension(Integer.MAX_VALUE, this@apply.preferredSize.height)
        })
        container.updateUI()
    }

    fun getComponent(): JComponent {
        val container = JPanel().apply {
            layout = BoxLayout(this@apply, BoxLayout.Y_AXIS)
        }
        val decorator = LoadingDecorator(container, parentDisposable, 0)

        this.container = container
        loadingDecorator = decorator
        return decorator.component
    }

    private fun createCommonConfigUI(): DialogPanel {
        return panel {
            group("Basic Configuration") {
                row {
                    textField().bindText(state::dataDirectory).label("Data directory").align(AlignX.FILL)
                }
            }
        }
    }

    private fun transState(newState: HostMachineConfig) {
        state.dataDirectory = newState.dataDirectory
        state.connect = newState.connect
        state.providers = newState.providers
        state.id = newState.id
        state.name = newState.name
        state.searchGroups = newState.searchGroups
    }

    /**
     * 刷新组件状态
     */
    fun refresh(hostMachine: HostMachine) {
        loadingDecorator.startLoading(false)

        transState(hostMachine.getHostMachineConfig())

        if (hostMachine is ShellAvailableHostMachine) {
            state.dataDirectory = hostMachine.resolveDefaultDataDirectory()
        }

        val components = mutableListOf<Components>()
        val providers = service<JvmProviderManager>().getProviders()
        for (provider in providers) {
            if (provider.isSupport(hostMachine)) {
                val configuration = provider.tryCreateDefaultConfiguration(hostMachine)
                val form = provider.createForm(configuration, parentDisposable)
                components.add(Pair(provider.getName(), form))
            }
        }

        ApplicationManager.getApplication().invokeLater {
            try {
                recreateContainer(hostMachine, components)
                loadingDecorator.stopLoading()
            } catch (e: Exception) {
                MessagesUtils.showErrorMessageLater("Auto Detect Jvm Provider Failed", e.message, null)
            }
        }
    }

    fun apply(): HostMachineConfig? {
        commonDialogPanel?.apply()
        val result = mutableListOf<JvmProviderConfig>()
        for (i in formTabs.indices) {
            val formTab = formTabs[i]
            formTab.apply()?.let {
                result.add(it)
                tabbedPane?.getTabComponentAt(i)?.foreground = okColor
            } ?: let {
                tabbedPane?.getTabComponentAt(i)?.foreground = errorColor
            }

        }
        return if (result.size == formTabs.size) {
            state.providers = result
            state
        } else {
            null
        }
    }


    fun isInvalid(): Boolean {
        var result = false
        for (i in formTabs.indices) {
            val formTab = formTabs[i]
            formTab.apply()?.let {
                tabbedPane?.getTabComponentAt(i)?.foreground = okColor
            } ?: let {
                tabbedPane?.getTabComponentAt(i)?.foreground = errorColor
                result = true
            }
        }
        return result
    }

}