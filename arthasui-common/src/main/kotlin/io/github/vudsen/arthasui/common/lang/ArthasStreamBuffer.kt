package io.github.vudsen.arthasui.common.lang

import java.nio.CharBuffer

/**
 * 持续解析 Arthas 输出，并返回对应的"帧".
 *
 * 使用 [write] 方法来写入数据。[buffer] 字段默认是读模式，在读的时候不需要手动切换。
 */
class ArthasStreamBuffer {

    companion object {
        private val FRAME_END_PATTERN = Regex("^\\[arthas@\\d+]\\$ ", setOf(RegexOption.MULTILINE))
    }

    private var buffer = CharBuffer.allocate(1024)

    fun write(out: CharArray, len: Int) {
        if (buffer.limit() != buffer.capacity()) {
            // switch to write mode
            buffer.compact()
        }
        while (buffer.position() + len + 1 >= buffer.capacity()) {
            val nextCap = buffer.capacity() * 2
            // maximum 64mb
            if (nextCap >= 1024 * 1024 * 64) {
                throw IllegalStateException("Output is too large! Consider using lower '-x' argument.")
            }
            val old = buffer
            buffer = CharBuffer.allocate(nextCap)
            old.flip()
            buffer.put(old)
        }
        buffer.put(out, 0, len)
        buffer.flip()
    }


    /**
     * 读取所有的内容
     */
    fun clear() {
        buffer.clear()
        buffer.flip()
    }

    /**
     * 返回下一帧(如果帧不完整，返回 null)
     */
    fun readNextFrame(ignoreFirstLine: Boolean = true): String? {
        val result = FRAME_END_PATTERN.find(buffer) ?: return null
        val first = result.range.first - 1
        val content = StringBuilder(first)

        for (i in 0..first) {
            content.append(buffer.get())
        }
        buffer.position(result.range.last + 1)
        if (ignoreFirstLine) {
            val i = content.indexOf("\n")
            return content.substring(i + "\n".length).toString().trimEnd()
        }
        return content.toString().trim()
    }

}