package io.github.vudsen.arthasui.language.arthas

import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import io.github.vudsen.arthasui.language.arthas.psi.*

class ArthasParserDefinition : ParserDefinition {


    companion object {
        val FILE: IFileElementType = IFileElementType(ArthasLanguage)
    }


    override fun createLexer(project: Project?): Lexer {
        return ArthasLexerAdapter()
    }

    override fun createParser(project: Project?): PsiParser {
        return ArthasParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun getCommentTokens(): TokenSet {
        return COMMENTS
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return ArthasTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return ArthasFile(viewProvider)
    }
}