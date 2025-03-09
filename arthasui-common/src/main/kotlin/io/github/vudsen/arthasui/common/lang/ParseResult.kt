package io.github.vudsen.arthasui.common.lang

data class ParseResult<T>(
    var data: T,
    var raw: String
)
