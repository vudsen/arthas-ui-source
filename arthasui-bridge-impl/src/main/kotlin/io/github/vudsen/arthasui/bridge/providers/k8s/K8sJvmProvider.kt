package io.github.vudsen.arthasui.bridge.providers.k8s

import com.intellij.openapi.Disposable
import io.github.vudsen.arthasui.api.ArthasBridgeFactory
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
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

        val namespaces = client.execute("get", "ns", "-o", "jsonpath='{.items[*].metadata.name}'").ok().split(' ')

        return JvmSearchResult(null, namespaces.map { ns ->
            K8sNamespaceChildSearcher(client, ns)
        })
    }

    override fun createArthasBridgeFactory(
        jvm: JVM,
        jvmProviderConfig: JvmProviderConfig
    ): ArthasBridgeFactory {
        return ArthasBridgeFactory {
            jvm as PodJvm
            val hostMachine = jvm.context.template as ShellAvailableHostMachine
            jvmProviderConfig as K8sJvmProviderConfig
            val client = KubectlClient(hostMachine, jvmProviderConfig, jvm.containerName)
            val toolchainManager = ToolChainManagerUtil.createToolChainManager(hostMachine)

            client.execute("exec", "-n", jvm.namespace, "pod/${jvm.name}", "--", "mkdir", "-p", "/opt/arthas-ui/arthas").ok()
            client.execute("exec", "-n", jvm.namespace, "pod/${jvm.name}", "--", "mkdir", "-p", "/opt/arthas-ui/jattach").ok()
            client.execute("cp", "-r", toolchainManager.getToolChainHomePath(ToolChain.ARTHAS_BUNDLE), "${jvm.name}:/opt/arthas-ui/arthas").ok()
            client.execute("cp", "-r", toolchainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE), "${jvm.name}:/opt/arthas-ui/jattach").ok()

            client.execute("exec", "-n", jvm.namespace, "pod/${jvm.name}", "--",
                "/opt/arthas-ui/jattach/jattach",
                "1",
                "load",
                "instrument",
                "false",
                "/opt/arthas-ui/arthas/arthas-agent.jar"
            ).ok()

            ArthasBridgeImpl(
                client.createInteractiveShell("exec", "-n", jvm.namespace, "pod/${jvm.name}", "--",
                    "java -jar /opt/arthas-ui/arthas/arthas-client.jar"
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

        return client.execute("get", "pod/${jvm.name}").exitCode != 0
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