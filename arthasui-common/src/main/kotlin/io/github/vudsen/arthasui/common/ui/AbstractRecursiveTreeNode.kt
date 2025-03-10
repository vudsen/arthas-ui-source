package io.github.vudsen.arthasui.common.ui

import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import java.util.*
import javax.swing.tree.DefaultMutableTreeNode

/**
 * 封装 [RecursiveTreeNode] 提供了保留子节点的能力，避免每次更新导致子节点被删除，进而导致整个子树被删除。
 */
abstract class AbstractRecursiveTreeNode : RecursiveTreeNode {

    private val root: DefaultMutableTreeNode by lazy {
        DefaultMutableTreeNode(this)
    }

    /**
     * 刷新子节点，返回所有新的子节点.
     *
     * 实现类应该在合适的地方检查 [com.intellij.openapi.progress.ProgressManager.checkCanceled] 的状态以停止任务
     */
    protected abstract fun refresh(): List<AbstractRecursiveTreeNode>

    override fun refreshRootNode(): DefaultMutableTreeNode {
        replace(refresh())
        return root
    }

    /**
     * 替换子节点，若有相同的节点，则会保留原始的节点
     */
    private fun replace(childValues: List<AbstractRecursiveTreeNode>) {
        if (childValues.isEmpty()) {
            root.removeAllChildren()
            return
        }
        // 对于 [childValues], 找到在 [root] 中相同的对象，然后放到新数组中
        // 如果 [root] 中没有的，认作新对象
        val replace = ArrayList<DefaultMutableTreeNode>(childValues.size)
        for (childValue in childValues) {
            var old: DefaultMutableTreeNode? = null
            for (child in root.children()) {
                val treeNode = child as DefaultMutableTreeNode
                if (treeNode.userObject.equals(childValue)) {
                    old = treeNode
                    break
                }
            }
            if (old != null) {
                replace.add(old)
            } else {
                replace.add(childValue.root)
            }
        }
        root.removeAllChildren()
        for (defaultMutableTreeNode in replace) {
            root.add(defaultMutableTreeNode)
        }
    }

    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int


    
}