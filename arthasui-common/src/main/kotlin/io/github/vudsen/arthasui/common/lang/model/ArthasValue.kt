package io.github.vudsen.arthasui.common.lang.model

import io.github.vudsen.arthasui.api.ArthasResultItem


/**
 * arthas 输出中**不能再细分的最小单元**.
 *
 * Example:
 * ```
 * @Test[
 *    value=@Integer[2],
 *    str=@String[test],
 * ]
 * ```
 *
 * 此处 `@Integer[2]` 和 `@String[test]` 分别是两个 [ArthasValue]:

 */
data class ArthasValue(
    var value: String,
    var clazz: String
): ArthasResultItem {

    override fun toString(): String {
        return "@${clazz}[${value}]"
    }

}