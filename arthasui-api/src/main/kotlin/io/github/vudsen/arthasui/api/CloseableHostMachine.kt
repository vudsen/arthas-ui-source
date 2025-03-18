package io.github.vudsen.arthasui.api


interface CloseableHostMachine : AutoCloseable, HostMachine {

    fun isClosed(): Boolean

}