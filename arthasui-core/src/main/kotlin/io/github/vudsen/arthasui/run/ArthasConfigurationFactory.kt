package io.github.vudsen.arthasui.run

import com.intellij.execution.configurations.*
import com.intellij.openapi.components.BaseState
import com.intellij.openapi.project.Project
import io.github.vudsen.arthasui.api.JVM


class ArthasConfigurationFactory(
    configurationType: ConfigurationType,
    private val jvm: JVM,
) : ConfigurationFactory(configurationType) {

    override fun isApplicable(project: Project): Boolean {
        return false
    }

    override fun createTemplateConfiguration(project: Project): RunConfiguration {
        return ArthasRunConfiguration(project, this, jvm)
    }

    override fun getOptionsClass(): Class<out BaseState> {
        return EmptyOptions::class.java
    }

    override fun getId(): String {
        return jvm.toString()
    }

    override fun getName(): String {
        return jvm.name
    }

}