package io.github.vudsen.arthasui.common.ui

import javax.swing.tree.DefaultMutableTreeNode

/**
 * 假的可展开节点。即使没有子节点，也可以设置可展开
 */
class FakeExpandableTreeNode(uo: Any) : DefaultMutableTreeNode(uo) {

    private var myIsLeafFlag = false

    fun setLeaf(isLeaf: Boolean) {
        myIsLeafFlag = isLeaf
    }

    override fun isLeaf(): Boolean {
        return myIsLeafFlag
    }

}