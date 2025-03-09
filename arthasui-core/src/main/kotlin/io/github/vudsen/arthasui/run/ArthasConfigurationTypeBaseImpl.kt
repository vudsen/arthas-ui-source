package io.github.vudsen.arthasui.run

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.icons.AllIcons

/**
 * Use [com.intellij.execution.configurations.ConfigurationTypeUtil.findConfigurationType] to retrieve this instance
 */
class ArthasConfigurationTypeBaseImpl :
    ConfigurationTypeBase(
        "io.github.vudsen.arthasui.run.ArthasConfigurationTypeBaseImpl",
        "Arthas",
        "Run arthas",
        AllIcons.Ide.Gift
    )