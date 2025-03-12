package io.github.vudsen.arthasui.script

import io.github.vudsen.arthasui.api.JVM
import ognl.Ognl
import kotlin.jvm.Throws

object OgnlJvmSearcher {

    /**
     * 执行脚本搜索 jvm
     * @throws ClassCastException 脚本返回值不是 [ArrayList]<[JVM]>
     */
    @Throws(ClassCastException::class)
    fun search(script: String, root: SearcherRootState): ArrayList<JVM> {
        val context = Ognl.createDefaultContext(root)
        val expression = Ognl.parseExpression(script)
        val value = Ognl.getValue(expression, context, ArrayList::class)
        return value as ArrayList<JVM>
    }

}