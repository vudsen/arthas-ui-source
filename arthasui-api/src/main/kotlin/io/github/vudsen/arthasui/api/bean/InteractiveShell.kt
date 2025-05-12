package io.github.vudsen.arthasui.api.bean

import java.io.Reader
import java.io.Writer

interface InteractiveShell : AutoCloseable {

    fun getReader(): Reader

    fun getWriter(): Writer

    fun isAlive(): Boolean

    /**
     * 退出码，如果还没有退出，返回空
     */
    fun exitCode(): Int?


}