package io.github.vudsen.arthasui.api.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JComponent

abstract class AbstractFormComponent<T>(private val parentDisposable: Disposable?) : FormComponent<T> {

    private var panel: DialogPanel? = null

    /**
     * 获取状态
     */
    abstract fun getState(): T

    /**
     * 创建一个新的 [DialogPanel]
     */
    abstract fun createDialogPanel(): DialogPanel

    override fun getComponent(): JComponent {
        panel ?.let { return it }
        val dialogPanel = createDialogPanel()
        parentDisposable ?.let {
            dialogPanel.registerValidators(it)
        }
        panel = dialogPanel
        return dialogPanel
    }

    override fun isModified(): Boolean {
        return panel?.isModified() ?: false
    }

    override fun apply(): T? {
        val dialogPanel = panel ?: return null
        dialogPanel.apply()
        if (dialogPanel.validateAll().isNotEmpty()) {
            return null
        }
        return getState()
    }
}