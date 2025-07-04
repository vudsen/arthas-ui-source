package io.github.vudsen.arthasui.api.bean

import io.github.vudsen.arthasui.api.exception.AppException

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
            throw AppException("Command execute failed: $exitCode, stdout: $stdout")
        }
        return stdout
    }

    /**
     * 尝试获取结果
     * @return 如果成功，返回输出，否则返回 null
     */
    fun tryUnwrap(): String? {
        return if (exitCode == 0) {
            return stdout
        } else {
            null
        }
    }
}
