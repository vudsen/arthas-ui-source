package io.github.vudsen.arthasui.language.ognl.psi

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object OgnlFileType : LanguageFileType(OgnlLanguage) {

    override fun getName(): String {
        return "Ognl File"
    }

    override fun getDescription(): String {
        return "Ognl language file"
    }

    override fun getDefaultExtension(): String {
        return "ognl"
    }

    override fun getIcon(): Icon {
        return AllIcons.Ide.Gift
    }

}