package io.github.vudsen.arthasui.common.util

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import io.github.vudsen.arthasui.api.exception.AppException
import java.lang.reflect.UndeclaredThrowableException

object MessagesUtils {

    fun showErrorMessageLater(title: String, messages: String?, project: Project?) {
        ApplicationManager.getApplication().invokeLater {
            Messages.showErrorDialog(project, messages, title)
        }
    }

    fun isNotAppException(e: Throwable): Boolean {
        if (e is AppException) {
            return false
        }
        if (e is UndeclaredThrowableException && e.undeclaredThrowable is AppException) {
            return false
        }
        return true
    }

}