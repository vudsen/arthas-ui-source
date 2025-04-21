package io.github.vudsen.arthasui.api.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JComponent

/**
 * 使用 Kotlin DSL 创建组件.
 * @param parentDisposable 如果不提供该值，则不会创建表单验证器
 */
abstract class AbstractFormComponent<T>(protected val parentDisposable: Disposable) : FormComponent<T> {

    protected var panel: DialogPanel? = null

    /**
     * 获取状态
     */
    abstract fun getState(): T

    /**
     * 创建一个新的 [DialogPanel]
     */
    protected abstract fun createDialogPanel(): DialogPanel

    override fun getComponent(): JComponent {
        panel ?.let { return it }
        val dialogPanel = createDialogPanel()
        dialogPanel.registerValidators(parentDisposable)
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