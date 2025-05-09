package io.github.vudsen.arthasui.bridge.providers

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import io.github.vudsen.arthasui.api.ArthasBridgeFactory
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.JvmContext
import io.github.vudsen.arthasui.api.bean.JvmSearchResult
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.JvmProvider
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.api.toolchain.ToolchainManager
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.ArthasBridgeImpl
import io.github.vudsen.arthasui.bridge.bean.DockerJvm
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.bridge.factory.ToolChainManagerFactory
import io.github.vudsen.arthasui.bridge.ui.DockerJvmProviderForm
import io.github.vudsen.arthasui.common.ArthasUIIcons
import io.github.vudsen.arthasui.common.util.MapTypeToken
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService
import javax.swing.Icon

class DockerJvmProvider : JvmProvider {

    companion object {

        @JvmStatic
        fun parseOutput(
            execResult: String,
            ctx: JvmContext
        ): MutableList<JVM> {
            val gson = service<SingletonInstanceHolderService>().gson
            val containers = execResult.split('\n')
            val result = mutableListOf<JVM>()
            for (container in containers) {
                if (container.isEmpty()) {
                    continue
                }
                val tree = gson.fromJson(container, MapTypeToken())
                result.add(
                    DockerJvm(
                        tree["ID"]!!,
                        "${tree["Names"]!!}(${tree["Image"]!!})",
                        ctx
                    )
                )
            }
            return result
        }

    }

    override fun getName(): String {
        return "Docker"
    }

    override fun searchJvm(hostMachine: HostMachine, providerConfig: JvmProviderConfig): JvmSearchResult {
        if (hostMachine !is ShellAvailableHostMachine) {
            return JvmSearchResult(emptyList())
        }
        val config = providerConfig as JvmInDockerProviderConfig
        val execResult = hostMachine.execute(config.dockerPath, "ps", "--format=json").ok()

        return JvmSearchResult(parseOutput(execResult, JvmContext(hostMachine, providerConfig)))
    }

    private fun isDirectoryNotExistInContainer(hostMachine: ShellAvailableHostMachine, id: String, path: String): Boolean {
        return hostMachine.execute("docker", "exec", "-i", id, "test", "-d", path).exitCode != 0
    }

    override fun createArthasBridgeFactory(
        jvm: JVM,
        jvmProviderConfig: JvmProviderConfig,
    ): ArthasBridgeFactory {
        val hostMachine = jvm.context.getHostMachineAsShellAvailable()
        val toolchainManager = ToolChainManagerFactory.createToolChainManager(hostMachine)
        val config = jvmProviderConfig as JvmInDockerProviderConfig
        val javaExecutable = if (config.javaHome.isEmpty()) {
            "java"
        }  else {
            config.javaHome + "/bin/java"
        }

        return ArthasBridgeFactory {
            val jattach = "/tmp/jdk"
            val arthasHome = "/tmp/arthas"
            if (isDirectoryNotExistInContainer(hostMachine, jvm.id, arthasHome)) {
                hostMachine.execute("docker", "cp", toolchainManager.getToolChainHomePath(ToolChain.ARTHAS_BUNDLE), "${jvm.id}:$arthasHome").ok()
            }
            if (isDirectoryNotExistInContainer(hostMachine, jvm.id, jattach)) {
                hostMachine.execute("docker", "cp", toolchainManager.getToolChainHomePath(ToolChain.JATTACH_BUNDLE), "${jvm.id}:$jattach").ok()
            }

            // TODO, support switch pid
            hostMachine.execute("docker", "exec", "-i",
                jvm.id, "$jattach/jattach", "1", "load", "instrument", "false", "\"${arthasHome}/arthas-agent.jar\"").ok()
            return@ArthasBridgeFactory ArthasBridgeImpl(
                hostMachine.createInteractiveShell("docker", "exec", "-i",
                    jvm.id, javaExecutable, "-jar", "${arthasHome}/arthas-client.jar"),
            )
        }
    }


    override fun createForm(oldState: JvmProviderConfig?, parentDisposable: Disposable): FormComponent<JvmProviderConfig> {
        return DockerJvmProviderForm(oldState, parentDisposable)
    }

    override fun getConfigClass(): Class<out JvmProviderConfig> {
        return JvmInDockerProviderConfig::class.java
    }

    override fun getJvmClass(): Class<out JVM> {
        return DockerJvm::class.java
    }

    override fun isJvmInactive(jvm: JVM): Boolean {
        val hostMachine = jvm.context.template
        if (hostMachine !is ShellAvailableHostMachine) {
            return false
        }
        val config = jvm.context.providerConfig as JvmInDockerProviderConfig
        val execResult = hostMachine.execute(config.dockerPath, "ps", "--format=json", "--filter", "id=${jvm.id}").ok()
        return execResult.isEmpty()
    }

    override fun tryCreateDefaultConfiguration(hostMachine: HostMachine): JvmProviderConfig {
        if (hostMachine is ShellAvailableHostMachine) {
            val result = hostMachine.execute("docker", "version")
            return JvmInDockerProviderConfig(result.exitCode == 0)
        }
        return JvmInDockerProviderConfig(false)
    }

    override fun getIcon(): Icon {
        return ArthasUIIcons.Box
    }


    override fun isSupport(hostMachine: HostMachine): Boolean {
        return hostMachine is ShellAvailableHostMachine
    }

}