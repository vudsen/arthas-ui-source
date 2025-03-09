package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import io.github.vudsen.arthasui.api.CloseableHostMachine
import io.github.vudsen.arthasui.api.ui.CloseableTreeNode
import io.github.vudsen.arthasui.conf.HostMachineConfigV2

/**
 * 默认可关闭的树节点
 */
class DefaultCloseableTreeNode(config: HostMachineConfigV2, project: Project) : DefaultHostMachineTreeNode(config, project),
    CloseableTreeNode {

    private fun getHostMachine(): CloseableHostMachine = ctx.hostMachine as CloseableHostMachine

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