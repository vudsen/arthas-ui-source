package io.github.vudsen.arthasui.api


interface JVM {

    /**
     * 获取显示时的名称.
     */
    fun getDisplayName(): String

    /**
     * 获取主类
     */
    fun getMainClass(): String

    /**
     * 获取 JVM 的 id。可以是 pid 或者容器的 id
     */
    fun getId(): String

}