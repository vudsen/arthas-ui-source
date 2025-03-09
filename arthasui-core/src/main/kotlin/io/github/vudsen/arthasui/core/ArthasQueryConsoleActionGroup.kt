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
import io.github.vudsen.arthasui.run.ArthasConfigurationTypeBaseImpl
import io.github.vudsen.arthasui.core.ui.ExecutionGutterIconRenderer
import io.github.vudsen.arthasui.run.ArthasConfigurationFactory
import kotlinx.coroutines.runBlocking

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
     * 获取对应的 [ArthasBridge] 实例，若对应的 Bridge 还没有被创建，则创建并缓存
     */
    fun runSelected(editorEx: EditorEx) {
        val selected = editorEx.selectionModel.selectedText ?: return


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

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Execute command", true) {

            override fun run(indicator: ProgressIndicator) {
                runBlockingCancellable {
                    val coordinator = project.getService(ArthasExecutionManager::class.java)
                    coordinator.getTemplate(virtualFileAttributes.jvm)?.let {
                        it.execute(selected)
                        return@runBlockingCancellable
                    }

                    val arthasBridgeTemplate = blockingContext {
                         coordinator.initTemplate(virtualFileAttributes.jvm, virtualFileAttributes.connectConfig, virtualFileAttributes.providerConfig)
                    }

                    val configurationType =
                        ConfigurationTypeUtil.findConfigurationType(ArthasConfigurationTypeBaseImpl::class.java)

                    val runnerAndConfigurationSettings = RunManager.getInstance(project)
                        .createConfiguration(virtualFileAttributes.jvm.getDisplayName(), ArthasConfigurationFactory(configurationType, virtualFileAttributes.jvm))

                    runnerAndConfigurationSettings.isTemporary = true

                    ProgramRunnerUtil.executeConfiguration(
                        runnerAndConfigurationSettings,
                        ExecutorRegistry.getInstance().getExecutorById(ToolWindowId.RUN)!!
                    )

                    arthasBridgeTemplate.waitUntilAttached()
                    arthasBridgeTemplate.execute(selected)
                }
            }

            override fun onCancel() {
                runBlocking {
                    val coordinator = project.getService(ArthasExecutionManager::class.java)
                    coordinator.getTemplate(virtualFileAttributes.jvm)?.cancel()
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

        },
        object : AnAction(AllIcons.Actions.Suspend) {

            override fun getActionUpdateThread(): ActionUpdateThread {
                return ActionUpdateThread.EDT
            }

            override fun actionPerformed(e: AnActionEvent) {
                runBlocking {
                    val coordinator = project.getService(ArthasExecutionManager::class.java)
                    coordinator.getTemplate(virtualFileAttributes.jvm)?.cancel()
                }
            }

            override fun update(e: AnActionEvent) {
                val coordinator = project.getService(ArthasExecutionManager::class.java)
                coordinator.getTemplate(virtualFileAttributes.jvm) ?.let {
                    e.presentation.isEnabled = it.isBusy()
                } ?: let {
                    e.presentation.isEnabled = false
                }
            }

        }
    )

    override fun getChildren(e: AnActionEvent?): Array<AnAction> {
        return actions
    }


}