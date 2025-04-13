package io.github.vudsen.arthasui.api.util

import io.github.vudsen.arthasui.api.DeepCopyable
import java.io.*

/**
 * 深拷贝数组
 */
fun <T : DeepCopyable<T>> List<T>.deepCopy(): MutableList<T> {
    return this.mapMutable { v -> v.deepCopy() }
}

fun <T, R> List<T>.mapMutable(cast: (T) -> R): MutableList<R> {
    val result = ArrayList<R>(this.size)
    for (t in this) {
        result.add(cast(t))
    }
    return result
}

/**
 * 收集调用栈
 */
fun Exception.collectStackTrace(): String {
    val sw = StringWriter();
    this.printStackTrace( PrintWriter(sw, true));
    return sw.buffer.toString();
}