package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import io.github.vudsen.arthasui.conf.HostMachineConfigV2
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree

/**
 * 默认的根节点
 */
open class DefaultHostMachineTreeNode(val config: HostMachineConfigV2, project: Project) : AbstractRecursiveTreeNode() {


    protected val ctx: TreeNodeContext

    private var root: JComponent? = null

    init {
        val factory = service<HostMachineConnectManager>()
        val hostMachine = factory.connect(config.connect)
        ctx = TreeNodeContext(hostMachine, this, project, config)
    }

    fun getConnectConfig(): HostMachineConnectConfig {
        return config.connect
    }

    override fun refresh(): List<AbstractRecursiveTreeNode> {
        val result = mutableListOf<AbstractRecursiveTreeNode>()
        for (provider in config.providers) {
            result.add(TreeNodeJvmProviderFolder(ctx, provider))
        }
        for (searchGroup in config.searchGroups) {
            result.add(CustomSearchGroupTreeNode(searchGroup, ctx))
        }
        return result
    }


    override fun render(tree: JTree): JComponent {
        root ?.let { return it }
        val root = JPanel(FlowLayout(FlowLayout.LEFT, 0, 5)).apply {
            add(JLabel(config.connect.getIcon()))
            add(JLabel(config.name))
        }

        this.root = root
        return root
    }

    override fun getTopRootNode(): RecursiveTreeNode {
        return this
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultHostMachineTreeNode

        return config == other.config
    }

    override fun hashCode(): Int {
        return config.hashCode()
    }


}