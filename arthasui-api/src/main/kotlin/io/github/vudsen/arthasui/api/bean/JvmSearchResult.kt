package io.github.vudsen.arthasui.api.bean

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.extension.JvmSearchDelegate

/**
 * Jvm 的搜索结果. 所有字段最多有一个非空
 */
class JvmSearchResult(
    /**
     * 搜索的结果
     */
    var result: List<JVM>? = null,
    /**
     * 表示当前还需要继续搜索
     */
    var childs: List<JvmSearchDelegate>? = null
)