package io.github.vudsen.arthasui.api.ui

import javax.swing.JComponent
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

/**
 * 一个递归渲染的树. 每个节点自己负责渲染自己，不再需要外部的判断
 */
interface RecursiveTreeNode {

    /**
     * 刷新当前节点.
     *
     * **该方法可能会阻塞线程**, 实现类应该在合适的地方检查 [com.intellij.openapi.progress.ProgressManager.checkCanceled] 的状态以停止任务
     * @param force 强制刷新，若该值为 false，除了第一次调用会刷新节点，后续操作都不会更新
     * @return 当前节点自己, 需要更新所有子节点的值，每次返回的根节点必须是同一个实例.
     */
    fun refreshNode(force: Boolean): DefaultMutableTreeNode

    /**
     * 渲染当前节点
     */
    fun render(tree: JTree): JComponent

    /**
     * 获取根节点
     */
    fun getTopRootNode(): RecursiveTreeNode

}