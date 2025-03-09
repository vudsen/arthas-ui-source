package io.github.vudsen.arthasui.language.arthas.psi

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object ArthasFileType : LanguageFileType(ArthasLanguage) {

    override fun getName(): String {
        return "Arthas File"
    }

    override fun getDescription(): String {
        return ""
    }

    override fun getDefaultExtension(): String {
        return "arthas"
    }

    override fun getIcon(): Icon {
        return AllIcons.Ide.Gift
    }
}