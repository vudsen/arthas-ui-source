package io.github.vudsen.arthasui.language.common

import com.intellij.codeInsight.completion.CompletionUtil

object CompletionUtil {

    data class ParsedClass(var pkg: String?, var classname: String, var qualifiedName: String)

    /**
     * 分割类的全限定名称
     */
    fun splitClass(className: String): ParsedClass {
        var cn = className
        if (cn.endsWith(CompletionUtil.DUMMY_IDENTIFIER_TRIMMED)) {
            cn = cn.substring(0, cn.length - CompletionUtil.DUMMY_IDENTIFIER_TRIMMED.length)
        }
        if (cn.endsWith('.')) {
            return ParsedClass(cn.substring(0, cn.length - 1), "", cn)
        }
        val i = cn.lastIndexOf('.')
        if (i == -1) {
            return ParsedClass(null, cn, cn)
        }
        return ParsedClass(cn.substring(0, i), cn.substring(i + 1), cn)
    }

}