package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.ui.CloseableTreeNode
import io.github.vudsen.arthasui.api.conf.HostMachineConfig

/**
 * 默认可关闭的树节点
 */
class DefaultCloseableTreeNode(config: HostMachineConfig, project: Project, tree: Tree) : DefaultHostMachineTreeNode(config, project, tree),
    CloseableTreeNode {

    private fun getHostMachine(): CloseableHostMachine = ctx.template as CloseableHostMachine

    override fun isActive(): Boolean {
        return !getHostMachine().isClosed()
    }

    override fun close() {
        getHostMachine().close()
    }

    override fun dispose() {
        this.close()
    }

}