package io.github.vudsen.arthasui.common.parser

import io.github.vudsen.arthasui.api.ArthasResultItem
import io.github.vudsen.arthasui.api.exception.BridgeException
import kotlin.jvm.Throws

fun interface ArthasFrameDecoder {

    /**
     * 解析一个完整的帧.
     */
    @Throws(BridgeException::class)
    fun parse(frame: String): ArthasResultItem

}