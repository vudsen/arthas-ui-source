package io.github.vudsen.arthasui.core

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.github.vudsen.arthasui.api.ArthasExecutionManager
import io.github.vudsen.arthasui.language.arthas.psi.ArthasFileType

class ArthasQueryConsoleEditorProvider : FileEditorProvider, DumbAware {

    override fun accept(project: Project, file: VirtualFile): Boolean {
        return file.fileType == ArthasFileType && file.getUserData(ArthasExecutionManager.VF_ATTRIBUTES) != null
    }


    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return ArthasQueryConsoleEditor(project, file, TextEditorProvider.getInstance())
    }

    override fun getEditorTypeId(): String {
        return "Arthas Query Console"
    }

    override fun getPolicy(): FileEditorPolicy {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR
    }

}