package io.github.vudsen.arthasui.common.ui

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.ScrollPaneConstants

class SimpleDialog(title: String, private val content: String) : DialogWrapper(false) {

    init {
        this.title = title
        init()
    }

    override fun createCenterPanel(): JComponent {
        val jbScrollPane = JBScrollPane(JBTextArea(content), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED)
        jbScrollPane.preferredSize = Dimension(300, 200)
        return jbScrollPane
    }
}