package io.github.vudsen.arthasui.common.lang

class UnexpectedCharacterException(line: Int, offset: Int, expected: Char, actual: Char) : ParseException(line, offset, "Unexpected character: $actual, expected: $expected") {



}