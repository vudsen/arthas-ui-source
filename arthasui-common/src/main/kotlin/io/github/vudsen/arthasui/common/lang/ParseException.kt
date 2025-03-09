package io.github.vudsen.arthasui.common.lang

open class ParseException(line: Int, offset: Int, extraMessage: String) : Exception("Failed to parse line: $line, offset: $offset; $extraMessage") {

    constructor(line: Int, offset: Int) : this(line, offset, "")

}