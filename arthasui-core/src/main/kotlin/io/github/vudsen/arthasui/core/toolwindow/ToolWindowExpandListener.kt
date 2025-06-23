package io.github.vudsen.arthasui.core.toolwindow

import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeExpansionListener

class ToolWindowExpandListener(private val toolwindow: ToolWindowTree) : TreeExpansionListener {

    override fun treeExpanded(event: TreeExpansionEvent?) {
        val uo = toolwindow.currentFocusedNode() ?: return
        toolwindow.launchRefreshNodeTask(uo)
    }

    override fun treeCollapsed(event: TreeExpansionEvent?) {
    }

}