package io.github.vudsen.arthasui.bridge.providers.k8s

import com.google.gson.JsonArray
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.extension.JvmSearchDelegate
import io.github.vudsen.arthasui.bridge.bean.PodJvm
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

/**
 * 多容器 Pod 搜索节点
 */
class K8sMultiContainerChildSearcher(
    private val baseJvm: PodJvm,
    private val containers: JsonArray
) : JvmSearchDelegate {

    override fun getName(): String {
        return baseJvm.name
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Box
    }

    override fun load(): JvmSearchResult {
        return JvmSearchResult(
            containers.map { ctr ->
                PodJvm(
                    baseJvm.name,
                    baseJvm.name,
                    baseJvm.context,
                    baseJvm.namespace,
                    ctr.asJsonObject.get("name").asString
                )
            },
            null
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as K8sMultiContainerChildSearcher

        if (baseJvm != other.baseJvm) return false
        if (containers != other.containers) return false

        return true
    }

    override fun hashCode(): Int {
        var result = baseJvm.hashCode()
        result = 31 * result + containers.hashCode()
        return result
    }


}