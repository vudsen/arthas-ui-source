package io.github.vudsen.arthasui.api

interface AutoCloseableWithState : AutoCloseable {

    /**
     * 是否已经关闭了
     */
    fun isClosed(): Boolean

    /**
     * 当前是否可以关闭. 例如用户主动创建了长连接，此时不应该自动关闭连接，而是应该由用户收到关闭
     */
    fun isCloseable(): Boolean


}