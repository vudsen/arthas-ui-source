package io.github.vudsen.arthasui.bridge.toolchain

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import io.github.vudsen.arthasui.api.template.HostMachineTemplate
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.api.toolchain.ToolchainManager
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class DefaultToolChainManager(private val template: HostMachineTemplate, private val localDownloadProxy: HostMachineTemplate?) :
    ToolchainManager {


    companion object {
        private val logger = Logger.getInstance(DefaultToolChainManager::class.java)
    }

    private fun searchPkg(search: String): String? {
        val files = template.listFiles(template.getHostMachineConfig().dataDirectory)
        for (file in files) {
            if (file.contains(search) && (file.endsWith("zip") || file.endsWith("tgz") || file.endsWith("tar.gz"))) {
                return file
            }
        }
        return null
    }

    /**
     * @return the file name.
     */
    private fun downloadJattach(): String {
        searchPkg("jattach") ?.let {
            return it
        }

        val data = fetchLatestData("jattach/jattach")

        val hostMachineConfig = template.getHostMachineConfig()

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
        return finalDownload(asset, hostMachineConfig)
    }

    private fun prepareJattach(): String {
        val hostMachineConfig = template.getHostMachineConfig()
        val home = "${hostMachineConfig.dataDirectory}/pkg/jattach"
        if (template.isDirectoryExist(home)) {
            return home
        }
        val filename = downloadJattach()
        template.mkdirs(home)
        template.unzip("${hostMachineConfig.dataDirectory}/$filename", home)
        return home
    }

    private fun prepareArthas(): String {
        val hostMachineConfig = template.getHostMachineConfig()
        val home = "${hostMachineConfig.dataDirectory}/pkg/arthas"
        if (template.isDirectoryExist(home)) {
            return home
        }
        val filename = downloadArthas()
        template.mkdirs(home)
        template.unzip("${hostMachineConfig.dataDirectory}/$filename", home)
        return home
    }

    private fun downloadArthas(): String {
        searchPkg("arthas") ?.let {
            return it
        }
        val data = fetchLatestData("alibaba/arthas")
        val versions = data.get("assets").asJsonArray
        val asset = versions.find { v -> v.asJsonObject.get("name").asString == "arthas-bin.zip" }
        val hostMachineConfig = template.getHostMachineConfig()
        if (asset == null) {
            if (logger.isDebugEnabled) {
                logger.debug(data.toString())
            }
            throw IllegalStateException("No suitable arthas asset found for ${hostMachineConfig.connect.getOS()}")
        }
        return finalDownload(asset, hostMachineConfig)
    }

    private fun finalDownload(
        asset: JsonElement,
        hostMachineConfig: HostMachineConfig,
    ): String {
        val asJsonObject = asset.asJsonObject
        val filename = asJsonObject.get("name").asString

        localDownloadProxy?.let {
            val dest = hostMachineConfig.dataDirectory + "/" + filename
            val local = it.getHostMachineConfig().dataDirectory + "/" + filename
            localDownloadProxy.download(asJsonObject.get("browser_download_url").asString, local)
            template.mkdirs(hostMachineConfig.dataDirectory)
            template.getHostMachine()
                .transferFile(local, dest, template.getUserData(HostMachineTemplate.PROGRESS_INDICATOR)?.get())
        } ?: let {
            template.download(
                asJsonObject.get("browser_download_url").asString,
                hostMachineConfig.dataDirectory + "/" + filename
            )
        }
        return filename
    }


    override fun getToolChainHomePath(toolChain: ToolChain): String {
        return when (toolChain) {
            ToolChain.JATTACH_BUNDLE -> prepareJattach()
            ToolChain.ARTHAS_BUNDLE -> prepareArthas()
        }
    }


    private fun fetchLatestData(pkg: String): JsonObject {
        val url = URL("https://api.github.com/repos/$pkg/releases/latest")

        val connection = url.openConnection() as HttpURLConnection
        try {

            connection.requestMethod = "GET"

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IllegalStateException("No suitable artifact found for $pkg")
            }

            val tree = service<SingletonInstanceHolderService>().gson.fromJson(
                String(
                    connection.inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
                ), JsonObject::class.java
            )
            return tree
        } finally {
            connection.inputStream.close()
            connection.disconnect()
        }
    }

}