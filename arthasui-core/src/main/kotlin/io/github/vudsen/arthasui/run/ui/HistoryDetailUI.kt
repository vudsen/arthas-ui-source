package io.github.vudsen.arthasui.run.ui

import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTabbedPane
import io.github.vudsen.arthasui.core.ui.OgnlTreeDisplayUI
import io.github.vudsen.arthasui.api.ArthasResultItem
import io.github.vudsen.arthasui.common.bean.StringResult
import javax.swing.BorderFactory

class HistoryDetailUI(project: Project, raw: String, result: ArthasResultItem?) : JBTabbedPane() {


    init {
        val consoleViewImpl = ConsoleViewImpl(project, true)
        // help init
        consoleViewImpl.component.border = null
        consoleViewImpl.component.border = BorderFactory.createEmptyBorder(-5, -10, -5,-10)
        consoleViewImpl.editor.document.setText(raw)
        consoleViewImpl.editor.settings.isUseSoftWraps = true
        addTab("Raw", consoleViewImpl)

        if (result is StringResult) {
            addTab("Parsed", JBLabel("No parsed view available."))
        } else if (result is ArthasResultItem) {
            addTab("Parsed", OgnlTreeDisplayUI(result).getComponent())
        }
    }

}