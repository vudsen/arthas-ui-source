package io.github.vudsen.arthasui.bridge.host

import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.util.Key
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.bean.CommandExecuteResult
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.bridge.bean.StandardInteractiveShell
import io.github.vudsen.arthasui.bridge.bean.PodJvm
import io.github.vudsen.arthasui.bridge.conf.K8sConnectConfig
import io.kubernetes.client.Exec
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.apis.VersionApi
import io.kubernetes.client.openapi.models.V1Namespace
import io.kubernetes.client.openapi.models.V1Pod
import io.kubernetes.client.util.Config
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.CharArrayReader
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path

class K8sHostMachine(private val config: HostMachineConfig) : HostMachine {

    private val apiClient: ApiClient

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
        val process = Exec(apiClient).exec(jvm.namespace, jvm.id, command, false)
        while (!process.waitFor(1, TimeUnit.SECONDS)) {
            ProgressManager.checkCanceled()
        }
        return CommandExecuteResult(String(process.inputStream.readAllBytes(), StandardCharsets.UTF_8), process.exitValue())
    }

    private fun createInteractiveShell0(jvm: PodJvm, vararg command: String): Process {
        return Exec(apiClient).exec(jvm.namespace, jvm.id, command, true)
    }

    fun createInteractiveShell(jvm: PodJvm, vararg command: String): InteractiveShell {
        return StandardInteractiveShell(createInteractiveShell0(jvm, *command))
    }


    private fun packageDirectory(localPath: String): ByteArray {
        val out = ByteArrayOutputStream()
        GzipCompressorOutputStream(BufferedOutputStream(out)).use { gzipOut ->
            TarArchiveOutputStream(gzipOut).use { tarOut ->
                tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
                val sourceDir = Path(localPath)
                Files.walk(sourceDir).forEach { path ->
                    val relativePath = sourceDir.relativize (path).toString().replace(File.separatorChar, '/');
                    if (Files.isDirectory(path)) {
                        // 目录也需要作为 entry 添加进去
                        if (!relativePath.isEmpty()) {
                            val entry = TarArchiveEntry(path.toFile(), "$relativePath/");
                            tarOut.putArchiveEntry(entry);
                            tarOut.closeArchiveEntry();
                        }
                    } else {
                        val entry = TarArchiveEntry(path.toFile(), relativePath);
                        entry.size = Files.size(path);
                        tarOut.putArchiveEntry(entry);
                        Files.copy(path, tarOut);
                        tarOut.closeArchiveEntry();
                    }
                }
                tarOut.finish();

            }
        }
        // tarOut.getBytesWritten()
        return out.toByteArray()
    }

    /**
     * 上传本地文件夹
     * @param localPath 本地文件路径
     * @param destPath 目标文件夹路径
     */
    fun uploadDirectory(jvm: PodJvm, localPath: String, destPath: String): CommandExecuteResult {
        val progressIndicator = getUserData(HostMachine.PROGRESS_INDICATOR)?.get()
        progressIndicator ?.let {
            it.pushState()
            it.text = "Uploading $localPath"
        }

        try {
            val process = createInteractiveShell0(jvm, "tar", "-xmf", "-", "-C", destPath)
            val byteArray = packageDirectory(localPath)
            val inputStream = ByteArrayInputStream(byteArray)
            var len = 0
            var uploaded = 0
            val buf = ByteArray(1024 * 1024)

            while (inputStream.read(buf).also { len = it } != -1) {
                process.outputStream.write(buf, 0, len)
                ProgressManager.checkCanceled()
                uploaded += len
                progressIndicator?.let {
                    it.fraction = uploaded.toDouble() / byteArray.size
                }
            }

            return CommandExecuteResult(
                String(process.inputStream.readAllBytes(), StandardCharsets.UTF_8),
                process.exitValue()
            )
        } finally {
            progressIndicator?.popState()
        }
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
            } ?: let {
                return true
            }
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