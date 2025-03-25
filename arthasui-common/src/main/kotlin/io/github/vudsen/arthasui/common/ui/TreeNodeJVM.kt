package io.github.vudsen.arthasui.common.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.shortenTextWithEllipsis
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.ArthasExecutionManager
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree

class TreeNodeJVM(
    private val rootNode: RecursiveTreeNode,
    val providerConfig: JvmProviderConfig,
    val jvm: JVM,
    project: Project) : AbstractRecursiveTreeNode() {

    private val manager = project.getService(ArthasExecutionManager::class.java)

    override fun refresh(): List<AbstractRecursiveTreeNode> {
        return emptyList()
    }

    override fun render(tree: JTree): JComponent {
        return JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            if (manager.isAttached(jvm)) {
                add(JLabel(AllIcons.Debugger.AttachToProcess))
            } else {
                add(JLabel(AllIcons.Nodes.Console))
            }
            add(JLabel(jvm.id))
            add(
                JLabel(
                    shortenTextWithEllipsis(
                        jvm.name,
                        4,
                        10,
                        0.3f,
                        tree.width - 200,
                        { s ->
                            getFontMetrics(this.font).stringWidth(s)
                        })
                )
            )
        }
    }

    override fun getTopRootNode(): RecursiveTreeNode {
        return rootNode
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TreeNodeJVM

        return jvm == other.jvm
    }

    override fun hashCode(): Int {
        return jvm.hashCode()
    }


}