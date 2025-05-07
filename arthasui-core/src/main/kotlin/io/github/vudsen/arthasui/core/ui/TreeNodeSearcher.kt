package io.github.vudsen.arthasui.core.ui

import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import java.awt.FlowLayout
import javax.swing.*

class TreeNodeSearcher(
    private val child: JvmSearchResult.Companion.ChildSearcher,
    private val ctx: TreeNodeContext,
    private val providerConfig: JvmProviderConfig,
) : AbstractRecursiveTreeNode() {

    override fun refresh(): List<AbstractRecursiveTreeNode> {
        val jvmList = child.load()
        jvmList.result ?.let {
            val result = ArrayList<AbstractRecursiveTreeNode>(it.size)
            for (jvm in it) {
                result.add(TreeNodeJVM(ctx.root, providerConfig, jvm, ctx.project))
            }
            return result
        }
        jvmList.child ?.let {
            val result = ArrayList<AbstractRecursiveTreeNode>(it.size)
            for (childSearcher in it) {
                result.add(TreeNodeSearcher(childSearcher, ctx, providerConfig))
            }
            return result
        }
        return emptyList()
    }

    override fun equals(other: Any?): Boolean {
        return child == other
    }

    override fun hashCode(): Int {
        return child.hashCode()
    }

    override fun render(tree: JTree): JComponent {
        return JPanel(FlowLayout(FlowLayout.LEFT, 0, 5)).apply {
            add(JLabel(child.getIcon()))
            add(JLabel(child.getName()).apply {
                border = BorderFactory.createEmptyBorder(0, 8, 0, 0)
            })
        }
    }

    override fun getTopRootNode(): RecursiveTreeNode {
        return ctx.root
    }
}