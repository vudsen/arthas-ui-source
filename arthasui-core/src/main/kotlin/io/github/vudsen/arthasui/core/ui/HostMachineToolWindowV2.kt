package io.github.vudsen.arthasui.core.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.treeStructure.Tree
import com.intellij.ui.util.minimumWidth
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import io.github.vudsen.arthasui.common.ui.TreeNodeJVM
import io.github.vudsen.arthasui.conf.ArthasUISettingsConfigurable
import io.github.vudsen.arthasui.conf.ArthasUISettingsPersistent
import io.github.vudsen.arthasui.api.ArthasExecutionManager
import io.github.vudsen.arthasui.api.bean.VirtualFileAttributes
import io.github.vudsen.arthasui.api.ui.CloseableTreeNode
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.language.arthas.psi.ArthasFileType
import io.ktor.util.collections.*
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.TreeCellRenderer

/**
 * ToolWindow 界面。所有的子节点必须实现 [RecursiveTreeNode]. 根节点可以选择实现:
 *
 * - [CloseableTreeNode] : 用于表示节点可以被关闭
 */
class HostMachineToolWindowV2(private val project: Project) : Disposable {

    private val rootModel = DefaultMutableTreeNode("Invisible Root")

    /**
     * Structure:
     * - `Invisible Root`
     *  - --> [HostMachineNode]
     */
    private val tree = Tree(DefaultTreeModel(rootModel))

    private lateinit var closeAction: CloseAction

    inner class CloseAction : AnAction(AllIcons.Actions.Suspend) {

        var enabled = false

        private val debounce = (ConcurrentSet <CloseableTreeNode>())

        override fun getActionUpdateThread(): ActionUpdateThread {
            return ActionUpdateThread.EDT
        }

        override fun actionPerformed(e: AnActionEvent) {
            val lastSelectedPathComponent = tree.lastSelectedPathComponent ?: return
            val node = lastSelectedPathComponent as DefaultMutableTreeNode
            val uo = node.userObject as RecursiveTreeNode
            val rootNode = uo.getTopRootNode()
            if (rootNode is CloseableTreeNode && !debounce.contains(rootNode) && rootNode.isActive()) {
                debounce.add(rootNode)
                ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Closing client", true) {

                    override fun run(indicator: ProgressIndicator) {
                        try {
                            rootNode.close()
                            e.presentation.isEnabled = rootNode.isActive()
                        } finally {
                            debounce.remove(rootNode)
                        }
                    }
                })
            }
        }

        override fun update(e: AnActionEvent) {
            e.presentation.isEnabled = enabled
        }

    }

    init {
        tree.setCellRenderer(object : TreeCellRenderer {
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
        })
        tree.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val lastSelectedPathComponent = tree.lastSelectedPathComponent ?: return
                val node = lastSelectedPathComponent as DefaultMutableTreeNode
                val uo = node.userObject as RecursiveTreeNode
                if (e.clickCount == 2) {
                    if (uo is TreeNodeJVM) {
                        tryOpenQueryConsole(lastSelectedPathComponent)
                    } else {
                        launchRefreshNodeTask(uo)
                    }
                }
                val rootNode = uo.getTopRootNode()
                if (rootNode is CloseableTreeNode) {
                    closeAction.enabled = rootNode.isActive()
                } else {
                    closeAction.enabled = false
                }
            }
        })
        project.getService(ArthasUISettingsPersistent::class.java).addUpdatedListener {
            loadRootNode()
        }
        tree.minimumWidth = 301
        loadRootNode()
        tree.expandRow(0)
        tree.isRootVisible = false
    }

    private fun launchRefreshNodeTask(node: RecursiveTreeNode) {
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Load arthas nodes", true) {
            override fun run(indicator: ProgressIndicator) {
                node.refreshRootNode()
                ToolWindowManager.getInstance(project).invokeLater {
                    tree.updateUI()
                }
            }
        })
    }

    private fun loadRootNode() {
        val persistent = project.getService(ArthasUISettingsPersistent::class.java)

        for (child in rootModel.children()) {
            val treeNode = child as DefaultMutableTreeNode
            val uo = treeNode.userObject
            if (uo is CloseableTreeNode) {
                Disposer.dispose(uo)
            }
        }
        rootModel.removeAllChildren()
        for (hostMachineConfig in persistent.state.hostMachines) {
            val node: AbstractRecursiveTreeNode
            if (hostMachineConfig.connect.isRequireClose()) {
                node = DefaultCloseableTreeNode(hostMachineConfig, project)
                Disposer.register(this, node)
            } else {
                node = DefaultHostMachineTreeNode(hostMachineConfig, project)
            }
            rootModel.add(node.refreshRootNode())
            tree.updateUI()
        }
    }

    private fun tryResolveConfig(node: DefaultMutableTreeNode): VirtualFileAttributes? {
        val userObject = node.userObject
        if (userObject !is TreeNodeJVM) {
            return null
        }
        val jvm = userObject.jvm
        val root = userObject.getTopRootNode()
        if (root is DefaultHostMachineTreeNode) {
            return VirtualFileAttributes(jvm, root.getConnectConfig(), userObject.providerConfig)
        }
        return null
    }

    private fun tryOpenQueryConsole(node: Any = tree.lastSelectedPathComponent) {
        if (node !is DefaultMutableTreeNode) {
            return
        }
        val config = tryResolveConfig(node) ?: return

        val fileEditorManager = FileEditorManager.getInstance(project)

        val lightVirtualFile = LightVirtualFile(config.jvm.getMainClass(), ArthasFileType, "")
        lightVirtualFile.putUserData(ArthasExecutionManager.VF_ATTRIBUTES, config)
        fileEditorManager.openFile(lightVirtualFile, true)
    }

    fun getComponent(): JComponent {
        val toolbarDecorator = ToolbarDecorator.createDecorator(tree)
        toolbarDecorator.setAddAction {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, ArthasUISettingsConfigurable::class.java)
        }
        toolbarDecorator.disableRemoveAction()
        toolbarDecorator.addExtraAction(object : AnAction("Open Query Console", "", AllIcons.Debugger.Console) {
            override fun actionPerformed(e: AnActionEvent) {
                tryOpenQueryConsole()
            }
        })

        this.closeAction = CloseAction()
        toolbarDecorator.addExtraAction(closeAction)
        toolbarDecorator.addExtraAction(object : AnAction(AllIcons.Actions.Refresh) {

            override fun actionPerformed(e: AnActionEvent) {
                val com = tree.lastSelectedPathComponent ?: return
                val treeNode = com as DefaultMutableTreeNode
                val node = treeNode.userObject as RecursiveTreeNode
                launchRefreshNodeTask(node)
            }

        })

        return toolbarDecorator.createPanel()
    }

    override fun dispose() {}


}