package io.github.vudsen.arthasui.api.ui

import com.intellij.openapi.Disposable

/**
 * 表示该节点可以被关闭，当用户选中该节点时，可以使用关闭按钮
 */
interface CloseableTreeNode : AutoCloseable, Disposable {

    /**
     * 是否存活
     */
    fun isActive(): Boolean


}