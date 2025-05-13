package io.github.vudsen.arthasui.bridge.conf

import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

class K8sConnectConfig(
    /**
     * 指向 kubeconfig 文件
     */
    var kubeConfigFilePath: String? = null,
    /**
     * kubeconfig 内容
     */
    var kubeConfig: String? = null,
    /**
     * 使用 token
     */
    var token: TokenAuthorization? = null,
    /**
     * 启用 ssl 校验
     */
    var validateSSL: Boolean = true,
    /**
     * 当前激活的认证方式
     */
    var authorizationType: AuthorizationType = AuthorizationType.TOKEN,
    /**
     * 仅支持使用本地包传输
     */
    var localPkgSourceId: Long? = null,
) : HostMachineConnectConfig(TYPE) {

    enum class AuthorizationType {
        KUBE_CONFIG,
        KUBE_CONFIG_FILE,
        TOKEN,
    }

    data class TokenAuthorization(
        var token: String = "",
        var url: String = ""
    )

    companion object {
        const val TYPE = "Kubernetes"
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Kubernetes
    }

    override fun getOS(): OS {
        return OS.UNKNOWN
    }



    override fun deepCopy(): K8sConnectConfig {
        return K8sConnectConfig(
            kubeConfigFilePath,
            kubeConfig,
            token?.copy(),
            validateSSL,
            authorizationType,
            localPkgSourceId
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as K8sConnectConfig

        if (validateSSL != other.validateSSL) return false
        if (localPkgSourceId != other.localPkgSourceId) return false
        if (kubeConfigFilePath != other.kubeConfigFilePath) return false
        if (kubeConfig != other.kubeConfig) return false
        if (token != other.token) return false
        if (authorizationType != other.authorizationType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = validateSSL.hashCode()
        result = 31 * result + (localPkgSourceId?.hashCode() ?: 0)
        result = 31 * result + (kubeConfigFilePath?.hashCode() ?: 0)
        result = 31 * result + (kubeConfig?.hashCode() ?: 0)
        result = 31 * result + (token?.hashCode() ?: 0)
        result = 31 * result + authorizationType.hashCode()
        return result
    }
}