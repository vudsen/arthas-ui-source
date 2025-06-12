package io.github.vudsen.arthasui.core.toolwindow

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.ui.ToolbarDecorator
import io.github.vudsen.arthasui.api.ui.CloseableTreeNode
import io.github.vudsen.arthasui.common.util.ProgressIndicatorStack
import io.github.vudsen.arthasui.conf.ArthasUISettingsConfigurable
import io.ktor.util.collections.*
import javax.swing.JPanel

class ToolWindowToolbar(private val toolWindow: ToolWindowTree)  {


    private lateinit var closeAction: CloseAction;

    inner class CloseAction : AnAction(AllIcons.Actions.Suspend) {


        private val debounce = (ConcurrentSet <CloseableTreeNode>())

        override fun getActionUpdateThread(): ActionUpdateThread {
            return ActionUpdateThread.EDT
        }

        override fun actionPerformed(e: AnActionEvent) {
            val rootNode = toolWindow.currentFocusedNode()?.getTopRootNode()
            if (rootNode is CloseableTreeNode && !debounce.contains(rootNode) && rootNode.isActive()) {
                debounce.add(rootNode)
                ProgressManager.getInstance().run(object : Task.Backgroundable(toolWindow.project, "Closing client", true) {

                    override fun run(indicator: ProgressIndicator) {
                        ProgressIndicatorStack.push(indicator)
                        try {
                            rootNode.close()
                            e.presentation.isEnabled = rootNode.isActive()
                        } finally {
                            ProgressIndicatorStack.pop()
                            debounce.remove(rootNode)
                        }
                    }
                })
            }
        }

        override fun update(e: AnActionEvent) {
            val rootNode = toolWindow.currentFocusedNode()?.getTopRootNode()
            if (rootNode is CloseableTreeNode) {
                e.presentation.isEnabled = rootNode.isActive()
            } else {
                e.presentation.isEnabled = false
            }
        }

    }


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

        this.closeAction = CloseAction()
        toolbarDecorator.addExtraAction(closeAction)
        toolbarDecorator.addExtraAction(object : AnAction(AllIcons.Actions.Refresh) {

            override fun actionPerformed(e: AnActionEvent) {
                toolWindow.currentFocusedNode() ?.let {
                    toolWindow.launchRefreshNodeTask(it)
                } ?: let {
                    toolWindow.refreshRootNode()
                }
            }

        })

        return toolbarDecorator.createPanel()
    }

}