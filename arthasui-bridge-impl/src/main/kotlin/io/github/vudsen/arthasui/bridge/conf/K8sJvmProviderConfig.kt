package io.github.vudsen.arthasui.bridge.conf

import io.github.vudsen.arthasui.api.conf.JvmProviderConfig

class K8sJvmProviderConfig(
    enabled: Boolean = false,
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
    var authorizationType: AuthorizationType = AuthorizationType.BUILTIN,
    /**
     * 是否为 arm 架构的 k8s 集群
     */
    var isArm: Boolean = false,
    /**
     * 数据目录
     */
    var dataDirectory: String = "/opt/arthas-ui",
) : JvmProviderConfig(TYPE, enabled) {

    enum class AuthorizationType(val displayName: String) {
        /**
         * 使用系统中的 kubect
         */
        BUILTIN("System"),
        TOKEN("Token"),
        KUBE_CONFIG("Kubeconfig"),
        KUBE_CONFIG_FILE("Kubeconfig File"),
    }

    data class TokenAuthorization(
        var token: String = "",
        var url: String = ""
    )


    companion object {
        const val TYPE = "Kubernetes"
    }

    override fun getName(): String {
        return TYPE
    }

    override fun copy(): K8sJvmProviderConfig {
        return deepCopy()
    }

    override fun deepCopy(): K8sJvmProviderConfig {
        return K8sJvmProviderConfig(enabled, kubeConfigFilePath, kubeConfig, token, validateSSL, authorizationType, isArm, dataDirectory)
    }

}