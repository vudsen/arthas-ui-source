package io.github.vudsen.arthasui.api.bean

/**
 * 命令执行结果. **标准错误流应该重定向到标准输出上一起显示**
 */
data class CommandExecuteResult(
    var stdout: String,
    var exitCode: Int
)
