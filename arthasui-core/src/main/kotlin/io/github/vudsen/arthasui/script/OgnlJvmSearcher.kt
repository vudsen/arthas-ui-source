package io.github.vudsen.arthasui.script

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.conf.bean.JvmSearchGroup
import ognl.Ognl
import kotlin.jvm.Throws

/**
 * 使用 ognl 脚本搜索 jvm.
 *
 * Example:
 * ```
 * #resultStr = hostMachine.execute(localHelper.jps(), '-lm').stdout,
 * #result = #resultStr.split('\n').{? #this.contains('com.intellij.idea.Main')},
 * addLocal(#result.{ #this.split(' ') })
 * ```
 */
object OgnlJvmSearcher {

    /**
     * 执行脚本搜索 jvm
     * @throws ClassCastException 脚本返回值不是 [ArrayList]<[JVM]>
     */
    @Throws(ClassCastException::class)
    fun execute(script: String, context: MyOgnlContext) {
        val expression = Ognl.parseExpression(script)
        Ognl.getValue(expression, context)
    }

    fun executeByGroup(searchGroup: JvmSearchGroup, context: MyOgnlContext): MutableList<JVM> {
        execute(searchGroup.script, context)
        return context.getResultHolder().result
    }

}