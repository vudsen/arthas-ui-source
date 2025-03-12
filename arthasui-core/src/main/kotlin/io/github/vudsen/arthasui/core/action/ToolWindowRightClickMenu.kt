package io.github.vudsen.arthasui.core.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class ToolWindowRightClickMenu() : ActionGroup() {

    private val actions: Array<AnAction>

    init {
        val actions = mutableListOf<AnAction>()
        actions.add(object : AnAction("Create Custom Search Group", "", AllIcons.Nodes.Folder) {
            override fun actionPerformed(evt: AnActionEvent) {

            }
        }.apply {
            templatePresentation.isEnabled = false
        })

        this.actions = actions.toTypedArray()
    }

    override fun getChildren(event: AnActionEvent?): Array<AnAction> {

    }

}