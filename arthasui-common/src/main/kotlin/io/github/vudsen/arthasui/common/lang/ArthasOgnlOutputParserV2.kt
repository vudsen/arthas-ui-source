package io.github.vudsen.arthasui.common.lang

import io.github.vudsen.arthasui.common.lang.model.ArthasArray
import io.github.vudsen.arthasui.common.lang.model.ArthasMap
import io.github.vudsen.arthasui.common.lang.model.ArthasObject
import io.github.vudsen.arthasui.common.lang.model.ArthasValue
import io.github.vudsen.arthasui.api.ArthasResultItem
import java.nio.CharBuffer

/**
 *
 */
class ArthasOgnlOutputParserV2(ognlStatement: String) {

    private val buffer = CharBuffer.wrap(ognlStatement)


    companion object {

        @JvmStatic
        fun parse(input: String): ArthasResultItem {
            return ArthasOgnlOutputParserV2(input).doParse()
        }

        enum class CollectionType {
            Object,
            Array,
            Map,
            Empty
        }

        /**
         * 查看下一个字符，如果下一个字符为空，则会返回 '\0'
         */
        private fun CharBuffer.peekNextChar(): Char {
            if (this.position() < this.limit()) {
                return this[this.position()]
            }
            return 0.toChar()
        }

        private fun CharBuffer.skipAndAssertChar(expected: Char) {
            val ch = this.get()
            if (ch != expected) {
                throw UnexpectedCharacterException(0, this.position(), expected, ch)
            }
        }

        private fun CharBuffer.readUntil(char: Char): String {
            val result = StringBuilder()
            while (position() < limit()) {
                val ch = get()
                if (ch == char) {
                    break
                }
                result.append(ch)
            }
            return result.toString()
        }

        /**
         * 读取剩下的内容直到碰到换行
         */
        private fun CharBuffer.readRemaining(): String {
            return readUntil('\n')
        }

        private fun CharBuffer.skipWhitespace() {
            while (this.position() < this.limit()) {
                val ch = this[this.position()]
                when (ch) {
                    ' ', '\r', '\t'-> {
                        this.get()
                        continue
                    }
                    '\n' -> {
                        this.get()
                        continue
                    }
                    else -> {
                        break
                    }
                }
            }
        }
    }

    /**
     * 解析类型，当前位置必须位于 '[' 后面的一个字符
     */
    private fun parseType(slice: CharBuffer): CollectionType {
        slice.skipWhitespace()
        val head = slice.get()
        if (head == ']') {
            return CollectionType.Empty
        } else if (head != '@') {
            return CollectionType.Object
        }
        val line = slice.readRemaining()
        return if (line.contains("]:@")) {
            CollectionType.Map
        } else {
            CollectionType.Array
        }
    }


    private fun parseCollection(clazz: String): ArthasResultItem {
        return when (parseType(buffer.slice())) {
            CollectionType.Map -> parseAsMap(clazz)
            CollectionType.Empty -> parseAsArray(clazz)
            CollectionType.Array -> parseAsArray(clazz)
            CollectionType.Object -> parseAsObject(clazz)
        }
    }

    /**
     * 获取 ArthasValue
     * @param startIndex 指向 '@' 符号
     * @param end 执行 `]`
     */
    private fun parseArthasValueFromRawString(raw: String, startIndex: Int, end: Int): ArthasValue {
        val i = raw.indexOf('[', startIndex)
        return ArthasValue(raw.substring(i + 1, end), raw.substring(startIndex + 1, i))
    }


    private fun parseAsArray(clazz: String): ArthasArray {
        val result = mutableListOf<ArthasResultItem>()

        while (true) {
            buffer.skipWhitespace()
            if (buffer.peekNextChar() == ']') {
                buffer.get()
                if (buffer.peekNextChar() == ',') {
                    buffer.get()
                }
                break
            }
            buffer.skipAndAssertChar('@')
            val valueClazz = buffer.readUntil('[')
            val remaining = buffer.readRemaining().trim()

            if (remaining.isEmpty()) {
                result.add(parseCollection(valueClazz))
            } else {
                result.add(ArthasValue(remaining.substring(0, remaining.length - 2), valueClazz))
            }
        }
        return ArthasArray(result, clazz)
    }


    /**
     * 使用 `]:@` 分割
     */
    private fun parseAsMap(clazz: String): ArthasMap {
        val result = linkedMapOf<ArthasResultItem, ArthasResultItem>()
        while (true) {
            buffer.skipWhitespace()
            val remaining = buffer.readRemaining().trim()
            if (remaining[0] == ']') {
                break
            }
            // TODO, handle when multiply ']:@' occurs
            val i = remaining.indexOf("]:@")
            val key = parseArthasValueFromRawString(remaining, 0, i)
            if (remaining[remaining.length - 1] == '[') {
                result[key] = parseCollection(remaining.substring(i + 3, remaining.length - 1))
            } else {
                result[key] = parseArthasValueFromRawString(remaining, i + 2, remaining.length - 2)
            }
        }
        return ArthasMap(result, clazz)
    }

    private fun parseAsObject(clazz: String): ArthasObject {
        val result = linkedMapOf<String, ArthasResultItem>()
        while (true) {
            buffer.skipWhitespace()
            val remaining = buffer.readRemaining().trim()
            if (remaining[0] == ']') {
                break
            }
            val i = remaining.indexOf('=')
            val key = remaining.substring(0, i)
            if (remaining[remaining.length - 1] == '[') {
                result[key] = parseCollection(remaining.substring(i + 1, remaining.length - 1))
            } else {
                result[key] = parseArthasValueFromRawString(remaining, i + 1, remaining.length - 2)
            }
        }
        return ArthasObject(result, clazz)
    }

    fun doParse(): ArthasResultItem {
        buffer.skipAndAssertChar('@')
        val clazz = buffer.readUntil('[')
        val remaining = buffer.readRemaining()
        return if (remaining.trim().isEmpty()) {
            parseCollection(clazz)
        } else {
            ArthasValue(clazz, remaining.substring(0, remaining.length - 1))
        }
    }

}