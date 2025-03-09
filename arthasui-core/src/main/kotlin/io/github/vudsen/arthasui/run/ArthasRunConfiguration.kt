package io.github.vudsen.arthasui.run

import com.intellij.diagnostic.logging.LogConsoleManagerBase
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.execution.ui.ConsoleView
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.run.ui.ExecuteHistoryUI

class ArthasRunConfiguration(
    project: Project,
    configurationFactory: ConfigurationFactory,
    private val jvm: JVM,
) :
    RunConfigurationBase<EmptyOptions>(project, configurationFactory, "Arthas Query Console") {

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return object : CommandLineState(environment) {
            override fun startProcess(): ProcessHandler {
                return ArthasProcessHandler(
                    environment.project,
                    jvm,
                )
            }

            override fun createConsole(executor: Executor): ConsoleView? {
                return super.createConsole(executor)
            }

            override fun createActions(
                console: ConsoleView?,
                processHandler: ProcessHandler?,
                executor: Executor?
            ): Array<AnAction> {
                return arrayOf(
                    object : AnAction(AllIcons.Actions.AddList) {
                        override fun actionPerformed(e: AnActionEvent) {
                            println(e)
                        }
                    }
                )
            }
        }
    }

    override fun getConfigurationEditor(): SettingsEditor<out RunConfiguration> {
        return ArthasSettingsEditor()
    }

    override fun createRunnerSettings(provider: ConfigurationInfoProvider?): ConfigurationPerRunnerSettings? {
        return super.createRunnerSettings(provider)
    }

    override fun createAdditionalTabComponents(
        manager: AdditionalTabComponentManager,
        startedProcess: ProcessHandler
    ) {
        if (manager is LogConsoleManagerBase) {
            manager.addAdditionalTabComponent(ExecuteHistoryUI(project, jvm), "io.github.vudsen.arthasui.run.ui.ExecuteHistoryUI", null, false)
        }

    }

}