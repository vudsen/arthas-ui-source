package io.github.vudsen.arthasui.core.ui

import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import javax.swing.JComponent
import javax.swing.JTree

/**
 * 用户自定义的搜索节点，使用 ognl 脚本搜索特定的 jvm.
 */
class CustomGroupSearchTreeNode : AbstractRecursiveTreeNode() {

    override fun refresh(): List<AbstractRecursiveTreeNode> {
        TODO("Not yet implemented")
    }

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        TODO("Not yet implemented")
    }

    override fun render(tree: JTree): JComponent {
        TODO("Not yet implemented")
    }

    override fun getTopRootNode(): RecursiveTreeNode {
        TODO("Not yet implemented")
    }
}