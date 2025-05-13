package io.github.vudsen.arthasui.bridge.providers.k8s

import com.intellij.openapi.Disposable
import io.github.vudsen.arthasui.api.ArthasBridgeFactory
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProvider
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.ArthasBridgeImpl
import io.github.vudsen.arthasui.bridge.bean.PodJvm
import io.github.vudsen.arthasui.bridge.conf.K8sJvmProviderConfig
import io.github.vudsen.arthasui.bridge.factory.ToolChainManagerUtil
import io.github.vudsen.arthasui.bridge.host.K8sHostMachine
import io.github.vudsen.arthasui.bridge.toolchain.K8sToolChainManager
import io.github.vudsen.arthasui.bridge.ui.K8sJvmProviderForm
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

class K8sJvmProvider() : JvmProvider {

    override fun getName(): String {
        return "Namespaces"
    }

    override fun searchJvm(
        hostMachine: HostMachine,
        providerConfig: JvmProviderConfig
    ): JvmSearchResult {
        hostMachine as K8sHostMachine
        providerConfig as K8sJvmProviderConfig
        return JvmSearchResult(null, hostMachine.listNamespace().map { ns ->
            K8sNamespaceChildSearcher(hostMachine, providerConfig, ns.metadata.name)
        })
    }

    override fun createArthasBridgeFactory(
        jvm: JVM,
        jvmProviderConfig: JvmProviderConfig
    ): ArthasBridgeFactory {
        return ArthasBridgeFactory {
            jvm as PodJvm
            val hostMachine = jvm.context.template as K8sHostMachine

            val toolChainManager = K8sToolChainManager(
                hostMachine,
                ToolChainManagerUtil.findLocalHostMachine(hostMachine.getConfiguration().localPkgSourceId)
                    ?: throw IllegalStateException("No such local host machine, id: ${hostMachine.getConfiguration().localPkgSourceId}, please check your configuration"),
                jvm,
                ToolChainManagerUtil.mirror
            )

            val arthasHome = toolChainManager.getToolChainHomePath(ToolChain.ARTHAS_BUNDLE)

            // TODO: support choose pid.
            hostMachine.execute(
                jvm,
                "${toolChainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE)}/jattach",
                "1",
                "load",
                "instrument",
                "false",
                "${arthasHome}/arthas-agent.jar"
            ).ok()

            ArthasBridgeImpl(
                hostMachine.createInteractiveShell(
                    jvm,
                    "sh",
                    "-c",
                    "java -jar $arthasHome/arthas-client.jar"
                )
            )
        }
    }

    override fun createForm(
        oldState: JvmProviderConfig?,
        parentDisposable: Disposable
    ): FormComponent<JvmProviderConfig> {
        return K8sJvmProviderForm(oldState, parentDisposable)
    }


    override fun getConfigClass(): Class<out JvmProviderConfig> {
        return K8sJvmProviderConfig::class.java
    }

    override fun getJvmClass(): Class<out JVM> {
        return PodJvm::class.java
    }

    override fun isJvmInactive(jvm: JVM): Boolean {
        jvm as PodJvm
        val hostMachine = jvm.context.template as K8sHostMachine
        return hostMachine.isPodExist(jvm.id, jvm.namespace, jvm.containerName)
    }

    override fun tryCreateDefaultConfiguration(hostMachine: HostMachine): JvmProviderConfig {
        return K8sJvmProviderConfig(true)
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Kubernetes
    }

    override fun isSupport(hostMachine: HostMachine): Boolean {
        return hostMachine is K8sHostMachine
    }
}