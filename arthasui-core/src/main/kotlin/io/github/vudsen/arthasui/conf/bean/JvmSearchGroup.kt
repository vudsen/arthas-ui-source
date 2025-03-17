package io.github.vudsen.arthasui.conf.bean

data class JvmSearchGroup(
    /**
     * 搜索组的名称
     */
    var name: String = "",
    /**
     * 搜索脚本
     */
    var script: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JvmSearchGroup

        return name == other.name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
