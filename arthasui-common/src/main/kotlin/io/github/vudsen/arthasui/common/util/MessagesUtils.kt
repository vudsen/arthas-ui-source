package io.github.vudsen.arthasui.common.util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages

object MessagesUtils {

    fun showErrorMessageLater(title: String, messages: String?, project: Project?) {
        ApplicationManager.getApplication().invokeLater {
            Messages.showErrorDialog(project, messages, title)
        }
    }


}