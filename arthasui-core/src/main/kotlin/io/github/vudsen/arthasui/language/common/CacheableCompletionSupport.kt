package io.github.vudsen.arthasui.language.common

import ai.grazie.utils.WeakHashMap
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Iconable
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiPackage
import com.intellij.psi.impl.JavaPsiFacadeImpl
import com.intellij.psi.impl.file.PsiPackageImpl
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.AllClassesSearch

/**
 * 提供带缓存的代码补全支持, 使用 [getSuggestions] 即可获取对应的补全提示。
 */
@Service(Service.Level.PROJECT)
class CacheableCompletionSupport {


    private val pkgCache = WeakHashMap<CacheLocator, Array<PsiPackage>>()

    private val classNameCache = WeakHashMap<CacheLocator, Array<PsiClass>>()

    private companion object {

        private val fullClassInsertHandler = FullClassInsertHandler()

        private data class CacheLocator(
            var pkgName: String?,
            var scope: GlobalSearchScope,
            var project: Project,
        )

        private class FullClassInsertHandler : InsertHandler<LookupElement> {


            override fun handleInsert(context: InsertionContext, item: LookupElement) {
                val insertContent = item.`object` as String
                context.document.replaceString(context.startOffset, context.tailOffset, insertContent)
            }

        }

    }


    fun getSuggestions(classname: String, packageName: String?, project: Project, scope: GlobalSearchScope): List<LookupElement> {
        val result = ArrayList<LookupElement>()
        val facade = JavaPsiFacade.getInstance(project) as JavaPsiFacadeImpl
        val cacheLocator = CacheLocator(packageName, scope, project)

        val rootPackage = PsiPackageImpl(PsiManager.getInstance(cacheLocator.project), cacheLocator.pkgName)

        if (cacheLocator.pkgName != null) {
            addAllPackages(cacheLocator, facade, result, rootPackage)
        }
        if (cacheLocator.pkgName == null) {
            // fuzzy search all without cache.
            val searched = AllClassesSearch.search(cacheLocator.scope, cacheLocator.project) { item ->
                return@search item.startsWith(classname)
            }
            val it = searched.take(40)
            for (psiClass in it) {
                psiClass.qualifiedName ?.let {
                    val i = it.lastIndexOf('.')
                    if (i >= 0) {
                        result.add(
                            buildBasicLookupElement(it.substring(i + 1), it)
                                .withIcon(psiClass.getIcon(Iconable.ICON_FLAG_VISIBILITY))
                        )

                    }
                }
            }
        } else {
            addAllClassesWithPackage(cacheLocator, facade, rootPackage, result)
        }
        return result
    }

    /**
     * 创建基础的 [LookupElementBuilder]
     */
    private fun buildBasicLookupElement(classname: String, insertContent: String): LookupElementBuilder {
        return LookupElementBuilder.create(insertContent, classname)
            .withTailText(" " + insertContent.substringBeforeLast('.'))
            .withInsertHandler(fullClassInsertHandler)
    }

    private fun addAllClassesWithPackage(
        cacheLocator: CacheLocator,
        facade: JavaPsiFacadeImpl,
        rootPackage: PsiPackageImpl,
        result: ArrayList<LookupElement>
    ) {
        var cachedClassName = classNameCache[cacheLocator]
        if (cachedClassName == null) {
            cachedClassName = facade.getClasses(rootPackage, cacheLocator.scope)
            classNameCache[cacheLocator] = cachedClassName
        }

        for (clazz in cachedClassName) {
            clazz.name?.let { name ->
                clazz.qualifiedName?.let { qualifiedName ->
                    result.add(buildBasicLookupElement(name, qualifiedName).withIcon(clazz.getIcon(Iconable.ICON_FLAG_VISIBILITY)))
                }
            }
        }
    }

    private fun addAllPackages(
        cacheLocator: CacheLocator,
        facade: JavaPsiFacadeImpl,
        result: ArrayList<LookupElement>,
        psiPackage: PsiPackage
    ) {
        var cached= pkgCache[cacheLocator]
        if (cached == null) {
            cached = facade.getSubPackages(psiPackage, cacheLocator.scope)
            pkgCache[cacheLocator] = cached
        }
        for (p in cached) {
            result.add(
                buildBasicLookupElement(p.name ?: p.qualifiedName, p.qualifiedName)
                    .withIcon(p.getIcon(Iconable.ICON_FLAG_VISIBILITY))
            )
        }
    }


}