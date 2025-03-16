package io.github.vudsen.arthasui.conf.bean

data class JvmSearchGroup(
    /**
     * 搜索组的名称
     */
    var name: String = "",
    /**
     * 宿主机的名称 [io.github.vudsen.arthasui.conf.HostMachineConfigV2.name]
     */
    var hostMachineName: String = "",
    /**
     * 搜索脚本
     */
    var script: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as JvmSearchGroup

        if (name != other.name) return false
        if (hostMachineName != other.hostMachineName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + hostMachineName.hashCode()
        return result
    }
}
