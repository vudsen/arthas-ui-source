package io.github.vudsen.arthasui.api.toolchain

enum class ToolChain {
    /**
     * 根据当前宿主机自动选择 jattach
     */
    JATTACH_BUNDLE,
    /**
     * 获取 arthas-bin.zip
     */
    ARTHAS_BUNDLE,
    /**
     * 该工具链直接执行可执行文件
     */
    KUBECTL
}