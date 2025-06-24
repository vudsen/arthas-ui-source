package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.ui.shortenTextWithEllipsis
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import javax.swing.*

class TreeNodeJVM(
    val providerConfig: JvmProviderConfig,
    val jvm: JVM,
    val ctx: TreeNodeContext
) : AbstractRecursiveTreeNode() {

    override fun refresh(): List<AbstractRecursiveTreeNode> {
        return emptyList()
    }

    override fun getIcon(): Icon {
        return jvm.getIcon()
    }

    override fun resolveText(): JLabel {
        val label = JLabel()
        label.text = jvm.id + ' ' + shortenTextWithEllipsis(
            jvm.name,
            4,
            10,
            0.3f,
            ctx.tree.width - 200,
            { s ->
                label.getFontMetrics(label.font).stringWidth(s)
            })
        return label
    }

    override fun getTopRootNode(): RecursiveTreeNode {
        return ctx.root
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