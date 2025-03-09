package io.github.vudsen.arthasui.language.ognl.psi

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class OgnlFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, OgnlLanguage) {

    override fun getFileType(): FileType {
        return OgnlFileType
    }

    override fun toString(): String {
        return "Ognl File"
    }

}