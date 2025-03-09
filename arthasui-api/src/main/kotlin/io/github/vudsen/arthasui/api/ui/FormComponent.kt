package io.github.vudsen.arthasui.api.ui

import javax.swing.JComponent

interface FormComponent<T> {

    /**
     * 获取组件. 对于多次调用，必须返回相同的组件
     */
    fun getComponent(): JComponent?

    /**
     * 验证表单并返回结果，如果非空，表示表单验证通过
     */
    fun apply(): T?

}