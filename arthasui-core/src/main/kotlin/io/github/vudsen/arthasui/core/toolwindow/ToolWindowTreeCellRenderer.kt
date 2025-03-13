package io.github.vudsen.arthasui.core.toolwindow

import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import java.awt.Component
import javax.swing.JLabel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreeCellRenderer

class ToolWindowTreeCellRenderer : TreeCellRenderer {
    override fun getTreeCellRendererComponent(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ): Component {
        if (value !is DefaultMutableTreeNode) {
            throw IllegalStateException()
        }
        val userObject = value.userObject
        if (userObject !is RecursiveTreeNode) {
            return JLabel(userObject.toString())
        }
        return userObject.render(tree)
    }
}