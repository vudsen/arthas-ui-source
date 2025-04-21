package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.project.Project
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.template.HostMachineTemplate

class TreeNodeContext(
    val template: HostMachineTemplate,
    val root: RecursiveTreeNode,
    val project: Project,
    val config: HostMachineConfig
)
