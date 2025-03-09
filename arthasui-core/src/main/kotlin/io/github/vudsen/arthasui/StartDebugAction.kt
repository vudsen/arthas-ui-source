package io.github.vudsen.arthasui

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.testFramework.LightVirtualFile


class StartDebugAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        val fileEditorManager = FileEditorManager.getInstance(project!!)

        val lightVirtualFile = LightVirtualFile("Arthas Query Console", io.github.vudsen.arthasui.language.arthas.psi.ArthasFileType, "")
        val editor = fileEditorManager.openFile(lightVirtualFile, true)

        println(editor)
    }

}