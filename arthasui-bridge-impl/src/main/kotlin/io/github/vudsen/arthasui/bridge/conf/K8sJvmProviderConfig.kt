package io.github.vudsen.arthasui.bridge.conf

import io.github.vudsen.arthasui.api.conf.JvmProviderConfig

class K8sJvmProviderConfig(enabled: Boolean = false) : JvmProviderConfig(TYPE, enabled) {

    companion object {
        const val TYPE = "Kubernetes"
    }

    override fun getName(): String {
        return TYPE
    }

    override fun copy(): JvmProviderConfig {
        return K8sJvmProviderConfig(enabled)
    }

    override fun deepCopy(): JvmProviderConfig {
        return K8sJvmProviderConfig(enabled)
    }

}