package io.github.vudsen.arthasui.common.parser

import io.github.vudsen.arthasui.api.ArthasResultItem
import io.github.vudsen.arthasui.common.bean.StringResult

class DefaultFrameDecoder : ArthasFrameDecoder {
    override fun parse(frame: String): ArthasResultItem {
        return StringResult(frame)
    }
}