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
import io.github.vudsen.arthasui.bridge.host.LocalHostMachineImpl
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

            client.execute("exec", "-n", jvm.namespace, "pod/${jvm.name}", "--", "mkdir", "-p", "/opt/arthas-ui/arthas")
                .ok()
            client.execute(
                "exec",
                "-n",
                jvm.namespace,
                "pod/${jvm.name}",
                "--",
                "mkdir",
                "-p",
                "/opt/arthas-ui/jattach"
            ).ok()


            val jattachHome: String
            val arthasHome: String

            // https://github.com/kubernetes/kubernetes/issues/77310
            // the data directory on windows MUST be located at C:\
            if (hostMachine is LocalHostMachineImpl && currentOS() == OS.WINDOWS) {
                jattachHome = (if (jvmProviderConfig.isArm)
                    toolchainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE) else
                    toolchainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE_LINUX_ARM)
                ).substring(2)
                arthasHome = toolchainManager.getToolChainHomePath(ToolChain.ARTHAS_BUNDLE).substring(2)
            } else {
                jattachHome = if (jvmProviderConfig.isArm)
                    toolchainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE) else
                    toolchainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE_LINUX_ARM)
                arthasHome = toolchainManager.getToolChainHomePath(ToolChain.ARTHAS_BUNDLE)
            }

            // TODO check file exist.
            client.execute("cp", arthasHome, "${jvm.name}:/opt/arthas-ui/", "-n", jvm.namespace).ok()
            client.execute("cp", jattachHome, "${jvm.name}:/opt/arthas-ui/", "-n", jvm.namespace).ok()

            val arthasDirectory = arthasHome.substringAfterLast('/')
            val jattachDirectory = jattachHome.substringAfterLast('/')

            // TODO chmod jattach
            client.execute(
                "exec", "-n", jvm.namespace, "pod/${jvm.name}", "--",
                "/opt/arthas-ui/${jattachDirectory}/jattach",
                "1",
                "load",
                "instrument",
                "false",
                "/opt/arthas-ui/${arthasDirectory}/arthas-agent.jar"
            ).ok()

            ArthasBridgeImpl(
                client.createInteractiveShell(
                    "exec", "-n", jvm.namespace, "pod/${jvm.name}", "--",
                    "java -jar /opt/arthas-ui/${arthasDirectory}/arthas-client.jar"
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