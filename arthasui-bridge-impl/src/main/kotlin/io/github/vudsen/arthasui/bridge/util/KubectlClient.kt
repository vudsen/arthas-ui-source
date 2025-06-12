package io.github.vudsen.arthasui.bridge.util

import com.intellij.openapi.progress.ProgressIndicator
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.bridge.conf.K8sJvmProviderConfig
import io.github.vudsen.arthasui.bridge.factory.ToolChainManagerUtil
import io.github.vudsen.arthasui.common.util.ProgressIndicatorStack
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets


class KubectlClient(
    val hostMachine: ShellAvailableHostMachine,
    val config: K8sJvmProviderConfig,
    /**
     * 设置容器. 若该参数非空，则后续指令只能使用 exc 或其它可以指定容器的指令
     */
    var container: String? = null,
) {

    private val baseCommand: MutableList<String> by lazy {
        val pair = resolveKubectl()
        val commands = mutableListOf<String>()
        commands.add(pair.first)
        pair.second ?.let {
            commands.add("--kubeconfig=${it}")
        }
        container ?.let {
            commands.add("-c")
            commands.add(it)
        }
        commands
    }

    private var checked = false

    private fun isKubeconfigLatest(kubeconfigPath: String, currentContent: String): Boolean {
        // TODO 在保存配置时覆盖 kubeconfig
        if (checked) {
            return true
        }
        checked = true
        return String(File(kubeconfigPath).readBytes()) == currentContent
    }

    private fun createKubeconfig(hostMachine: ShellAvailableHostMachine, content: String): String {
        var tempFile: File? = null
        val dest = hostMachine.getHostMachineConfig().dataDirectory + "/conf/kubeconfig"
        try {
            if (!hostMachine.isFileNotExist(dest) && isKubeconfigLatest(dest, content)) {
                return dest
            }
            hostMachine.mkdirs(hostMachine.getHostMachineConfig().dataDirectory + "/conf")
            tempFile = File.createTempFile("arthas-ui-kubeconfig", "")
            FileOutputStream(tempFile).use {ins ->
                ins.write(content.toByteArray(StandardCharsets.UTF_8))
            }
            hostMachine.transferFile(
                tempFile.absolutePath,
                dest,
                ProgressIndicatorStack.currentIndicator()
            )
        } finally {
            tempFile?.delete()
        }

        return dest
    }

    /**
     * 获取 kubectl
     * @return key: kubectl 可执行文件路径, value: kubeconfig 路径, 为空时表示使用默认位置
     */
    private fun resolveKubectl(): Pair<String, String?> {
        if (config.authorizationType == K8sJvmProviderConfig.AuthorizationType.BUILTIN) {
            return Pair("kubectl", null)
        }
        val toolChainManager = ToolChainManagerUtil.createToolChainManager(hostMachine)
        val kubectl = toolChainManager.getToolChainHomePath(ToolChain.KUBECTL)
        if (config.authorizationType == K8sJvmProviderConfig.AuthorizationType.KUBE_CONFIG_FILE) {
            return Pair(kubectl, config.kubeConfigFilePath)
        } else if (config.authorizationType == K8sJvmProviderConfig.AuthorizationType.KUBE_CONFIG){
            return Pair(kubectl, createKubeconfig(hostMachine, config.kubeConfig!!))
        } else if (config.authorizationType == K8sJvmProviderConfig.AuthorizationType.TOKEN) {
            val token = config.token!!
            return Pair(kubectl, createKubeconfig(hostMachine, """
                apiVersion: v1
                kind: Config
                clusters:
                - cluster:
                    server: ${token.url}
                    insecure-skip-tls-verify: true
                  name: my-cluster
                users:
                - name: token-user
                  user:
                    token: ${token.token}
                contexts:
                - context:
                    cluster: my-cluster
                    user: token-user
                  name: token-context
                current-context: token-context
                """.trimIndent()))
        } else {
            throw IllegalStateException("Unsupported authorization type: ${config.authorizationType}")
        }
    }

    /**
     * 执行命令，不需要提供 `kubectl`，直接使用命令即可
     */
    fun execute(vararg command: String): CommandExecuteResult {
        val cmds = ArrayList(baseCommand)
        cmds.addAll(command)
        return hostMachine.execute(*cmds.toTypedArray())
    }

    fun createInteractiveShell(vararg command: String): InteractiveShell {
        val cmds = ArrayList(baseCommand)
        cmds.addAll(command)
        return hostMachine.createInteractiveShell(*cmds.toTypedArray())
    }
}