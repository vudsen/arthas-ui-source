package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.util.minimumHeight
import com.intellij.ui.util.preferredHeight
import com.intellij.ui.util.preferredWidth
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.common.util.collectStackTrace
import io.github.vudsen.arthasui.script.MyOgnlContext
import io.github.vudsen.arthasui.script.OgnlJvmSearcher
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.util.concurrent.Future
import javax.swing.*

class ScriptTestsDialog(
    private val script: String,
    private val context: MyOgnlContext
) : DialogWrapper(true) {

    companion object {
        private val logger = Logger.getInstance(ScriptTestsDialog::class.java)
    }

    private lateinit var future: Future<*>

    init {
        this.title = "Test Script"
        isOKActionEnabled = false
        init()

    }

    private lateinit var myRootPanel: JPanel


    override fun createCenterPanel(): JComponent {
        future = ApplicationManager.getApplication().executeOnPooledThread {
            try {
                OgnlJvmSearcher.execute(
                    script,
                    context
                )
                val resultHolder = context.getResultHolder()
                setSuccess(resultHolder.result, resultHolder.collectDebugMessages())
            } catch (e: Exception) {
                setFailed(e.collectStackTrace())
                if (logger.isDebugEnabled) {
                    logger.debug("Failed to execute script", e)
                }
            }
        }
        val root = JPanel(BorderLayout())
        root.add(JLabel(AnimatedIcon.Default.INSTANCE))
        this.myRootPanel = root
        root.preferredWidth = 500
        return root
    }

    private fun createJvmDisplayList(result: List<JVM>): JBList<JVM> {
        val list = JBList(result)

        list.setCellRenderer { _, value, _, _, _ ->
            val panel = JPanel(FlowLayout())
            panel.add(JLabel(value.getIcon()))
            panel.add(JLabel(value.name))
            panel.accessibleContext.accessibleName = value.name
            panel
        }
        return list
    }


    private fun setSuccess(result: List<JVM>, debugMessage: String) {
        isOKActionEnabled = true
        myRootPanel.removeAll()


        val contentPanel = panel {
            group("Searched JVM") {
                row {
                    cell(createJvmDisplayList(result))
                }
            }
            group("Debug Messages") {
                row {
                    cell(JBTextArea(debugMessage).apply {
                        lineWrap = true
                        minimumHeight = 200
                    }).align(Align.FILL)
                }
            }
        }

        myRootPanel.add(contentPanel, BorderLayout.CENTER)
        myRootPanel.updateUI()
    }

    private fun setFailed(stackTrace: String) {
        myRootPanel.removeAll()
        myRootPanel.preferredHeight = 500
        myRootPanel.add(JBScrollPane(JBTextArea(stackTrace).apply {
            lineWrap = true
        }, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER)
        myRootPanel.updateUI()
    }

    override fun doCancelAction() {
        super.doCancelAction()
        if (!future.isDone) {
            future.cancel(true)
        }
    }


    override fun dispose() {
        super.dispose()
        if (!future.isDone) {
            future.cancel(true)
        }
    }
}