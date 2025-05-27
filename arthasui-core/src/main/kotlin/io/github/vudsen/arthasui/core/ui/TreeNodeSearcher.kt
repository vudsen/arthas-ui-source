package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.ui.getUserData
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmSearchDelegate
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import java.awt.FlowLayout
import java.util.*
import javax.swing.*
import kotlin.collections.ArrayList

/**
 * 支持 [JvmSearchDelegate]，渲染树形结构
 */
class TreeNodeSearcher(
    private val delegate: JvmSearchDelegate,
    private val ctx: TreeNodeContext,
    private val providerConfig: JvmProviderConfig,
) : AbstractRecursiveTreeNode() {

    override fun refresh(): List<AbstractRecursiveTreeNode> {
        ctx.template.putUserData(HostMachine.PROGRESS_INDICATOR, ctx.tree.getUserData(HostMachine.PROGRESS_INDICATOR))
        val jvmList = delegate.load()
        jvmList.result ?.let {
            val result = ArrayList<AbstractRecursiveTreeNode>(it.size)
            for (jvm in it) {
                result.add(TreeNodeJVM(ctx.root, providerConfig, jvm, this))
            }
            return result
        }
        jvmList.childs ?.let {
            val result = ArrayList<AbstractRecursiveTreeNode>(it.size)
            for (child in it) {
                result.add(TreeNodeSearcher(child, ctx, providerConfig))
            }
            return result
        }
        return emptyList()
    }

    override fun equals(other: Any?): Boolean {
        return delegate == other
    }

    override fun hashCode(): Int {
        return delegate.hashCode()
    }

    override fun toString(): String {
        return delegate.getName()
    }

    override fun render(tree: JTree): JComponent {
        return JPanel(FlowLayout(FlowLayout.LEFT, 0, 5)).apply {
            add(JLabel(delegate.getIcon()))
            add(JLabel(delegate.getName()).apply {
                border = BorderFactory.createEmptyBorder(0, 8, 0, 0)
            })
        }
    }

    override fun getTopRootNode(): RecursiveTreeNode {
        return ctx.root
    }
}