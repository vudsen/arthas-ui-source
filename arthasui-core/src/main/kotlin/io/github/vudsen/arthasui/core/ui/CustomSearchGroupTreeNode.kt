package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.Messages
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.common.ArthasUIIcons
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import io.github.vudsen.arthasui.api.bean.JvmSearchGroup
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
        return TreeNodeJVM(ctx.root, provider, jvm, this)
    }

    override fun refresh(): List<AbstractRecursiveTreeNode> {
        try {
            return OgnlJvmSearcher.executeByGroup(group, MyOgnlContext(ctx.template, ctx.config)).map {
                jvm -> mapToJvmNode(jvm)
            }
        } catch (e: Exception) {
            ApplicationManager.getApplication().invokeLater {
                Messages.showErrorDialog(e.toString(), "Failed To Execute Script")
            }
            return emptyList()
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