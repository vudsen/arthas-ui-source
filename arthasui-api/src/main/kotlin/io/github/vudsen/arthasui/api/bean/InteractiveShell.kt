package io.github.vudsen.arthasui.api.bean

import java.io.InputStream
import java.io.OutputStream

interface InteractiveShell : AutoCloseable{

    fun getInputStream(): InputStream

    fun getOutputStream(): OutputStream

    fun isAlive(): Boolean

    /**
     * 退出码，如果还没有退出，返回空
     */
    fun exitCode(): Int?


}