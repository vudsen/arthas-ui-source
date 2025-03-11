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
        return AnimatedIcon.Default.INSTANCE
    }

}