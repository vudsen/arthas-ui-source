package io.github.vudsen.arthasui.bridge.providers.k8s

import com.google.gson.reflect.TypeToken
import io.kubernetes.client.KubernetesConstants
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.JSON
import io.kubernetes.client.openapi.models.V1Status
import io.kubernetes.client.util.Streams
import io.kubernetes.client.util.WebSocketStreamHandler
import io.kubernetes.client.util.WebSockets
import org.apache.commons.lang3.StringUtils
import java.io.*
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * 替换官方的 [io.kubernetes.client.Exec]，官方的有点过于难用...
 */
class MyK8sExecProcess(
    apiClient: ApiClient,
    namespace: String,
    podName: String,
    stdin: Boolean,
    tty: Boolean,
    command: Array<out String>,
    container: String?
) : Process() {

    private val exitCodeFuture = CompletableFuture<Int>()

    private val actualInputStream = PipedInputStream()

    private val pipedOutputStream = PipedOutputStream(actualInputStream)

    private var stdin: OutputStream? = null

    var name: String? = null

    init {
        val encodedCommand = arrayOfNulls<String>(command.size)
        for (i in command.indices) {
            try {
                encodedCommand[i] = URLEncoder.encode(command[i], "UTF-8")
            } catch (ex: UnsupportedEncodingException) {
                throw RuntimeException("some thing wrong happend: " + ex.message)
            }
        }
        val path = ("/api/v1/namespaces/"
                + namespace
                + "/pods/"
                + podName
                + "/exec?"
                + "stdin="
                + stdin
                + "&stdout="
                + true
                + "&stderr="
                + true
                + "&tty="
                + tty
                + (if (container != null) "&container=" + container else "")
                + "&command="
                + StringUtils.join(encodedCommand, "&command="))

        val listener = MyK8sSocketListener()
        WebSockets.stream(path, "GET", apiClient, listener)
        if (stdin) {
            this.stdin = listener.getOutputStream(0)
        }
    }


    inner class MyK8sSocketListener : WebSocketStreamHandler() {

        private val buffer by lazy { ByteArray(2048) }

        override fun handleMessage(stream: Int, inStream: InputStream) {
            name ?.let {
                println(name)
            }
            if (stream != 3) {
                super.handleMessage(stream, inStream)
                if (stream == 255) {
                    // TODO, does the exit code right?
                    exitCodeFuture.complete(1)
                }
                var len = 0
                val underlyingInputStream = getInputStream(stream)
                while (underlyingInputStream.available() > 0 && underlyingInputStream.read(buffer).also { len = it } > 0) {
                    pipedOutputStream.write(buffer, 0, len)
                }
                return
            }
            val returnType = object : TypeToken<V1Status?>() {}.type
            val body: String?
            InputStreamReader(inStream).use { reader ->
                body = Streams.toString(reader)
            }
            val status = JSON.deserialize<V1Status?>(body, returnType)

            if (status == null) {
                exitCodeFuture.complete(1)
            } else if (KubernetesConstants.V1STATUS_SUCCESS == status.status) {
                exitCodeFuture.complete(0)
            } else if (KubernetesConstants.V1STATUS_FAILURE == status.status) {
                pipedOutputStream.write(status.message.toByteArray())
                exitCodeFuture.complete(status.code ?: 1)
            } else {
                pipedOutputStream.write("Unknown response message: $body".toByteArray(StandardCharsets.UTF_8))
                exitCodeFuture.complete(1)
            }
            this.close()
        }


        override fun failure(t: Throwable?) {
            super.failure(t)
            exitCodeFuture.completeExceptionally(t)
        }


        override fun close() {
            super.close()
            pipedOutputStream.close()
        }

    }

    override fun getOutputStream(): OutputStream {
        return stdin!!
    }

    override fun getInputStream(): InputStream {
        return actualInputStream
    }

    override fun getErrorStream(): InputStream {
        return actualInputStream
    }

    override fun waitFor(): Int {
        return exitCodeFuture.get()
    }

    override fun waitFor(timeout: Long, unit: TimeUnit): Boolean {
        try {
            exitCodeFuture.get(timeout, unit)
            return true
        } catch (_: TimeoutException) {
            return false
        }
    }

    override fun exitValue(): Int {
        if (exitCodeFuture.isDone) {
            return exitCodeFuture.get()
        }
        throw IllegalThreadStateException()
    }

    override fun destroy() {
        pipedOutputStream.close()
    }
}