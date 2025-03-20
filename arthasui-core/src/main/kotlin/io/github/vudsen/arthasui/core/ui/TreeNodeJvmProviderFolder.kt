package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.components.service
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import io.github.vudsen.arthasui.common.ui.TreeNodeJVM
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree

class TreeNodeJvmProviderFolder(private val ctx: TreeNodeContext, private val provider: JvmProviderConfig) : AbstractRecursiveTreeNode() {

    override fun refresh(): List<AbstractRecursiveTreeNode> {
        val jvmList = service<JvmProviderManager>().getProvider(provider).searchJvm(ctx.hostMachine, provider)
        val result = ArrayList<AbstractRecursiveTreeNode>(jvmList.size)
        for (jvm in jvmList) {
            result.add(TreeNodeJVM(ctx.root, provider, jvm, ctx.project))
        }
        return result
    }

    override fun render(tree: JTree): JComponent {
        return JPanel(FlowLayout(FlowLayout.LEFT, 0, 5)).apply {
            add(JLabel(provider.getIcon()))
            add(JLabel(provider.getName()))
        }
    }

    override fun getTopRootNode(): RecursiveTreeNode {
        return ctx.root
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TreeNodeJvmProviderFolder

        return provider == other.provider
    }

    override fun hashCode(): Int {
        return provider.hashCode()
    }


}