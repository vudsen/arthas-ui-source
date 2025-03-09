package io.github.vudsen.arthasui.language.arthas.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import io.github.vudsen.arthasui.language.arthas.psi.ArthasTypes
import io.github.vudsen.arthasui.language.common.CacheableCompletionSupport
import io.github.vudsen.arthasui.language.common.ClassPrefixMatcher
import io.github.vudsen.arthasui.language.common.CompletionUtil
import io.github.vudsen.arthasui.language.ognl.toQualifiedName

class ArthasCompletionContributor : CompletionContributor() {

    init {
        extend(CompletionType.BASIC,
            PlatformPatterns.psiElement(ArthasTypes.CLASS_PATTERN),
            object : CompletionProvider<CompletionParameters>() {
            override fun addCompletions(
                parameters: CompletionParameters,
                context: ProcessingContext,
                r: CompletionResultSet
            ) {
                val project = parameters.editor.project ?: return
                val resolveScope = parameters.position.resolveScope
                val splitClass = CompletionUtil.splitClass(parameters.position.text)
                val cacheableCompletionSupport = project.getService(CacheableCompletionSupport::class.java)

                val result = r.withPrefixMatcher(ClassPrefixMatcher(splitClass.qualifiedName))
                result.addAllElements(cacheableCompletionSupport.getSuggestions(splitClass.classname, splitClass.pkg, project, resolveScope))
            }
        })
        extend(CompletionType.BASIC,
            PlatformPatterns
                .psiElement(ArthasTypes.IDENTIFIER)
                .withSuperParent(2, PlatformPatterns.psiFile()),
            object : CompletionProvider<CompletionParameters>() {

            private val commandElements : List<LookupElement>

            init {
                val commands = mutableListOf<LookupElement>()
                commands.add(LookupElementBuilder.create("auth").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("base64").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("cat").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("classloader").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("cls").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("dashboard").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("dump").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("echo").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("getstatic").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("grep").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("heapdump").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("help").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("history").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("jad").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("jfr").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("keymap").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("logger").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("mbean").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("mc").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("memory").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("options").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("ognl").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("ognl").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("perfcounter").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("profiler").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("pwd").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("quit").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("redefine").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("reset").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("retransform").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("sc").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("session").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("sm").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("stack").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("stop").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("sysenv").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("sysprop").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("tee").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("thread").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("trace").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("tt").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("version").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("vmoptions").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("vmtool").withIcon(AllIcons.Nodes.Console))
                commands.add(LookupElementBuilder.create("watch").withIcon(AllIcons.Nodes.Console))
                commandElements = commands
            }

            override fun addCompletions(
                parameters: CompletionParameters,
                context: ProcessingContext,
                result: CompletionResultSet
            ) {
                result.addAllElements(commandElements)
            }
        })
    }
}