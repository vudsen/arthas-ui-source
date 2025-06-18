package io.github.vudsen.arthasui.common.util

import com.intellij.openapi.progress.ProgressIndicator
import java.util.Stack

/**
 * 用于解决跨线程共享 `ProgressIndicator` 的问题.
 */
object ProgressIndicatorStack {

    private val stack = Stack<ProgressIndicator>()

    fun push(indicator: ProgressIndicator) {
        stack.push(indicator)
    }

    fun currentIndicator(): ProgressIndicator? {
        if (stack.isEmpty()) {
            return null
        }
        return stack.peek()
    }

    fun pop() {
        if (stack.isNotEmpty()) {
            stack.pop()
        }
    }

}