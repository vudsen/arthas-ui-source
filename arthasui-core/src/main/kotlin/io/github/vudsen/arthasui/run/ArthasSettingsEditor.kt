package io.github.vudsen.arthasui.run

import com.intellij.openapi.options.SettingsEditor
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

/**
 * 空实现。必须通过设置来添加相关配置，不允许在 run configuration 直接配置
 */
class ArthasSettingsEditor : SettingsEditor<ArthasRunConfiguration>() {


    override fun resetEditorFrom(s: ArthasRunConfiguration) {}

    override fun applyEditorTo(s: ArthasRunConfiguration) {}


    override fun createEditor(): JComponent {
        return panel {
            row {
                text("Please use settings to create arthas run configuration.")
            }
        }
    }

}