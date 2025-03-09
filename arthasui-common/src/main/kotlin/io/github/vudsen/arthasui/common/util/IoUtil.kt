package io.github.vudsen.arthasui.common.util

import java.io.*
import java.nio.charset.StandardCharsets

fun InputStream.readAllAsString(): String {
    var available = this.available()
    if (available == 0) {
        available = 128
    }
    val baos = ByteArrayOutputStream(available)
    val buf = ByteArray(available)
    var len: Int
    while (true) {
        len = this.read(buf)
        if (len == -1) {
            break
        }
        baos.write(buf, 0, len)
    }
    return baos.toString(StandardCharsets.UTF_8)
}


fun Exception.printStackTraceToString(): String {
    val sw = StringWriter();
    this.printStackTrace( PrintWriter(sw, true));
    return sw.buffer.toString();
}