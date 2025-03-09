package io.github.vudsen.arthasui.api

fun interface ArthasBridgeFactory {

    /**
     * 获取 arthas bridge.
     */
    fun createBridge(): ArthasBridge

}