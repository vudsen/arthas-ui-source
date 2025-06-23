package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.getUserData
import com.intellij.ui.treeStructure.Tree
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.bridge.util.JvmProviderSearcher
import java.awt.FlowLayout
import java.lang.ref.WeakReference
import javax.swing.*

/**
 * 默认的根节点
 */
open class DefaultHostMachineTreeNode(
    val config: HostMachineConfig,
    project: Project,
    tree: Tree
) : AbstractRecursiveTreeNode() {


    protected val ctx: TreeNodeContext

    init {
        val factory = service<HostMachineConnectManager>()
        val hostMachine = factory.connect(config)
        ctx = TreeNodeContext(hostMachine, this, project, config, tree)
    }

    fun getConnectConfig(): HostMachineConfig {
        return config
    }

    override fun refresh(): List<AbstractRecursiveTreeNode> {
        val result = mutableListOf<AbstractRecursiveTreeNode>()
        val jvmProviderManager = service<JvmProviderManager>()
        for (providerConfig in config.providers) {
            if (!providerConfig.enabled) {
                continue
            }
            val provider = jvmProviderManager.getProvider(providerConfig)
            result.add(
                TreeNodeSearcher(
                    JvmProviderSearcher(provider, providerConfig, ctx.template),
                    ctx,
                    providerConfig
                )
            )
        }
        for (searchGroup in config.searchGroups) {
            result.add(CustomSearchGroupTreeNode(searchGroup, ctx))
        }
        return result
    }

    override fun getIcon(): Icon {
        return config.connect.getIcon()
    }

    override fun resolveText(): JLabel {
        return JLabel(config.name).apply {
            border = BorderFactory.createEmptyBorder(0, 4, 0, 0)
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

    override fun toString(): String {
        return config.name
    }


}