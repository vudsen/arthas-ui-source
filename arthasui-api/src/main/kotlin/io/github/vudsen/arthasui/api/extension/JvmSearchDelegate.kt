package io.github.vudsen.arthasui.api.extension

import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import javax.swing.Icon

/**
 * Jvm 搜索委托. 和 [JvmSearchResult] 组成树形结构
 * @see JvmSearchResult
 */
interface JvmSearchDelegate {

    /**
     * 获取名称
     */
    fun getName(): String

    /**
     * 获取图标
     */
    fun getIcon(): Icon

    /**
     * 加载所有结果
     */
    fun load(): JvmSearchResult



    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

}