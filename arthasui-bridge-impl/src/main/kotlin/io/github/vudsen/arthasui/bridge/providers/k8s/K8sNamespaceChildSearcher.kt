package io.github.vudsen.arthasui.bridge.providers.k8s

import com.google.gson.JsonObject
import com.intellij.openapi.components.service
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.extension.JvmSearchDelegate
import io.github.vudsen.arthasui.bridge.bean.PodJvm
import io.github.vudsen.arthasui.bridge.util.KubectlClient
import io.github.vudsen.arthasui.common.ArthasUIIcons
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService
import javax.swing.Icon

/**
 * 命名空间节点
 */
class K8sNamespaceChildSearcher(
    private val kubectlClient: KubectlClient,
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

        val ctx = JvmContext(kubectlClient.hostMachine, kubectlClient.config)
        val result = kubectlClient.execute("get", "pods", "-n", namespace, "-o=json").ok()
        val gson = service<SingletonInstanceHolderService>().gson

        val resp = gson.fromJson(result, JsonObject::class.java)

        for (element in resp.getAsJsonArray("items")) {
            val pod = element.asJsonObject
            val spec = pod.getAsJsonObject("spec")
            val ctrs = spec.getAsJsonArray("containers")
            val metadata = pod.getAsJsonObject("metadata")
            val name = metadata.get("name").asString
            val base = PodJvm(name, name, ctx, namespace, null, null, null)
            if (ctrs.size() == 1) {
                ctrs.get(0).asJsonObject.getAsJsonObject("securityContext")?.let {
                    base.uid = it.get("runAsUser")?.asString
                    base.gid = it.get("runAsGroup")?.asString
                }
                jvmResult.add(base)
            } else {
                delegate.add(K8sMultiContainerChildSearcher(base, ctrs))
            }
            spec
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