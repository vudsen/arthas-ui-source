package io.github.vudsen.arthasui.language.ognl

import com.intellij.codeInsight.completion.CompletionUtil
import io.github.vudsen.arthasui.language.ognl.psi.OgnlClazz


fun OgnlClazz.toQualifiedName(): String {
    val name: String = if (clazzPackageList.isEmpty()) {
        className.identifier.text
    } else {
        getPackageName() + '.' + className.identifier.text
    }
    if (name.endsWith(CompletionUtil.DUMMY_IDENTIFIER_TRIMMED)) {
        return name.substring(0, name.length - CompletionUtil.DUMMY_IDENTIFIER.length + 1)
    }
    return name
}

/**
 * 获取当前包名。如果没有包名，返回空字符串
 */
fun OgnlClazz.getPackageName(): String? {
    if (clazzPackageList.isEmpty()) {
        return null
    }
    val builder = StringBuilder(clazzPackageList.size * 4)

    for (pkg in clazzPackageList) {
        builder.append(pkg.identifier.text)
        builder.append('.')
    }
    builder.deleteCharAt(builder.length - 1)
    return builder.toString()
}

fun OgnlClazz.getClassname(): String {
    val text = className.identifier.text
    if (text.endsWith(CompletionUtil.DUMMY_IDENTIFIER_TRIMMED)) {
        return text.substring(0, text.length - CompletionUtil.DUMMY_IDENTIFIER.length + 1)
    }
    return text
}