package io.github.vudsen.arthasui.bridge.providers.k8s

import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.extension.JvmSearchDelegate
import io.github.vudsen.arthasui.bridge.bean.PodJvm
import io.github.vudsen.arthasui.common.ArthasUIIcons
import io.kubernetes.client.openapi.models.V1Pod
import javax.swing.Icon

/**
 * 多容器 Pod 搜索节点
 */
class K8sMultiContainerChildSearcher(
    private val context: JvmContext,
    private val pod: V1Pod
) : JvmSearchDelegate{
    override fun getName(): String {
        return pod.metadata.name
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Box
    }

    override fun load(): JvmSearchResult {
        return JvmSearchResult(
            pod.spec.containers.map { ctr -> PodJvm(pod.metadata.name, pod.metadata.name, context, pod.metadata.namespace, ctr.name) },
            null
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as K8sMultiContainerChildSearcher

        return pod == other.pod
    }

    override fun hashCode(): Int {
        return pod.hashCode()
    }


}