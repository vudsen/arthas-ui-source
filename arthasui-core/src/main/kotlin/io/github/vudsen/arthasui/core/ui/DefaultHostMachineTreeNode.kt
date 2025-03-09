package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.github.vudsen.arthasui.api.HostMachineFactory
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import io.github.vudsen.arthasui.conf.HostMachineConfigV2
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.bridge.JvmSearcher
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTree

/**
 * TODO, remove the CloseableTreeNode and move all the method in TreeNodeHostMachine itself.
 */
open class DefaultHostMachineTreeNode(private val config: HostMachineConfigV2, project: Project) : AbstractRecursiveTreeNode() {


    protected val ctx: TreeNodeContext


    init {
        val factory = service<HostMachineFactory>()
        val hostMachine = factory.getHostMachine(config.connect)
        ctx = TreeNodeContext(hostMachine, this, project, JvmSearcher(hostMachine))
    }

    fun getConnectConfig(): HostMachineConnectConfig {
        return config.connect
    }

    override fun refresh(): List<AbstractRecursiveTreeNode> {
        val result = mutableListOf<AbstractRecursiveTreeNode>()
        for (provider in config.providers) {
            result.add(TreeNodeJvmProviderFolder(ctx, provider))
        }
        return result
    }


    override fun render(tree: JTree): JComponent {
        return JPanel(FlowLayout(FlowLayout.LEFT, 0, 5)).apply {
            add(JLabel(config.connect.getIcon()))
            add(JLabel(config.name))
        }
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