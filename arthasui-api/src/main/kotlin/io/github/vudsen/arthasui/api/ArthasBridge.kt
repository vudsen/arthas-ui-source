package io.github.vudsen.arthasui.api

import io.github.vudsen.arthasui.api.exception.BridgeException
import kotlin.jvm.Throws

/**
 * Arthas 桥接
 */
interface ArthasBridge {

    /**
     * 执行命令.
     * @return 执行后的结果
     */
    @Throws(BridgeException::class)
    fun execute(command: String): ArthasResultItem

    /**
     * 进程是否完全关闭.
     */
    fun isClosed(): Boolean

    /**
     * 添加监听器
     */
    fun addListener(arthasBridgeListener: ArthasBridgeListener)

    /**
     * 关闭进场并返回 exit code. 如果已经关闭，返回退出码
     */
    fun stop(): Int

    /**
     * 是否繁忙
     * @return 若返回 true，表示下一次操作可能会被阻塞
     */
    fun isBusy(): Boolean

}