package io.github.vudsen.arthasui.common.parser

import io.github.vudsen.arthasui.api.ArthasResultItem
import io.github.vudsen.arthasui.common.bean.StringResult

/**
 * 读取接下来整个内容，直到碰到 `[arthas@xxxx]$ `
 */
class DefaultFrameDecoder : ArthasFrameDecoder {
    override fun parse(frame: String): ArthasResultItem {
        return StringResult(frame)
    }
}