package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.components.service
import com.intellij.ui.components.JBTabbedPane
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.api.ui.FormComponent
import javax.swing.JComponent

class JvmProviderConfigUI(private val oldStates: List<JvmProviderConfig>): FormComponent<MutableList<JvmProviderConfig>> {

    private val providers = service<JvmProviderManager>().getProviders()

    private val formTabs: MutableList<FormComponent<JvmProviderConfig>> = mutableListOf()


    override fun getComponent(): JComponent {
        val pane = JBTabbedPane().apply {
            for (provider in providers) {
                val old = oldStates.find { s -> s::class.java == provider.getConfigClass() }
                val form = provider.createForm(old)
                addTab(provider.getName(), form.getComponent())
                formTabs.add(form)
            }
        }
        return pane
    }

    override fun apply(): MutableList<JvmProviderConfig>? {
        val result = mutableListOf<JvmProviderConfig>()
        for (formTab in formTabs) {
            formTab.apply() ?.let { result.add(it) } ?: return null
        }
        return result
    }

}