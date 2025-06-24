package io.github.vudsen.arthasui.common.ui

import com.intellij.ui.AnimatedIcon
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import java.awt.FlowLayout
import java.util.*
import javax.swing.BorderFactory
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

/**
 * 封装 [RecursiveTreeNode] 提供了保留子节点的能力，避免每次更新导致子节点被删除，进而导致整个子树被删除。
 */
abstract class AbstractRecursiveTreeNode : RecursiveTreeNode {

    private val root: FakeExpandableTreeNode by lazy {
        FakeExpandableTreeNode(this)
    }

    private var isLoading = false

    private var isInitialized = false

    /**
     * 刷新子节点，返回所有新的子节点.
     *
     * 实现类应该在合适的地方检查 [com.intellij.openapi.progress.ProgressManager.checkCanceled] 的状态以停止任务
     */
    protected abstract fun refresh(): List<AbstractRecursiveTreeNode>

    /**
     * 获取当前要渲染的图标
     */
    protected abstract fun getIcon(): Icon

    /**
     * 获取要显示的文字
     */
    protected abstract fun resolveText(): JLabel

    /**
     * 需要处理的范围:
     * 1. 第一次显示时，子节点未加载，用户单击展开图标或双击节点
     * 2. 用户点击刷新按钮
     */
    override fun refreshRootNode(force: Boolean): DefaultMutableTreeNode {
        if (!force && isInitialized) {
            return root
        }
        isLoading = true
        try {
            replace(refresh())
            isInitialized = true
        } finally {
            isLoading = false
        }
        return root
    }


    override fun render(tree: JTree): JComponent {
        return JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            if (isLoading) {
                add(JLabel(AnimatedIcon.Default.INSTANCE))
            } else {
                add(JLabel(getIcon()))
            }
            add(resolveText())
            border = BorderFactory.createEmptyBorder(0, -5, 0, 0)
        }
    }

    /**
     * 替换子节点，若有相同的节点，则会保留原始的节点
     */
    private fun replace(childValues: List<AbstractRecursiveTreeNode>) {
        if (childValues.isEmpty()) {
            root.removeAllChildren()
            root.isLeaf = true
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
        root.isLeaf = false
    }

    abstract override fun equals(other: Any?): Boolean

    abstract override fun hashCode(): Int

    abstract override fun toString(): String
    
}