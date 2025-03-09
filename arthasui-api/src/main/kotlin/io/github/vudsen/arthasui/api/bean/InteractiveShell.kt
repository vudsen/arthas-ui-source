package io.github.vudsen.arthasui.api.bean

import java.io.InputStream
import java.io.OutputStream

class InteractiveShell(
    val inputStream: InputStream,
    val outputStream: OutputStream,
    val isAlive: () -> Boolean,
    /**
     * 结束进程，返回退出码，可以多次执行
     */
    val close: () -> Int
)