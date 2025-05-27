package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.ui.RecursiveTreeNode
import io.github.vudsen.arthasui.api.conf.HostMachineConfig

class TreeNodeContext(
    val template: HostMachine,
    val root: RecursiveTreeNode,
    val project: Project,
    val config: HostMachineConfig,
    val tree: Tree
)
