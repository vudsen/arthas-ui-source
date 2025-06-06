package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.ui.shortenTextWithEllipsis
import com.intellij.ui.AnimatedIcon
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import java.awt.FlowLayout
import javax.swing.*

class TreeNodeJVM(
    private val rootNode: RecursiveTreeNode,
    val providerConfig: JvmProviderConfig,
    val jvm: JVM,
    private val parent: RecursiveTreeNode
) : AbstractRecursiveTreeNode() {

    /**
     * 设置加载状态.
     */
    var loading: Boolean = false

    override fun refresh(): List<AbstractRecursiveTreeNode> {
        parent.refreshRootNode()
        return emptyList()
    }

    override fun render(tree: JTree): JComponent {
        return JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            if (loading) {
                add(JLabel(AnimatedIcon.Default.INSTANCE))
            } else {
                add(JLabel(jvm.getIcon()))
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
            border = BorderFactory.createEmptyBorder(0, -5, 0, 0)
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

    override fun toString(): String {
        return jvm.name
    }


}