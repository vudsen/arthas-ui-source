package io.github.vudsen.arthasui.run

import com.intellij.diagnostic.logging.LogConsoleManagerBase
import com.intellij.execution.Executor
import com.intellij.execution.configurations.*
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.run.ui.ExecuteHistoryUI

class ArthasRunConfiguration(
    project: Project,
    configurationFactory: ConfigurationFactory
) :
    RunConfigurationBase<ArthasProcessOptions>(project, configurationFactory, "Arthas Query Console") {

    override fun getState(executor: Executor, environment: ExecutionEnvironment): RunProfileState {
        return object : CommandLineState(environment) {
            override fun startProcess(): ProcessHandler {
                return ArthasProcessHandler(
                    project,
                    state.jvm
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

    override fun getState(): ArthasProcessOptions {
        return super.getState()!!
    }

    override fun createAdditionalTabComponents(
        manager: AdditionalTabComponentManager,
        startedProcess: ProcessHandler
    ) {
        if (manager is LogConsoleManagerBase) {
            manager.addAdditionalTabComponent(ExecuteHistoryUI(project, state.jvm), "io.github.vudsen.arthasui.run.ui.ExecuteHistoryUI", null, false)
        }

    }

}