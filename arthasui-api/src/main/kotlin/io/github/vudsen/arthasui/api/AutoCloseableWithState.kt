package io.github.vudsen.arthasui.api

interface AutoCloseableWithState : AutoCloseable {


    fun isClosed(): Boolean

}