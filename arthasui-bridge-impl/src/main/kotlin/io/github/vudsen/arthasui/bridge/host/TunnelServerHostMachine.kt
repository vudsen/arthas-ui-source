package io.github.vudsen.arthasui.bridge.host

import com.google.gson.JsonObject
import com.intellij.openapi.components.service
import com.intellij.openapi.util.Key
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.bridge.conf.TunnelServerConnectConfig
import io.github.vudsen.arthasui.common.util.ListStringTokenType
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import java.nio.charset.StandardCharsets

class TunnelServerHostMachine(private val hostMachineConfig: HostMachineConfig) : HostMachine {


    companion object {
        class Agent(
            var agentId: String,
            var clientConnectHost: String,
        )
    }

    override fun getOS(): OS {
        return OS.UNKNOWN
    }

    override fun getConfiguration(): TunnelServerConnectConfig {
        return hostMachineConfig.connect as TunnelServerConnectConfig
    }

    override fun getHostMachineConfig(): HostMachineConfig {
        return hostMachineConfig
    }

    private fun createHttpGet(url: String): HttpGet {
        val httpGet = HttpGet(url)
        httpGet.config = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .setSocketTimeout(5000)
            .build()
        return httpGet
    }

    private fun sendGet(url: String): CloseableHttpResponse {
        HttpClients.createDefault().use { client ->
            val httpGet = HttpGet(url)
            httpGet.config = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000)
                .build()
            return client.execute(httpGet)
        }
    }

    private fun ensureStatusCode(response: CloseableHttpResponse) {
        if (response.statusLine.statusCode != 200) {
            throw IllegalStateException(
                "Unexpected response status: ${response.statusLine.statusCode}, body: ${EntityUtils.toString(response.entity,
                    StandardCharsets.UTF_8)}")
        }
    }

    /**
     * 列出所有 app
     */
    fun listApps(): List<String> {
        val config = getConfiguration()
        HttpClients.createDefault().use { client ->
            client.execute(createHttpGet(config.baseUrl + "/api/tunnelApps")).use { response ->
                ensureStatusCode(response)
                return service<SingletonInstanceHolderService>().gson.fromJson(
                    EntityUtils.toString(response.entity, StandardCharsets.UTF_8),
                    ListStringTokenType()
                )
            }
        }
    }

    fun listAgents(appName: String): List<Agent> {
        val config = getConfiguration()
        HttpClients.createDefault().use { client ->
            client.execute(createHttpGet(config.baseUrl + "/api/tunnelAgentInfo?app=" + appName)).use { response ->
                ensureStatusCode(response)
                val jsonObject = service<SingletonInstanceHolderService>().gson.fromJson(
                    EntityUtils.toString(response.entity, StandardCharsets.UTF_8),
                    JsonObject::class.java
                )
                val result = ArrayList<Agent>(jsonObject.size())
                for (k in jsonObject.keySet()) {
                    val element = jsonObject.getAsJsonObject(k)
                    result.add(Agent(
                        k,
                        element.get("clientConnectHost").asString
                    ))
                }
                return result
            }
        }
    }

    override fun test() {
        val config = getConfiguration()
        sendGet(config.baseUrl).use { response ->
            ensureStatusCode(response)
        }
    }
    private val myData = HashMap<Key<*>, Any?>()

    override fun <T : Any?> getUserData(key: Key<T>): T? {
        return myData[key] as T?
    }

    override fun <T : Any?> putUserData(key: Key<T>, value: T?) {
        myData[key] = value
    }

}