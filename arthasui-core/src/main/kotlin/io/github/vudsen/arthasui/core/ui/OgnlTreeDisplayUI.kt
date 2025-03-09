package io.github.vudsen.arthasui.core.ui

import com.intellij.icons.AllIcons
import com.intellij.ui.JBColor
import com.intellij.ui.treeStructure.Tree
import io.github.vudsen.arthasui.common.lang.model.ArthasArray
import io.github.vudsen.arthasui.common.lang.model.ArthasMap
import io.github.vudsen.arthasui.common.lang.model.ArthasObject
import io.github.vudsen.arthasui.common.lang.model.ArthasValue
import io.github.vudsen.arthasui.api.ArthasResultItem
import java.awt.Component
import java.awt.FlowLayout
import javax.swing.*
import javax.swing.event.TreeModelListener
import javax.swing.tree.*

class OgnlTreeDisplayUI(rootItem: ArthasResultItem) {

    private val tree: Tree = Tree(OgnlTreeModel(rootItem))


    init {
        tree.cellRenderer = OgnlTreeCellRenderer()
        tree.background = JBColor.WHITE
        tree.isOpaque = true
    }

    fun getComponent(): Tree {
        return tree;
    }


    companion object {

        private class OgnlTreeModel(private val rootItem: ArthasResultItem) : TreeModel {
            override fun getRoot(): ArthasResultItem {
                return rootItem
            }

            override fun getChild(parent: Any, index: Int): Any {
                when (parent) {
                    is ArthasArray -> {
                        return parent.values[index]
                    }

                    is ArthasMap -> {
                        var p = index
                        val it = parent.entries.iterator()
                        while (p > 0) {
                            p--
                            it.next()
                        }
                        return it.next()
                    }

                    is ArthasObject -> {
                        var p = index
                        val it = parent.fields.iterator()
                        while (p > 0) {
                            p--
                            it.next()
                        }
                        return it.next()
                    }
                    else -> throw IllegalArgumentException("Invalid node: ${parent::class.qualifiedName}")
                }
            }

            override fun getChildCount(parent: Any): Int {
                return when (parent) {
                    is ArthasObject -> parent.fields.size
                    is ArthasArray -> parent.values.size
                    is ArthasMap -> parent.entries.size
                    else -> 0
                }
            }

            override fun isLeaf(node: Any): Boolean {
                return node is ArthasValue
            }

            override fun valueForPathChanged(path: TreePath?, newValue: Any?) {
                // TODO
            }

            override fun getIndexOfChild(parent: Any, child: Any): Int {
                when (parent) {
                    is ArthasArray -> {
                        return parent.values.indexOf(child)
                    }

                    is ArthasMap -> {
                        var p = 0
                        for (entry in parent.entries) {
                            if (entry == child) {
                                return p
                            }
                            p++
                        }
                    }

                    is ArthasObject -> {
                        var p = 0
                        for (entry in parent.fields) {
                            if (entry == child) {
                                return p
                            }
                            p++
                        }
                    }
                    else -> throw IllegalArgumentException("Invalid node: ${parent::class.qualifiedName}")
                }
                return -1
            }

            override fun addTreeModelListener(l: TreeModelListener?) {

            }

            override fun removeTreeModelListener(l: TreeModelListener?) {

            }
        }

        private class OgnlTreeCellRenderer : TreeCellRenderer {

            override fun getTreeCellRendererComponent(
                tree: JTree?,
                value: Any?,
                selected: Boolean,
                expanded: Boolean,
                leaf: Boolean,
                row: Int,
                hasFocus: Boolean
            ): Component {
                if (value !is ArthasResultItem) {
                    throw IllegalStateException()
                }

                return JPanel(FlowLayout(FlowLayout.LEFT, 0, 5)).apply {
                    add(JLabel(getIcon(value)).apply {
                        border = BorderFactory.createEmptyBorder(0, 0, 0, 5)
                        verticalAlignment = SwingConstants.CENTER
                    })
                    add(JLabel(value.toString()).apply {
                        verticalAlignment = SwingConstants.CENTER
                    })
                }
            }

            private fun getIcon(value: ArthasResultItem): Icon {
                if (value is ArthasValue) {
                    return AllIcons.Nodes.Field
                } else {
                    return AllIcons.Nodes.Class
                }
            }

        }

    }


}