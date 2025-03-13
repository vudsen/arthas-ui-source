package io.github.vudsen.arthasui.core.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.ui.PopupHandler
import java.awt.Component

class ToolWindowRightClickHandler : PopupHandler() {

    private val actions: Array<AnAction>

    init {
        val actions = mutableListOf<AnAction>()
        actions.add(object : AnAction("Create Custom Search Group", "", AllIcons.Nodes.Folder) {
            override fun actionPerformed(evt: AnActionEvent) {

            }
        })

        this.actions = actions.toTypedArray()
    }



    override fun invokePopup(p0: Component?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

}