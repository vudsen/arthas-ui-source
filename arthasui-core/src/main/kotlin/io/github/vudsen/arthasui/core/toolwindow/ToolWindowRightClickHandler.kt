package io.github.vudsen.arthasui.core.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.ui.PopupHandler
import io.github.vudsen.arthasui.conf.JvmSearchGroupConfigurable
import io.github.vudsen.arthasui.conf.JvmSearchGroupDeleteConfigurable
import io.github.vudsen.arthasui.core.ui.CustomSearchGroupTreeNode
import io.github.vudsen.arthasui.core.ui.DefaultHostMachineTreeNode
import java.awt.Component

class ToolWindowRightClickHandler(private val toolWindowTree: ToolWindowTree) : PopupHandler() {

    private val actions: DefaultActionGroup = DefaultActionGroup().apply {
        add(object : AnAction("Delete") {

            override fun getActionUpdateThread(): ActionUpdateThread {
                return ActionUpdateThread.EDT
            }

            override fun update(e: AnActionEvent) {
                e.presentation.isEnabled = toolWindowTree.currentFocusedNode() is CustomSearchGroupTreeNode
            }

            override fun actionPerformed(p0: AnActionEvent) {
                val node = toolWindowTree.currentFocusedNode()
                if (node !is CustomSearchGroupTreeNode) {
                    return
                }
                val root = node.getTopRootNode() as DefaultHostMachineTreeNode

                ShowSettingsUtil.getInstance().editConfigurable(
                    toolWindowTree.project,
                    JvmSearchGroupDeleteConfigurable(toolWindowTree.project, root.config, node.group)
                )
            }
        })
        add(object : AnAction("Update") {

            override fun update(e: AnActionEvent) {
                e.presentation.isEnabled = toolWindowTree.currentFocusedNode() is CustomSearchGroupTreeNode
            }

            override fun getActionUpdateThread(): ActionUpdateThread {
                return ActionUpdateThread.EDT
            }

            override fun actionPerformed(e: AnActionEvent) {
                val node = toolWindowTree.currentFocusedNode()
                if (node !is CustomSearchGroupTreeNode) {
                    return
                }
                val root = node.getTopRootNode() as DefaultHostMachineTreeNode

                ShowSettingsUtil.getInstance().editConfigurable(
                    toolWindowTree.project,
                    JvmSearchGroupConfigurable(toolWindowTree.project, root.config, node.group)
                )
            }
        })
        add(object : AnAction("Create Custom Search Group", "", AllIcons.Nodes.Folder) {
            override fun actionPerformed(evt: AnActionEvent) {
                val rootNode = toolWindowTree.currentFocusedNode()?.getTopRootNode() ?: return
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


    override fun invokePopup(p0: Component?, p1: Int, p2: Int) {
        val actionManager = ActionManager.getInstance()
        val popupMenu = actionManager.createActionPopupMenu(ActionPlaces.POPUP, actions)
        popupMenu.component.show(p0, p1, p2)
    }

}