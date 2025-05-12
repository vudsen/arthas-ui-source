package io.github.vudsen.arthasui.run

import com.intellij.execution.configurations.*
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project


class ArthasConfigurationFactory(
    configurationType: ConfigurationType
) : ConfigurationFactory(configurationType) {

    override fun isApplicable(project: Project): Boolean {
        return false
    }

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return ArthasRunConfiguration(project, this)
    }

    override fun getOptionsClass(): Class<out BaseState> {
        return ArthasProcessOptions::class.java
    }

    override fun getId(): String {
        return ArthasConfigurationType.ID
    }

    override fun getName(): String {
        return "ArthasConfigurationFactory"
    }

}