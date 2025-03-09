package io.github.vudsen.arthasui.language.ognl

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
import io.github.vudsen.arthasui.language.ognl.psi.OgnlFile
import io.github.vudsen.arthasui.language.ognl.psi.OgnlLanguage
import io.github.vudsen.arthasui.language.ognl.psi.OgnlParser
import io.github.vudsen.arthasui.language.ognl.psi.OgnlTypes

class OgnlParserDefinition : ParserDefinition {

    companion object {
        val FILE = IFileElementType(OgnlLanguage)
    }

    override fun createLexer(project: Project?): Lexer {
        return OgnlLexerAdapter()
    }

    override fun createParser(project: Project?): PsiParser {
        return OgnlParser()
    }

    override fun getFileNodeType(): IFileElementType {
        return FILE
    }

    override fun getCommentTokens(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun getStringLiteralElements(): TokenSet {
        return TokenSet.EMPTY
    }

    override fun createElement(node: ASTNode?): PsiElement {
        return OgnlTypes.Factory.createElement(node)
    }

    override fun createFile(viewProvider: FileViewProvider): PsiFile {
        return OgnlFile(viewProvider)
    }
}