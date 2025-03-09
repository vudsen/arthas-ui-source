package io.github.vudsen.arthasui.api


abstract class ArthasBridgeListener {

    /**
     * 当读取到任意内容时
     */
    open fun onContent(result: String) {}

    /**
     * 当命令完成后.
     * @param command 执行的命令
     * @param result 执行的结果
     */
    open fun onFinish(command: String, result: ArthasResultItem, rawContent: String) {}

    /**
     * @param command 执行的命令
     * @param exception 如果出现错误，则该值非空。如果是命令执行错误，一般会返回 [BridgeException]；如果是内部错误，则会返回其它类型
     */
    open fun onError(command: String, rawContent: String, exception: Exception) {}

    /**
     * 当连接关闭时触发.
     */
    open fun onClose() {}

}