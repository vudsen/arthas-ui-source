package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTabbedPane
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.api.ui.FormComponent
import javax.swing.JComponent

class JvmProviderConfigUI(
    private val oldStates: List<JvmProviderConfig>,
    private val parentDisposable: Disposable
): FormComponent<MutableList<JvmProviderConfig>> {

    private val providers = service<JvmProviderManager>().getProviders()

    private val formTabs: MutableList<FormComponent<JvmProviderConfig>> = mutableListOf()

    private lateinit var tab: JBTabbedPane

    private val okColor = JBColor.namedColor("Label.foreground")

    private val errorColor = JBColor.namedColor("Label.errorForeground")

    override fun getComponent(): JComponent {
        val pane = JBTabbedPane().apply {
            for (provider in providers) {
                val old = oldStates.find { s -> s::class.java == provider.getConfigClass() }
                val form = provider.createForm(old, parentDisposable)
                addTab(provider.getName(), form.getComponent())
                formTabs.add(form)
            }
        }
        tab = pane
        return pane
    }

    override fun isModified(): Boolean {
        for (formTab in formTabs) {
            return formTab.isModified()
        }
        return false
    }

    override fun apply(): MutableList<JvmProviderConfig>? {
        val result = mutableListOf<JvmProviderConfig>()
        for (i in formTabs.indices) {
            val formTab = formTabs[i]
            formTab.apply() ?.let {
                result.add(it)
                tab.getTabComponentAt(i).foreground = okColor
            } ?:let {
                tab.getTabComponentAt(i).foreground = errorColor
            }

        }
        return if (result.size == formTabs.size) result else null
    }

}