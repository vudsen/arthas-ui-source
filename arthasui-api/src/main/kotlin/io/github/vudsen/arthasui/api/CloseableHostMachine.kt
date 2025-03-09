package io.github.vudsen.arthasui.api

interface CloseableHostMachine : AutoCloseable {

    fun isClosed(): Boolean

}