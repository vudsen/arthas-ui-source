package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.openapi.ui.Messages
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTabbedPane
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.common.util.MessagesUtils
import io.github.vudsen.arthasui.common.util.collectStackTrace
import javax.swing.JComponent

class JvmProviderSetupUI(private val parentDisposable: Disposable, private val project: Project)  {

    companion object {
        private  val logger = Logger.getInstance(JvmProviderSetupUI::class.java)
    }

    private lateinit var loadingDecorator: LoadingDecorator

    private lateinit var tabbedPane: JBTabbedPane

    private val formTabs: MutableList<FormComponent<JvmProviderConfig>> = mutableListOf()

    private val okColor = JBColor.namedColor("Label.foreground")

    private val errorColor = JBColor.namedColor("Label.errorForeground")


    fun getComponent(): JComponent {
        val root = JBTabbedPane()
        val decorator = LoadingDecorator(root, parentDisposable, 0)
        loadingDecorator = decorator
        tabbedPane = root
        return decorator.component
    }

    /**
     * 刷新组件状态
     */
    fun refresh(hostMachine: HostMachine) {
        loadingDecorator.startLoading(false)
        ProgressManager.getInstance().run(object : Task.Modal(project, "Auto Detecting Jvm Provider", true)  {

            override fun run(indicator: ProgressIndicator) {
                try {
                    val tabs = mutableListOf<FormComponent<JvmProviderConfig>>()
                    val providers = service<JvmProviderManager>().getProviders()
                    val update: Boolean = tabbedPane.tabCount == providers.size
                    formTabs.clear()
                    for ((index, provider) in providers.withIndex()) {
                        val configuration = provider.tryCreateDefaultConfiguration(hostMachine)
                        val form = provider.createForm(configuration, parentDisposable)
                        tabs.add(form)
                        if (update) {
                            tabbedPane.setTabComponentAt(index, form.getComponent())
                        } else {
                            tabbedPane.addTab(provider.getName(), form.getComponent())
                        }
                        formTabs.add(form)
                    }
                    tabbedPane.updateUI()
                    loadingDecorator.stopLoading()
                } catch (e: Exception) {
                    loadingDecorator.stopLoading()
                    if (logger.isDebugEnabled) {
                        logger.error(e.collectStackTrace())
                    }
                    MessagesUtils.showErrorMessageLater("Auto Detect Jvm Provider Failed", e.message, project)
                }
            }

        })
    }

    fun apply(): MutableList<JvmProviderConfig>? {
        val result = mutableListOf<JvmProviderConfig>()
        for (i in formTabs.indices) {
            val formTab = formTabs[i]
            formTab.apply() ?.let {
                result.add(it)
                tabbedPane.getTabComponentAt(i).foreground = okColor
            } ?:let {
                tabbedPane.getTabComponentAt(i).foreground = errorColor
            }

        }
        return if (result.size == formTabs.size) result else null
    }

    fun isInvalid(): Boolean {
        for (formTab in formTabs) {
            if (formTab.apply() == null) {
                return true
            }
        }
        return false
    }

}