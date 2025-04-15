package io.github.vudsen.arthasui.bridge.toolchain

import com.google.gson.JsonObject
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class DefaultToolChainManager(private val template: HostMachineTemplate, private val hostMachineConfig: HostMachineConfig) : ToolchainManager {


    companion object {
        private const val JATTACH_BUNLDE = "jattach"
        private const val ARTHAS_BUNDLE = "arthas-bin.zip"
        private val logger = Logger.getInstance(DefaultToolChainManager::class.java)
    }

    /**
     * @return the file name.
     */
    private fun downloadJattach(): String {
        val data = fetchLatestData("jattach/attach")
        val versions = data.get("assets").asJsonArray
        val asset = when (hostMachineConfig.connect.getOS()) {
            OS.LINUX -> {
                if (template.isArm()) {
                    versions.find { v -> v.asJsonObject.get("name").asString.endsWith("arm64.tgz") }
                } else {
                    versions.find { v -> v.asJsonObject.get("name").asString.endsWith("x64.tgz") }
                }
            }

            OS.WINDOWS -> {
                versions.find { v -> v.asJsonObject.get("name").asString.endsWith("windows.zip") }
            }

            OS.MAC -> {
                versions.find { v -> v.asJsonObject.get("name").asString.endsWith("macos.zip") }
            }
        }
        if (asset == null) {
            if (logger.isDebugEnabled) {
                logger.debug(data.toString())
            }
            throw IllegalStateException("No suitable jattach asset found for ${hostMachineConfig.connect.getOS()}")
        }
        val asobj = asset.asJsonObject
        template.download(asobj.get("browser_download_url").asString, hostMachineConfig.connect.dataDirectory)
        return asobj.get("name").asString
    }

    private fun downloadArthas() {
        val data = fetchLatestData("alibaba/arthas")
        val versions = data.get("assets").asJsonArray
        val asset = versions.find { v -> v.asJsonObject.get("name").asString == "arthas-bin.zip" }
        if (asset == null) {
            if (logger.isDebugEnabled) {
                logger.debug(data.toString())
            }
            throw IllegalStateException("No suitable arthas asset found for ${hostMachineConfig.connect.getOS()}")
        }
        template.download(asset.asJsonObject.get("browser_download_url").asString, hostMachineConfig.connect.dataDirectory)
    }

    override fun ensureToolChainDownloaded() {
        val dataDirectory = template.mkdirs(hostMachineConfig.dataDirectory)
        if (template.isFileNotExist("$dataDirectory/$JATTACH_BUNLDE")) {
            downloadJattach()
        }
        if (template.isFileNotExist("$dataDirectory/$ARTHAS_BUNDLE")) {
            downloadArthas()
        }
    }

    override fun isNotAllToolChainExist(): Boolean {
        val dataDirectory = hostMachineConfig.dataDirectory
        return template.isFileNotExist("$dataDirectory/$JATTACH_BUNLDE") || template.isFileNotExist("$dataDirectory/$ARTHAS_BUNDLE")
    }

    override fun getToolChainPath(toolChain: ToolChain): String {
        return when (toolChain) {
            ToolChain.JATTACH -> "${hostMachineConfig.dataDirectory}/$JATTACH_BUNLDE"
            ToolChain.ARTHAS -> "${hostMachineConfig.dataDirectory}/$ARTHAS_BUNDLE"
        }
    }


    private fun fetchLatestData(pkg: String): JsonObject {
        val url = URL("https://api.github.com/repos/$pkg/releases/latest")

        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"

        BufferedReader(InputStreamReader(connection.inputStream)).use { reader ->
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            return service<SingletonInstanceHolderService>().gson.toJsonTree(response.toString()).asJsonObject
        }
    }

}