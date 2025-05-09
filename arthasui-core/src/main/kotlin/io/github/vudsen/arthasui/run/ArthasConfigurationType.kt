package io.github.vudsen.arthasui.run

import com.intellij.execution.configurations.ConfigurationTypeBase
import com.intellij.icons.AllIcons

/**
 * Use [com.intellij.execution.configurations.ConfigurationTypeUtil.findConfigurationType] to retrieve this instance
 */
class ArthasConfigurationType :
    ConfigurationTypeBase(
        ID,
        "Arthas",
        "Run arthas",
        AllIcons.Ide.Gift
    ) {

        init {
            addFactory(ArthasConfigurationFactory(this))
        }

        companion object {
            const val ID = "io.github.vudsen.arthasui.run.ArthasConfigurationTypeBaseImpl"
        }
    }