package io.github.vudsen.arthasui.core

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import io.github.vudsen.arthasui.core.ui.HostMachineToolWindowV2

class HostMachineToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val hostMachineToolWindow = HostMachineToolWindowV2(project)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(hostMachineToolWindow.getComponent(), "", false)
        Disposer.register(toolWindow.contentManager, content)
        toolWindow.contentManager.addContent(content)
    }

}