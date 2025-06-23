package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.progress.ProgressManager
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmSearchDelegate
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import java.awt.FlowLayout
import java.lang.ref.WeakReference
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
        val jvmList = delegate.load()
        jvmList.result ?.let {
            val result = ArrayList<AbstractRecursiveTreeNode>(it.size)
            for (jvm in it) {
                result.add(TreeNodeJVM(providerConfig, jvm, ctx))
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

    override fun getIcon(): Icon {
        return delegate.getIcon()
    }

    override fun resolveText(): JLabel {
        return JLabel(delegate.getName())
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

    override fun getTopRootNode(): RecursiveTreeNode {
        return ctx.root
    }
}