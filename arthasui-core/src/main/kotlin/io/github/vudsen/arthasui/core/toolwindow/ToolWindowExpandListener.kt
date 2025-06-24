package io.github.vudsen.arthasui.core.toolwindow

import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeExpansionListener
import javax.swing.tree.DefaultMutableTreeNode

class ToolWindowExpandListener(private val toolwindow: ToolWindowTree) : TreeExpansionListener {

    override fun treeExpanded(event: TreeExpansionEvent) {
        val component = event.path.lastPathComponent as DefaultMutableTreeNode
        val uo = component.userObject as RecursiveTreeNode
        toolwindow.launchRefreshNodeTask(uo, false)
    }

    override fun treeCollapsed(event: TreeExpansionEvent?) {}

}