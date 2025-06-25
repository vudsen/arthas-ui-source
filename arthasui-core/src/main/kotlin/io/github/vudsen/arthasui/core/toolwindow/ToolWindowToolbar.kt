package io.github.vudsen.arthasui.core.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.ui.ToolbarDecorator
import io.github.vudsen.arthasui.conf.ArthasUISettingsConfigurable
import javax.swing.JPanel

class ToolWindowToolbar(private val toolWindow: ToolWindowTree)  {


    fun createPanel(): JPanel {
        val toolbarDecorator = ToolbarDecorator.createDecorator(toolWindow.tree)
        toolbarDecorator.setAddAction {
            ShowSettingsUtil.getInstance().showSettingsDialog(toolWindow.project, ArthasUISettingsConfigurable::class.java)
        }
        toolbarDecorator.disableRemoveAction()
        toolbarDecorator.addExtraAction(object : AnAction("Open Query Console", "", AllIcons.Debugger.Console) {
            override fun actionPerformed(e: AnActionEvent) {
                toolWindow.tryOpenQueryConsole()
            }
        })

        toolbarDecorator.addExtraAction(object : AnAction(AllIcons.Actions.Refresh) {

            override fun actionPerformed(e: AnActionEvent) {
                toolWindow.currentFocusedNode() ?.let {
                    toolWindow.launchRefreshNodeTask(it, true)
                } ?: let {
                    toolWindow.refreshRootNode(true)
                }
            }

        })

        return toolbarDecorator.createPanel()
    }

}