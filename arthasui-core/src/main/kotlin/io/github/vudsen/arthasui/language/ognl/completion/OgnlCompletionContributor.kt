package io.github.vudsen.arthasui.language.ognl.completion

import com.intellij.codeInsight.completion.*
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import io.github.vudsen.arthasui.language.common.CacheableCompletionSupport
import io.github.vudsen.arthasui.language.common.ClassPrefixMatcher
import io.github.vudsen.arthasui.language.ognl.getClassname
import io.github.vudsen.arthasui.language.ognl.getPackageName
import io.github.vudsen.arthasui.language.ognl.psi.OgnlClazz
import io.github.vudsen.arthasui.language.ognl.psi.OgnlTypes
import io.github.vudsen.arthasui.language.ognl.toQualifiedName

/**
 * Class 搜索: [com.intellij.codeInsight.completion.JavaNoVariantsDelegator.suggestNonImportedClasses]
 */
class OgnlCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(OgnlTypes.IDENTIFIER)
                .withParent(PlatformPatterns.psiElement(OgnlTypes.CLASS_NAME)),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    r: CompletionResultSet
                ) {
                    val resolveScope = parameters.position.resolveScope
                    val ognlClazz = parameters.position.parent.parent as OgnlClazz
                    val project = parameters.editor.project ?: return

                    val cacheableCompletionSupport = project.getService(CacheableCompletionSupport::class.java)
                    val result = r.withPrefixMatcher(ClassPrefixMatcher(ognlClazz.toQualifiedName()))
                    result.addAllElements(cacheableCompletionSupport.getSuggestions(ognlClazz.getClassname(), ognlClazz.getPackageName(), project, resolveScope))
                }
            })
    }


}