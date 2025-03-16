package io.github.vudsen.arthasui.core.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.ui.PopupHandler
import io.github.vudsen.arthasui.conf.JvmSearchGroupConfigurable
import io.github.vudsen.arthasui.core.ui.DefaultHostMachineTreeNode
import java.awt.Component

class ToolWindowRightClickHandler(private val toolWindowTree: ToolWindowTree) : PopupHandler() {

    private val actions: DefaultActionGroup

    init {
        this.actions = DefaultActionGroup().apply {
            add(object : AnAction("Create Custom Search Group", "", AllIcons.Nodes.Folder) {
                override fun actionPerformed(evt: AnActionEvent) {
                    val rootNode = toolWindowTree.currentFocusedRootNode()?.getTopRootNode() ?: return
                    if (rootNode !is DefaultHostMachineTreeNode) {
                        return
                    }
                    ShowSettingsUtil.getInstance().editConfigurable(
                        toolWindowTree.project,
                        JvmSearchGroupConfigurable(toolWindowTree.project, rootNode.config)
                    )
                }
            })
        }
    }



    override fun invokePopup(p0: Component?, p1: Int, p2: Int) {
        val actionManager = ActionManager.getInstance()
        val popupMenu = actionManager.createActionPopupMenu(ActionPlaces.POPUP, actions)
        popupMenu.component.show(p0, p1, p2)
    }

}