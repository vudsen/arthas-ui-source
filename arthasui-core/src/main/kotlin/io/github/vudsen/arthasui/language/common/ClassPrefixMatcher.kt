package io.github.vudsen.arthasui.language.common

import com.intellij.codeInsight.completion.PrefixMatcher

class ClassPrefixMatcher(classQualifiedName: String) : PrefixMatcher(classQualifiedName) {

    private val clazzName: String

    private val hasPackage: Boolean

    init {
        val i = classQualifiedName.lastIndexOf(".")
        hasPackage = i >= 0
        clazzName = classQualifiedName.substring(i + 1)
    }

    override fun isStartMatch(name: String?): Boolean {
        return super.isStartMatch(name)
    }

    override fun prefixMatches(name: String): Boolean {
        // 有包名, 例如输入了 java.lang.T
        if (hasPackage) {
            return name.startsWith(clazzName)
        }
        // name 为全限定名称
        val i = name.lastIndexOf('.')
        return if (i < 0) {
            name.startsWith(clazzName)
        } else {
            name.startsWith(clazzName, i + 1)
        }
    }

    override fun cloneWithPrefix(prefix: String): PrefixMatcher {
        return ClassPrefixMatcher(prefix)
    }

}