package io.github.vudsen.arthasui.api

import java.io.InputStream
import java.io.OutputStream

/**
 * Arthas 进程
 */
interface ArthasProcess {

    fun getInputStream(): InputStream

    fun getOutputStream(): OutputStream

    fun isAlive(): Boolean

    fun stop(): Int

}