package io.github.vudsen.arthasui.common.parser

import io.github.vudsen.arthasui.api.ArthasResultItem
import io.github.vudsen.arthasui.common.lang.ArthasOgnlOutputParserV2
import io.github.vudsen.arthasui.common.bean.StringResult

class OgnlFrameDecoder : ArthasFrameDecoder {

    override fun parse(frame: String): ArthasResultItem {
        if ("null" == frame) {
            return StringResult(frame)
        }
        return ArthasOgnlOutputParserV2(frame).doParse()
    }

}