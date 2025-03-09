package io.github.vudsen.arthasui.language.arthas.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class ArthasFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ArthasLanguage) {
    override fun getFileType(): FileType {
        return ArthasFileType
    }
}