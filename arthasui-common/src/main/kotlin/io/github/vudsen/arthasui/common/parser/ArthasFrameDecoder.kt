package io.github.vudsen.arthasui.common.parser

import io.github.vudsen.arthasui.api.ArthasResultItem

fun interface ArthasFrameDecoder {

    /**
     * 解析一个完整的帧.
     */
    fun parse(frame: String): ArthasResultItem

}