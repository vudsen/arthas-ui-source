package io.github.vudsen.arthasui.core

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.editor.impl.EditorImpl
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl
import com.intellij.openapi.fileEditor.impl.text.TextEditorComponent
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.github.vudsen.arthasui.api.ArthasExecutionManager

class ArthasQueryConsoleEditor(project: Project, virtualFile: VirtualFile, provider: TextEditorProvider) : PsiAwareTextEditorImpl(project, virtualFile, provider) {


    override fun createEditorComponent(project: Project, file: VirtualFile, editor: EditorImpl): TextEditorComponent {
        val editorComponent =  super.createEditorComponent(project, file, editor)

        val attributes = file.getUserData(ArthasExecutionManager.VF_ATTRIBUTES) ?: return editorComponent


        val actionToolbar =
            ActionManager.getInstance()
                .createActionToolbar(
                    ActionPlaces.EDITOR_TOOLBAR, ArthasQueryConsoleActionGroup(
                        project,
                        editorComponent.editor,
                        attributes
                    ), true
                )
        actionToolbar.targetComponent = editorComponent

        editorComponent.editor.headerComponent = actionToolbar.component
        return editorComponent;
    }

    override fun getName(): String {
        return "Arthas Query Console"
    }


}