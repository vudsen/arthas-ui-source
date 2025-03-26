package io.github.vudsen.arthasui.core.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.common.ArthasUIIcons
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import io.github.vudsen.arthasui.conf.bean.JvmSearchGroup
import io.github.vudsen.arthasui.script.MyOgnlContext
import io.github.vudsen.arthasui.script.OgnlJvmSearcher
import java.awt.FlowLayout
import javax.swing.*

/**
 * 用户自定义的搜索节点，使用 ognl 脚本搜索特定的 jvm.
 */
class CustomSearchGroupTreeNode(val group: JvmSearchGroup, private val ctx: TreeNodeContext) : AbstractRecursiveTreeNode() {

    private fun mapToJvmNode(jvm: JVM): TreeNodeJVM {
        val provider = service<JvmProviderManager>().findProviderByJvm(ctx.config.providers, jvm) ?: TODO("Tip user that this type is not configured")
        return TreeNodeJVM(ctx.root, provider, jvm, ctx.project)
    }

    override fun refresh(): List<AbstractRecursiveTreeNode> {
        return OgnlJvmSearcher.executeByGroup(group, MyOgnlContext(ctx.hostMachine, ctx.config)).map {
            jvm -> mapToJvmNode(jvm)
        }
    }



    override fun render(tree: JTree): JComponent {
        return JPanel(FlowLayout(FlowLayout.LEFT, 0, 5)).apply {
            add(JLabel(ArthasUIIcons.Script))
            add(JLabel(group.name).apply {
                border = BorderFactory.createEmptyBorder(0, 4, 0, 0)
            })
        }
    }

    override fun getTopRootNode(): RecursiveTreeNode {
        return ctx.root
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomSearchGroupTreeNode

        return group == other.group
    }

    override fun hashCode(): Int {
        return group.hashCode()
    }
}