package io.github.vudsen.arthasui.api


abstract class JVM(
    /**
     * id，可以是 pid，也可以是容器id 或者容器名称
     */
    val id: String,
    var name: String
) {

    /**
     * 获取显示时的名称.
     */
    @Deprecated("Use name instead", ReplaceWith("name"))
    fun getDisplayName(): String {
        return name
    }

    /**
     * 获取主类
     */
    @Deprecated("Use name instead", ReplaceWith("name"))
    fun getMainClass(): String {
        return name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JVM) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}