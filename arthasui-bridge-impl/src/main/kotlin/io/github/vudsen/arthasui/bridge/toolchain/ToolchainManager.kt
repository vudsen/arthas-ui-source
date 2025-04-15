package io.github.vudsen.arthasui.bridge.toolchain


interface ToolchainManager {

    /**
     * 确保所有工具链下载完毕. **如果工具链不存在，则会阻塞当前线程然后进行下载**
     */
    fun ensureToolChainDownloaded()

    /**
     * 是否存在部分工具链不存在
     */
    fun isNotAllToolChainExist(): Boolean

    /**
     * 获取工具链的路径
     */
    fun getToolChainPath(toolChain: ToolChain): String

}