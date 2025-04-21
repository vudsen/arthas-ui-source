package io.github.vudsen.arthasui.core

import com.intellij.execution.ExecutorRegistry
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.configurations.ConfigurationTypeUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.progress.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowId
import io.github.vudsen.arthasui.api.ArthasExecutionManager
import io.github.vudsen.arthasui.api.bean.VirtualFileAttributes
import io.github.vudsen.arthasui.core.ui.ExecutionGutterIconRenderer
import io.github.vudsen.arthasui.run.ArthasConfigurationFactory
import io.github.vudsen.arthasui.run.ArthasConfigurationTypeBaseImpl

/**
 * Arthas Query Console header actions group.
 */
class ArthasQueryConsoleActionGroup(
    private val project: Project,
    private val editorEx: EditorEx,
    private val virtualFileAttributes: VirtualFileAttributes
) : ActionGroup() {

    private var lastHighlighter: RangeHighlighter? = null

    /**
     * 移除回车以及命令结尾的分号
     */
    private fun compactCommand(cmd: String): String {
        val builder = StringBuilder(cmd.length)
        var skipWhiteSpace = false
        for (ch in cmd) {
            if (ch == '\n') {
                // 清除左右空格，只保留一个
                for (i in builder.length - 1 downTo 0) {
                    if (builder[i] == ' ') {
                        builder.deleteAt(i)
                    } else {
                        break
                    }
                }
                skipWhiteSpace = true
                builder.append(' ')
                continue
            } else if (skipWhiteSpace) {
                if (ch == ' ') {
                    continue
                }
                skipWhiteSpace = false
            }
            builder.append(ch)
        }
        for (i in builder.length - 1 downTo 0) {
            val ch = builder[i]
            if (ch == ' ' || ch == ';') {
                builder.deleteAt(i)
            } else {
                break
            }
        }
        return builder.toString()
    }

    /**
     * 获取对应的 [ArthasBridge] 实例，若对应的 Bridge 还没有被创建，则创建并缓存
     */
    fun runSelected(editorEx: EditorEx) {
        val selected = compactCommand(editorEx.selectionModel.selectedText ?: return)


        lastHighlighter?.let {
            editorEx.markupModel.removeHighlighter(it)
        }

        val highlighter = editorEx.markupModel.addRangeHighlighter(
            null,
            editorEx.selectionModel.selectionStart,
            editorEx.selectionModel.selectionEnd,
            0,
            HighlighterTargetArea.EXACT_RANGE
        )
        lastHighlighter = highlighter
        highlighter.gutterIconRenderer = ExecutionGutterIconRenderer(editorEx)

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, selected, true) {

            override fun run(indicator: ProgressIndicator) {
                runBlockingCancellable {
                    val coordinator = project.getService(ArthasExecutionManager::class.java)
                    coordinator.getTemplate(virtualFileAttributes.jvm)?.let {
                        it.execute(selected)
                        return@runBlockingCancellable
                    }

                    val arthasBridgeTemplate = blockingContext {
                         coordinator.initTemplate(virtualFileAttributes.jvm, virtualFileAttributes.hostMachineConfig, virtualFileAttributes.providerConfig, indicator)
                    }

                    val configurationType =
                        ConfigurationTypeUtil.findConfigurationType(ArthasConfigurationTypeBaseImpl::class.java)

                    val runnerAndConfigurationSettings = RunManager.getInstance(project)
                        .createConfiguration(virtualFileAttributes.jvm.name, ArthasConfigurationFactory(configurationType, virtualFileAttributes.jvm))

                    runnerAndConfigurationSettings.isTemporary = true

                    ProgramRunnerUtil.executeConfiguration(
                        runnerAndConfigurationSettings,
                        ExecutorRegistry.getInstance().getExecutorById(ToolWindowId.RUN)!!
                    )

                    arthasBridgeTemplate.waitUntilAttached()
                    arthasBridgeTemplate.execute(selected)
                }
            }

            override fun onFinished() {
                editorEx.markupModel.removeHighlighter(highlighter)
            }

        })

    }


    private val actions: Array<AnAction> = arrayOf(
        object : AnAction(AllIcons.RunConfigurations.TestState.Run) {


            override fun actionPerformed(e: AnActionEvent) {
                runSelected(editorEx)
            }

        }
    )

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return actions
    }


}