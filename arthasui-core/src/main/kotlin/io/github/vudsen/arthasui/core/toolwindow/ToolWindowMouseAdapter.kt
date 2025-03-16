package io.github.vudsen.arthasui.core.toolwindow

import io.github.vudsen.arthasui.common.ui.TreeNodeJVM
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class ToolWindowMouseAdapter(private val toolwindow: ToolWindowTree) : MouseAdapter() {

    override fun mouseClicked(e: MouseEvent) {
        val uo = toolwindow.currentFocusedRootNode() ?: return
        if (e.clickCount == 2) {
            if (uo is TreeNodeJVM) {
                toolwindow.tryOpenQueryConsole()
            } else {
                toolwindow.launchRefreshNodeTask(uo)
            }
        }
    }

}