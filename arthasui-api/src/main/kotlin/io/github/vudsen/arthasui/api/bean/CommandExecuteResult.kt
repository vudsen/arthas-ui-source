package io.github.vudsen.arthasui.api.bean

/**
 * 命令执行结果. **标准错误流应该重定向到标准输出上一起显示**
 */
data class CommandExecuteResult(
    var stdout: String,
    var exitCode: Int
) {

    /**
     * 断言执行结果成功
     * @return [CommandExecuteResult.stdout]
     */
    fun ok(): String {
        if (exitCode != 0) {
            throw IllegalStateException("Command execute failed: $exitCode, stdout: $stdout")
        }
        return stdout
    }
}
