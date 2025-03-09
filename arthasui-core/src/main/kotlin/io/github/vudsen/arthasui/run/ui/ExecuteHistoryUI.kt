package io.github.vudsen.arthasui.run.ui

import com.intellij.diagnostic.logging.AdditionalTabComponent
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.HighlighterColors
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.project.Project
import com.intellij.ui.CollectionListModel
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import io.github.vudsen.arthasui.api.ArthasBridgeListener
import io.github.vudsen.arthasui.api.ArthasExecutionManager
import io.github.vudsen.arthasui.api.ArthasResultItem
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.exception.BridgeException
import io.github.vudsen.arthasui.common.util.printStackTraceToString
import io.github.vudsen.arthasui.util.ui.CardJPanel
import io.github.vudsen.arthasui.util.ui.TagLabel
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.util.LinkedList
import javax.swing.*

class ExecuteHistoryUI(project: Project, jvm: JVM) : AdditionalTabComponent() {

    private val historyCommand = CollectionListModel<CardItem>(LinkedList(), true)

    private var contentRight = CardJPanel()

    companion object {
        private enum class Tag(val color: Color) {
            SUCCESS(EditorColorsManager.getInstance().globalScheme.getAttributes(CodeInsightColors.MATCHED_BRACE_ATTRIBUTES).backgroundColor),
            ERROR(EditorColorsManager.getInstance().globalScheme.getAttributes(HighlighterColors.BAD_CHARACTER).backgroundColor);
        }
        private data class CardItem (
            var command: String,
            var id: String,
            var tag: Tag
        )
        val EXCEPTION_CHECK_PATTER = Regex("^(\\w+\\.)+\\w+Exception")
    }

    init {
        layout = BorderLayout()
        val splitter = JBSplitter(false, 0.2f, 0.05f, 0.95f).apply {
            firstComponent = createHistoryLeft()
            secondComponent = contentRight
        }
        add(splitter, BorderLayout.CENTER)
        splitter.divider.border = JBUI.Borders.customLineRight(JBUI.CurrentTheme.Toolbar.SEPARATOR_COLOR)
        val coordinator = project.getService(ArthasExecutionManager::class.java)
        val template = coordinator.getTemplate(jvm)!!

        template.addListener(object : ArthasBridgeListener() {

            override fun onFinish(command: String, result: ArthasResultItem, rawContent: String) {
                val item = CardItem(
                    command,
                    System.currentTimeMillis().toString(),
                    if (isCommandExecuteFailed(rawContent)) Tag.ERROR else Tag.SUCCESS
                )
                historyCommand.add(0, item)

                ApplicationManager.getApplication().invokeLater {
                    contentRight.add(HistoryDetailUI(project, rawContent, result), item.id)
                }
            }

            override fun onError(command: String, rawContent: String, exception: Exception) {
                val item = CardItem(command, System.currentTimeMillis().toString(), Tag.ERROR)
                historyCommand.add(0, item)

                ApplicationManager.getApplication().invokeLater {
                    if (exception is BridgeException) {
                        contentRight.add(HistoryDetailUI(project, rawContent, null), item.id)
                    } else {
                        contentRight.add(HistoryDetailUI(project, rawContent +
                                "\n\nPlugin Stack Trace(Our plugin is currently unstable, and we cannot determine whether the error originates from our plugin or your command. Therefore, we are providing the full stack trace below.):\n"
                                + exception.printStackTraceToString(), null), item.id)
                    }
                }
            }
        })
    }

    private fun isCommandExecuteFailed(frame: String): Boolean {
        return EXCEPTION_CHECK_PATTER.containsMatchIn(frame)
    }

    override fun dispose() {

    }

    override fun getPreferredFocusableComponent(): JComponent {
        return this
    }

    override fun getToolbarActions(): ActionGroup? {
        return null
    }

    override fun getSearchComponent(): JComponent? {
        return null
    }

    override fun getToolbarPlace(): String {
        return ActionPlaces.TOOLBAR
    }

    override fun getToolbarContextComponent(): JComponent? {
        return null
    }

    override fun isContentBuiltIn(): Boolean {
        return false
    }

    override fun getTabTitle(): String {
        return "History"
    }

    private fun createHistoryLeft(): JComponent {
        val list = JBList(historyCommand).apply {
            selectionMode = ListSelectionModel.SINGLE_SELECTION
            setEmptyText("No command executed yet")
            installCellRenderer { cell ->
                JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                    val label = TagLabel(cell.tag.name, cell.tag.color)
                    add(label)
                    add(JBLabel(cell.command))
                }
            }
        }
        list.addListSelectionListener { evt ->
            val jbList = evt.source as JBList<*>
            contentRight.layout.show(contentRight, historyCommand.getElementAt(jbList.selectedIndex).id)
        }
        val pane = JBScrollPane(list)
        return pane
    }




}