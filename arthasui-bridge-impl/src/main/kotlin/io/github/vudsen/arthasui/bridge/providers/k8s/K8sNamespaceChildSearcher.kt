package io.github.vudsen.arthasui.bridge.providers.k8s

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.extension.JvmSearchDelegate
import io.github.vudsen.arthasui.bridge.bean.PodJvm
import io.github.vudsen.arthasui.bridge.conf.K8sJvmProviderConfig
import io.github.vudsen.arthasui.bridge.host.K8sHostMachine
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

/**
 * 命名空间节点
 */
class K8sNamespaceChildSearcher(
    private val hostMachine: K8sHostMachine,
    private val providerConfig: K8sJvmProviderConfig,
    private val namespace: String
) : JvmSearchDelegate {

    override fun getName(): String {
        return namespace
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Cubes
    }

    override fun load(): JvmSearchResult {
        val jvmResult = mutableListOf<JVM>()
        val delegate = mutableListOf<JvmSearchDelegate>()
        val ctx = JvmContext(hostMachine, providerConfig)
        for (pod in hostMachine.listPod(namespace)) {
            val containers = pod.spec.containers
            if (containers.size == 1) {
                jvmResult.add(PodJvm(pod.metadata.name, pod.metadata.name, ctx, namespace, null))
            } else {
                delegate.add(K8sMultiContainerChildSearcher(ctx, pod))
            }
        }
        return JvmSearchResult(jvmResult, delegate)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as K8sNamespaceChildSearcher

        return namespace == other.namespace
    }

    override fun hashCode(): Int {
        return namespace.hashCode()
    }


}