package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Key
import com.intellij.util.Consumer
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.bridge.bean.StandardInteractiveShell
import io.github.vudsen.arthasui.bridge.bean.PodJvm
import io.github.vudsen.arthasui.bridge.conf.K8sConnectConfig
import io.github.vudsen.arthasui.bridge.providers.k8s.MyK8sExecProcess
import io.kubernetes.client.Exec
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.apis.VersionApi
import io.kubernetes.client.openapi.models.V1Namespace
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.util.Config
import java.io.CharArrayReader
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class K8sHostMachine(private val config: HostMachineConfig) : HostMachine {

    companion object {
        private val logger = Logger.getInstance(K8sHostMachine::class.java)
    }

    val apiClient: ApiClient

    init {
        val connect = config.connect as K8sConnectConfig
        if (connect.authorizationType == K8sConnectConfig.AuthorizationType.TOKEN) {
            connect.token ?.let {
                apiClient = Config.fromToken(it.url, it.token, connect.validateSSL)
            } ?: throw IllegalStateException("No kubernetes token is provided")
        } else if (connect.authorizationType == K8sConnectConfig.AuthorizationType.KUBE_CONFIG_FILE) {
            connect.kubeConfigFilePath ?.let {
                apiClient = Config.fromConfig(it)
            } ?: throw IllegalStateException("No kubeconfig file is provided")
        } else {
            connect.kubeConfig ?.let {
                apiClient = Config.fromConfig(CharArrayReader(it.toCharArray()))
            } ?: throw IllegalStateException("No kubeconfig is provided")
        }
    }

    fun listNamespace(): List<V1Namespace> {
        return CoreV1Api(apiClient).listNamespace().execute().items
    }

    fun listPod(namespace: String): List<V1Pod> {
        return CoreV1Api(apiClient).listNamespacedPod(namespace).execute().items
    }


    fun execute(jvm: PodJvm, vararg command: String): CommandExecuteResult {
        val process = MyK8sExecProcess(
            apiClient,
            jvm.namespace,
            jvm.id,
            false,
            true,
            command,
            null
        )

        while (!process.waitFor(1, TimeUnit.SECONDS)) {
            ProgressManager.checkCanceled()
        }

        try {
            val out: ByteArray = byteArrayOf(*process.inputStream.readAllBytes())
            return CommandExecuteResult(String(out, StandardCharsets.UTF_8), process.exitValue())
        } finally {
            process.inputStream.close()
        }
    }

    private fun createOriginalInteractiveShell(jvm: PodJvm, vararg command: String): Process {
        val process = MyK8sExecProcess(
            apiClient,
            jvm.namespace,
            jvm.id,
            true,
            true,
            command,
            null
        )
        process.name = command.joinToString(" ")
        return process
    }

    fun createInteractiveShell(jvm: PodJvm, vararg command: String): InteractiveShell {
        return StandardInteractiveShell(createOriginalInteractiveShell(jvm, *command))
    }


    fun isPodExist(name: String, namespace: String, container: String?): Boolean {
        val api = CoreV1Api(apiClient)
        try {
            val pod = api.readNamespacedPod(name, namespace).execute() ?: return false
            container ?.let {
                for (ctr in pod.spec.containers) {
                    if (ctr.name == container) {
                        return true
                    }
                }
                return false
            }
            return true
        } catch (_: Exception) {
            return false
        }
    }

    override fun getOS(): OS {
        return OS.UNKNOWN
    }

    override fun getConfiguration(): K8sConnectConfig {
        return config.connect as K8sConnectConfig
    }

    override fun getHostMachineConfig(): HostMachineConfig {
        return config
    }

    override fun test() {
        VersionApi(apiClient).code
    }

    private val myData = HashMap<Key<*>, Any?>()

    override fun <T : Any?> getUserData(key: Key<T>): T? {
        return myData[key] as T?
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
        myData[key] = value
    }
}