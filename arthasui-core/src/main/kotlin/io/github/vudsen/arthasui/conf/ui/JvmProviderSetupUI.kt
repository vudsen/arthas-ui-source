package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.ui.components.JBTabbedPane
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.api.ui.FormComponent
import javax.swing.JComponent

class JvmProviderSetupUI(private val parentDisposable: Disposable)  {

    private lateinit var loadingDecorator: LoadingDecorator

    private val formTabs: List<FormComponent<JvmProviderConfig>> = mutableListOf()

    fun getComponent(): JComponent {
        val root = JBTabbedPane()
        val decorator = LoadingDecorator(root, parentDisposable, 0)
        loadingDecorator = decorator
        return decorator.component
    }

    /**
     * 刷新组件状态
     */
    fun refresh(hostMachine: HostMachine) {
        loadingDecorator.startLoading(false)
        ApplicationManager.getApplication().executeOnPooledThread {
            val tabs = mutableListOf<FormComponent<JvmProviderConfig>>()
            val pane = loadingDecorator.component as JBTabbedPane
            pane.removeAll()
            for (provider in service<JvmProviderManager>().getProviders()) {
                val configuration = provider.tryCreateDefaultConfiguration(hostMachine)
                val form = provider.createForm(configuration, parentDisposable)
                tabs.add(form)
                pane.addTab(provider.getName(), form.getComponent())
            }
            loadingDecorator.stopLoading()
        }
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