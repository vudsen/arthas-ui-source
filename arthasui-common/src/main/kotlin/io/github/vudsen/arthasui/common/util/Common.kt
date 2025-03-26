package io.github.vudsen.arthasui.common.util

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

fun Exception.printStackTraceToString(): String {
    val sw = StringWriter();
    this.printStackTrace( PrintWriter(sw, true));
    return sw.buffer.toString();
}