package io.github.vudsen.arthasui.run

import com.intellij.execution.configurations.RunConfigurationOptions
import io.github.vudsen.arthasui.api.JVM

class ArthasProcessOptions(var jvm: JVM) : RunConfigurationOptions()