package io.github.vudsen.arthasui.common.lang.model

import io.github.vudsen.arthasui.api.ArthasResultItem

/**
 * Example:
 * ```
 * @HashMap[
 *      @String[test2]:@Integer[1],
 *      @String[test3]:@SingletonSet[
 *          @String[test3],
 *      ],
 *      @String[test]:@String[test],
 *  ]
 * ```
 */
data class ArthasMap(
    var entries: LinkedHashMap<ArthasResultItem, ArthasResultItem>,
    var clazz: String,
) : ArthasResultItem {

    override fun toString(): String {
        return "@${clazz}"
    }

}