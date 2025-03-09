package io.github.vudsen.arthasui.common.bean

import io.github.vudsen.arthasui.api.ArthasResultItem

/**
 * TODO move position.
 */
data class StringResult(var content: String) : ArthasResultItem {

    override fun toString(): String {
        return content
    }

}