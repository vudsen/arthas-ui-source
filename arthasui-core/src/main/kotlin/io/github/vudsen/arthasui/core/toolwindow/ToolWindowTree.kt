package io.github.vudsen.arthasui.core.toolwindow

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.treeStructure.Tree
import io.github.vudsen.arthasui.api.ArthasExecutionManager
import io.github.vudsen.arthasui.api.bean.VirtualFileAttributes
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.api.ui.CloseableTreeNode
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import io.github.vudsen.arthasui.core.ui.TreeNodeJVM
import io.github.vudsen.arthasui.api.conf.ArthasUISettingsPersistent
import io.github.vudsen.arthasui.common.util.ProgressIndicatorStack
import io.github.vudsen.arthasui.core.ui.DefaultCloseableTreeNode
import io.github.vudsen.arthasui.core.ui.DefaultHostMachineTreeNode
import io.github.vudsen.arthasui.language.arthas.psi.ArthasFileType
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

/**
 * ToolWindow 界面。所有的子节点必须实现 [RecursiveTreeNode]. 根节点可以选择实现:
 *
 * - [CloseableTreeNode] : 用于表示节点可以被关闭
 */
class ToolWindowTree(val project: Project) : Disposable {

    private val logger = Logger.getInstance(ToolWindowTree::class.java)

    private val rootModel = DefaultMutableTreeNode("Invisible Root")

    val tree = Tree(DefaultTreeModel(rootModel))

    private val updateListener =  {
        ApplicationManager.getApplication().invokeLater {
            refreshRootNode(true)
        }
    }

    init {
        tree.setCellRenderer(ToolWindowTreeCellRenderer())
        tree.addMouseListener(ToolWindowRightClickHandler(this))
        tree.addMouseListener(ToolWindowMouseAdapter(this))
        tree.addTreeExpansionListener(ToolWindowExpandListener(this))

        service<ArthasUISettingsPersistent>().addUpdatedListener(updateListener)
        tree.minimumSize = Dimension(301, tree.minimumSize.height)
        refreshRootNode(false)
        tree.expandRow(0)
        tree.isRootVisible = false
    }

    private var isRunning = false

    /**
     * 刷新某个一个节点
     */
    fun launchRefreshNodeTask(node: RecursiveTreeNode) {
        if (isRunning) {
            return
        }
        isRunning = true
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Load arthas nodes", true) {

            override fun onFinished() {
                isRunning = false
            }

            override fun run(indicator: ProgressIndicator) {
                ProgressIndicatorStack.push(indicator)
                try {
                    node.refreshRootNode(false)
                } finally {
                    ProgressIndicatorStack.pop()
                }
                ToolWindowManager.getInstance(project).invokeLater {
                    tree.updateUI()
                }
            }

            override fun onThrowable(error: Throwable) {
                logger.error(error)
                Messages.showErrorDialog(project, error.message, "Load Failed")
            }
        })
    }

    /**
     * 刷新根节点
     */
    fun refreshRootNode(force: Boolean) {
        val persistent = service<ArthasUISettingsPersistent>()

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
                node = DefaultCloseableTreeNode(hostMachineConfig, project, tree)
                Disposer.register(this, node)
            } else {
                node = DefaultHostMachineTreeNode(hostMachineConfig, project, tree)
            }
            rootModel.add(node.refreshRootNode(force))
        }
        tree.model = DefaultTreeModel(rootModel)
        tree.updateUI()
    }

    override fun dispose() {
        service<ArthasUISettingsPersistent>().removeUpdateListener(updateListener)
    }

    fun tryOpenQueryConsole() {
        val node = currentFocusedNode()
        if (node !is TreeNodeJVM) {
            return
        }

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Ensure JVM is alive", true) {

            override fun run(indicator: ProgressIndicator) {
                val provider = service<JvmProviderManager>().getProvider(node.jvm.context.providerConfig)
                if (provider.isJvmInactive(node.jvm)) {
                    ApplicationManager.getApplication().invokeLater {
                        Messages.showWarningDialog(project, "Jvm ${node.jvm.name} is not active, please refresh and try again!", "Jvm Not Available")
                    }
                    return
                }

                val fileEditorManager = FileEditorManager.getInstance(project)

                fileEditorManager.allEditors.find { e -> e.file.fileType == ArthasFileType && e.file.name == node.jvm.name } ?.let {
                    ApplicationManager.getApplication().invokeLater {
                        fileEditorManager.openFile(it.file, true, true)
                    }
                    return
                }

                val lightVirtualFile = LightVirtualFile(node.jvm.name, ArthasFileType, "")
                lightVirtualFile.putUserData(
                    ArthasExecutionManager.VF_ATTRIBUTES,
                    VirtualFileAttributes(
                        node.jvm,
                        (node.getTopRootNode() as DefaultHostMachineTreeNode).getConnectConfig(),
                        node.providerConfig)
                )
                ApplicationManager.getApplication().invokeLater {
                    fileEditorManager.openFile(lightVirtualFile, true)
                }
            }

        })

    }

    /**
     * 获取用户当前聚焦的节点
     */
    fun currentFocusedNode(): RecursiveTreeNode? {
        val comp = tree.lastSelectedPathComponent ?: return null
        if (comp !is DefaultMutableTreeNode) {
            throw IllegalStateException("The node must be a DefaultMutableTreeNode")
        }
        val uo = comp.userObject as RecursiveTreeNode
        return uo
    }

    fun getComponent(): JComponent {
        return ToolWindowToolbar(this).createPanel()
    }

}