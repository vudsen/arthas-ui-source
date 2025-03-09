package io.github.vudsen.arthasui.core.ui

import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.util.registry.Registry
import com.intellij.ui.AnimatedIcon
import com.intellij.util.IconUtil.scale
import javax.swing.Icon
import kotlin.math.abs

class ExecutionGutterIconRenderer(private val editorEx: EditorEx) : GutterIconRenderer() {

    private fun createLoadingIcon(editorEx: EditorEx): Icon {
        val loadingIcon = scaleIcon(AnimatedIcon.Default.INSTANCE, editorEx)
        return loadingIcon
    }

    private fun scaleIcon(icon: Icon, editorEx: EditorEx): Icon {
        val scale: Float = getEditorScaleFactor(editorEx)
        return if (scale == 1f) icon else scale(icon, editorEx.gutterComponentEx, scale)
    }

    private fun getEditorScaleFactor(editorEx: EditorEx): Float {
        if (Registry.`is`("editor.scale.gutter.icons") && editorEx is EditorImpl) {
            val scale: Float = editorEx.getScale()
            if (abs((1f - scale).toDouble()) > 0.10f) {
                return scale
            }
        }
        return 1f
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ExecutionGutterIconRenderer
        return this.editorEx == other.editorEx
    }

    override fun hashCode(): Int {
        return editorEx.hashCode()
    }

    override fun getIcon(): Icon {
        return createLoadingIcon(editorEx)
    }

}