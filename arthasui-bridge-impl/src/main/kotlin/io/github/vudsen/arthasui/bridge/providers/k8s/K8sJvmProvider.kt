package io.github.vudsen.arthasui.bridge.providers.k8s

import com.intellij.openapi.Disposable
import io.github.vudsen.arthasui.api.*
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProvider
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.ArthasBridgeImpl
import io.github.vudsen.arthasui.bridge.bean.PodJvm
import io.github.vudsen.arthasui.bridge.conf.K8sJvmProviderConfig
import io.github.vudsen.arthasui.bridge.factory.ToolChainManagerUtil
import io.github.vudsen.arthasui.bridge.host.K8sPodHostMachine
import io.github.vudsen.arthasui.bridge.toolchain.DefaultToolChainManager
import io.github.vudsen.arthasui.bridge.ui.K8sJvmProviderForm
import io.github.vudsen.arthasui.bridge.util.KubectlClient
import io.github.vudsen.arthasui.common.ArthasUIIcons
import javax.swing.Icon

class K8sJvmProvider : JvmProvider {

    override fun getName(): String {
        return "Kubernetes"
    }

    override fun searchJvm(
        hostMachine: HostMachine,
        providerConfig: JvmProviderConfig
    ): JvmSearchResult {
        providerConfig as K8sJvmProviderConfig
        hostMachine as ShellAvailableHostMachine
        val client = KubectlClient(hostMachine, providerConfig)

        val namespaces =
            client.execute("get", "ns", "-o", "jsonpath='{.items[*].metadata.name}'").ok().trim('\'').split(' ')

        return JvmSearchResult(null, namespaces.map { ns ->
            K8sNamespaceChildSearcher(client, ns)
        })
    }


    /**
     * 由于用户权限的问题，必须先传到远程宿主机，然后再由远程宿主机授权后，再放进容器里解压
     */
    override fun createArthasBridgeFactory(
        jvm: JVM,
        jvmProviderConfig: JvmProviderConfig
    ): ArthasBridgeFactory {
        return ArthasBridgeFactory {
            val hostMachine = jvm.context.template as ShellAvailableHostMachine
            val k8sPodHostMachine = K8sPodHostMachine(jvm as PodJvm, jvmProviderConfig as K8sJvmProviderConfig, hostMachine)

            k8sPodHostMachine.putUserData(HostMachine.PROGRESS_INDICATOR, hostMachine.getUserData(HostMachine.PROGRESS_INDICATOR))
            val toolChainManager = DefaultToolChainManager(
                k8sPodHostMachine,
                hostMachine,
                ToolChainManagerUtil.mirror
            )

            val jattachHome = if (k8sPodHostMachine.isArm()) {
                toolChainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE)
            } else {
                toolChainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE)
            }
            k8sPodHostMachine.execute("chmod", "ug+x", "${jattachHome}/jattach")
            val arthasHome = toolChainManager.getToolChainHomePath(ToolChain.ARTHAS_BUNDLE)

            k8sPodHostMachine.execute(
                "${jattachHome}/jattach",
                "1",
                "load",
                "instrument",
                "false",
                "${arthasHome}/arthas-agent.jar"
            ).ok()

            ArthasBridgeImpl(
                k8sPodHostMachine.createInteractiveShell(
                    "java", "-jar", "${arthasHome}/arthas-client.jar"
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
        val hostMachine = jvm.context.template as ShellAvailableHostMachine
        val client = KubectlClient(hostMachine, jvm.context.providerConfig as K8sJvmProviderConfig, jvm.containerName)

        return client.execute("get", "pod/${jvm.name}", "-n", jvm.namespace).exitCode != 0
    }

    override fun tryCreateDefaultConfiguration(hostMachine: HostMachine): JvmProviderConfig {
        val hostMachine = hostMachine as ShellAvailableHostMachine
        if (hostMachine.execute("kubectl", "version").exitCode == 0) {
            return K8sJvmProviderConfig(true)
        }
        return K8sJvmProviderConfig(false)
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Kubernetes
    }

    override fun isSupport(hostMachine: HostMachine): Boolean {
        return hostMachine is ShellAvailableHostMachine
    }
}