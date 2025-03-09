package io.github.vudsen.arthasui.common.lang.model

import io.github.vudsen.arthasui.api.ArthasResultItem

/**
 * 用于处理数组.
 *
 * Example:
 * ```
 *  @SingletonSet[
 *      @String[test3],
 *  ]
 * ```
 */
data class ArthasArray (
    var values: MutableList<ArthasResultItem> = mutableListOf(),
    var clazz: String,
) : ArthasResultItem {

    override fun toString(): String {
        return "@${clazz}[${values.size}]"
    }

}