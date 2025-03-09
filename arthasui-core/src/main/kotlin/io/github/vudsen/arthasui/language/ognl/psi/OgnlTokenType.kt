package io.github.vudsen.arthasui.language.ognl.psi

import com.intellij.psi.tree.IElementType

class OgnlTokenType(debugName: String) : IElementType(debugName, OgnlLanguage) {

    override fun toString(): String {
        return "OgnlTokenType." + super.toString()
    }

}