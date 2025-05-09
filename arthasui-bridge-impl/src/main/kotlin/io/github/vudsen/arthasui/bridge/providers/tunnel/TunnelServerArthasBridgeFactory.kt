package io.github.vudsen.arthasui.bridge.providers.tunnel

import com.google.gson.JsonObject
import com.intellij.openapi.components.service
import com.intellij.util.io.toByteArray
import io.github.vudsen.arthasui.api.ArthasBridge
import io.github.vudsen.arthasui.api.ArthasBridgeFactory
import io.github.vudsen.arthasui.api.bean.InteractiveShell
import io.github.vudsen.arthasui.bridge.ArthasBridgeImpl
import io.github.vudsen.arthasui.bridge.bean.TunnelServerJvm
import io.github.vudsen.arthasui.bridge.conf.TunnelServerConnectConfig
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService
import java.io.PipedReader
import java.io.PipedWriter
import java.io.Reader
import java.io.Writer
import java.net.URI
import java.net.http.HttpClient
import java.net.http.WebSocket
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage

class TunnelServerArthasBridgeFactory(
    private val config: TunnelServerConnectConfig,
    private val jvm: TunnelServerJvm
) : ArthasBridgeFactory {

    companion object {
        private class FakeWriter(private val websocket: WebSocket) : Writer() {

            private val gson = service<SingletonInstanceHolderService>().gson

            private fun buildMessage(data: String): String {
                val jsonObject = JsonObject()
                jsonObject.addProperty("action", "read")
                jsonObject.addProperty("data", data)
                return gson.toJson(jsonObject)
            }

            override fun write(cbuf: CharArray, off: Int, len: Int) {
                websocket.sendText(buildMessage(String(cbuf, off, len)), true)
            }

            override fun write(str: String) {
                websocket.sendText(buildMessage(str), true)
            }

            override fun flush() {}

            override fun close() {}



        }
    }

    private var exitCode: Int? = null

    private inner class WebSocketInteractiveShell(
        private val reader: Reader,
        private val writer: Writer,
        private val websocket: WebSocket
    ) : InteractiveShell {

        override fun getReader(): Reader {
            return reader
        }

        override fun getWriter(): Writer {
            return writer
        }

        override fun isAlive(): Boolean {
            return exitCode == null
        }

        override fun exitCode(): Int? {
            return exitCode
        }

        override fun close() {
            websocket.sendClose(0, "Client exit.")
            reader.close()
            writer.close()
        }

    }

    private inner class MyWebSocketListener(private val writer: Writer, private val future: CompletableFuture<Unit>) : WebSocket.Listener {

        override fun onBinary(
            webSocket: WebSocket?,
            data: ByteBuffer,
            last: Boolean
        ): CompletionStage<*>? {
            writer.write(String(data.toByteArray(), StandardCharsets.UTF_8))
            return super.onBinary(webSocket, data, last)
        }

        override fun onText(webSocket: WebSocket?, data: CharSequence, last: Boolean): CompletionStage<*>? {
            writer.write(data.toString())
            return super.onText(webSocket, data, last)
        }

        override fun onOpen(webSocket: WebSocket?) {
            future.complete(Unit)
            super.onOpen(webSocket)
        }

        override fun onClose(
            webSocket: WebSocket?,
            statusCode: Int,
            reason: String?
        ): CompletionStage<*>? {
            exitCode = statusCode
            if (future.isDone) {
                future.completeExceptionally(IllegalStateException(reason))
            }
            return super.onClose(webSocket, statusCode, reason)
        }

        override fun onError(webSocket: WebSocket?, error: Throwable?) {
            super.onError(webSocket, error)
        }
    }


    override fun createBridge(): ArthasBridge {
        val client = HttpClient.newHttpClient()
        val actualReader = PipedReader()

        val future = CompletableFuture<Unit>()

        val webSocket = client.newWebSocketBuilder()
            .buildAsync(
                URI.create(config.wsPath + "?method=connectArthas&id=${jvm.id}&targetServer=${jvm.agent.clientConnectHost}"),
                MyWebSocketListener(PipedWriter(actualReader), future)
            )
            .join()

        future.get()
        return ArthasBridgeImpl(WebSocketInteractiveShell(actualReader, FakeWriter(webSocket), webSocket))
    }


}