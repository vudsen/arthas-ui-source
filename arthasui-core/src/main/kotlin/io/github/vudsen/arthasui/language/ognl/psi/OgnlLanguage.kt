package io.github.vudsen.arthasui.language.ognl.psi

import com.intellij.lang.Language


object OgnlLanguage : Language("Ognl") {
    private fun readResolve(): Any = OgnlLanguage
}