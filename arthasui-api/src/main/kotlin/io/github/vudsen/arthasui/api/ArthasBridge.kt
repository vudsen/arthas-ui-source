package io.github.vudsen.arthasui.api

import io.github.vudsen.arthasui.api.exception.BridgeException
import kotlin.jvm.Throws

/**
 * Arthas 桥接。**对于调用者需要严格处理并发问题**，而实现类不需要关心并发问题，即实现类是**非线程安全的**。
 */
interface ArthasBridge {

    /**
     * 执行任意命令，用于处理部分命令没有专门做过增强，可以直接使用该方法进行调用。
     * @return 执行后的结果
     */
    @Throws(BridgeException::class)
    suspend fun execute(command: String): ArthasResultItem

    /**
     * 连接是否仍然存活
     */
    fun isAlive(): Boolean

    /**
     * 添加监听器
     */
    fun addListener(arthasBridgeListener: ArthasBridgeListener)

    /**
     * 关闭进场并返回 exit code
     */
    fun stop(): Int

    /**
     * 取消正在执行的命令
     */
    suspend fun cancel()
}