package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import io.github.vudsen.arthasui.common.ui.AbstractRecursiveTreeNode
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.bridge.util.JvmProviderSearcher
import java.awt.FlowLayout
import javax.swing.*

/**
 * 默认的根节点
 */
open class DefaultHostMachineTreeNode(val config: HostMachineConfig, project: Project) : AbstractRecursiveTreeNode() {


    protected val ctx: TreeNodeContext

    private var root: JComponent? = null

    init {
        val factory = service<HostMachineConnectManager>()
        val hostMachine = factory.connect(config)
        ctx = TreeNodeContext(hostMachine, this, project, config)
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
            if (provider.isHideCurrent()) {
                val searchJvm = provider.searchJvm(ctx.template, providerConfig)
                searchJvm.childs?.let {
                    for (delegate in it) {
                        result.add(
                            TreeNodeSearcher(
                                delegate,
                                ctx,
                                providerConfig
                            )
                        )
                    }
                }
                searchJvm.result ?.let {
                    for (jvm in it) {
                        result.add(TreeNodeJVM(ctx.root, providerConfig, jvm))
                    }
                }
                continue
            }
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


    override fun render(tree: JTree): JComponent {
        root?.let { return it }
        val root = JPanel(FlowLayout(FlowLayout.LEFT, 0, 5)).apply {
            add(JLabel(config.connect.getIcon()))
            add(JLabel(config.name).apply {
                border = BorderFactory.createEmptyBorder(0, 4, 0, 0)
            })
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