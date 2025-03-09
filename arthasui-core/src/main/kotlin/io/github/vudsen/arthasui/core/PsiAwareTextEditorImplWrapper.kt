package io.github.vudsen.arthasui.core

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl
import com.intellij.openapi.project.Project
import io.github.vudsen.arthasui.api.ArthasExecutionManager
import javax.swing.JComponent

/**
 * bypass 'internal' limitation.
 */
class PsiAwareTextEditorImplWrapper(private val delegate: FileEditor, private val project: Project) : FileEditor by delegate {



    override fun getComponent(): JComponent {
        val editorComponent = delegate.component
        val attributes = file.getUserData(ArthasExecutionManager.VF_ATTRIBUTES) ?: return editorComponent

        val editor = (delegate as PsiAwareTextEditorImpl).editor
        val actionToolbar =
            ActionManager.getInstance()
                .createActionToolbar(
                    ActionPlaces.EDITOR_TOOLBAR, ArthasQueryConsoleActionGroup(
                        project,
                        editor,
                        attributes
                    ), true
                )
        actionToolbar.targetComponent = editorComponent

        editor.headerComponent = actionToolbar.component
        return editorComponent;
    }

    override fun getName(): String {
        return "Arthas Query Console"
    }

}