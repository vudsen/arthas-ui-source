package io.github.vudsen.arthasui.common.lang.model

import io.github.vudsen.arthasui.api.ArthasResultItem

/**
 * 用于表示输出的对象属性。
 *
 * Example:
 * ```
 * @Test[
 *     value=@Integer[2],
 *     str=@String[test],
 * ]
 * ```
 * ```
 */
data class ArthasObject(
    var fields: LinkedHashMap<String, ArthasResultItem> = linkedMapOf(),
    var clazz: String,
): ArthasResultItem {

    override fun toString(): String {
        return "@${clazz}"
    }

}