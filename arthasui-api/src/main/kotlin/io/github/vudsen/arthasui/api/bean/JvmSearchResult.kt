package io.github.vudsen.arthasui.api.bean

import io.github.vudsen.arthasui.api.JVM
import javax.swing.Icon

class JvmSearchResult(
    var result: List<JVM>? = null,
    var child: List<ChildSearcher>? = null
) {
    companion object {
        interface ChildSearcher {

            fun getName(): String

            fun getIcon(): Icon

            fun load(): JvmSearchResult

            override fun equals(other: Any?): Boolean

            override fun hashCode(): Int

        }

    }
}