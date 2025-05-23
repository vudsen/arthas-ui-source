package io.github.vudsen.arthasui.bridge.toolchain

import com.google.gson.annotations.SerializedName
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.api.toolchain.ToolchainManager
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class DefaultToolChainManager(
    private val hostMachine: ShellAvailableHostMachine,
    private val localDownloadProxy: ShellAvailableHostMachine?,
    mirror: String? = null
) :
    ToolchainManager {

    var mirror = mirror ?: "https://api.github.com"

    companion object {
        private val logger = Logger.getInstance(DefaultToolChainManager::class.java)
        const val DOWNLOAD_DIRECTORY = "downloads"

        private class ApiData(
            var assets: MutableList<Asset> = mutableListOf()
        )

        private class Asset(
            var name: String = "",

            @SerializedName("browser_download_url")
            var browserDownloadUrl: String = ""
        )
    }

    private fun getDownloadDirectory(): String {
        return hostMachine.getHostMachineConfig().dataDirectory + "/" + DOWNLOAD_DIRECTORY
    }

    private fun searchPkg(search: String): String? {
        val files = hostMachine.listFiles(getDownloadDirectory())
        for (file in files) {
            if (file.startsWith(search) && (file.endsWith("zip") || file.endsWith("tgz") || file.endsWith("tar.gz"))) {
                return file
            }
        }
        return null
    }

    /**
     * 使用外部工具解压
     */
    private fun handleUnzipFailed(target: String, dest: String) {
        logger.info("Can't unzip ${target} by local os tools, downloading unzip tool from remote.")
        if (!target.endsWith(".zip")) {
            throw IllegalStateException("Can't exact $target: no toolchain available.")
        }
        val home = preparePackage("punzip", "ybirader/pzip") { assets ->
            when (hostMachine.getHostMachine().getOS()) {
                OS.LINUX -> {
                    if (hostMachine.isArm()) {
                        assets.find { a -> a.name == "punzip_Linux_arm64.tar.gz" }
                    } else {
                        assets.find { a -> a.name == "punzip_Linux_x86_64.tar.gz" }
                    }
                }

                else -> {
                    // Linux 和 MacOS 只允许本地连接，可以直接依靠 java 自身功能解压.
                    throw IllegalStateException("Unreachable code.")
                }
            }
        }
        val executable = "$home/punzip"
        hostMachine.execute(executable, "-d", dest, target).ok()
    }

    /**
     * 准备工具
     * @return 工具的 home 目录
     */
    private fun preparePackage(pkgName: String, repo: String, pickAsset: (assets: List<Asset>) -> Asset?): String {
        val hostMachineConfig = hostMachine.getHostMachineConfig()
        val home = "${hostMachineConfig.dataDirectory}/pkg/${pkgName}"
        if (hostMachine.isDirectoryExist(home)) {
            return home
        }
        val filename: String = searchPkg(pkgName) ?:let {
            hostMachine.mkdirs(getDownloadDirectory())

            val asset = pickAsset(fetchLatestData(repo).assets) ?: throw IllegalStateException("No suitable asset found for ${repo}, os is: ${hostMachine.getHostMachine().getOS()}, arm: ${hostMachine.isArm()}")
            finalDownload(asset)
        }
        hostMachine.mkdirs(home)

        val unzipTarget = "${hostMachineConfig.dataDirectory}/${DOWNLOAD_DIRECTORY}/$filename"
        if (hostMachine.tryUnzip(unzipTarget, home)) {
            return home
        }
        handleUnzipFailed(unzipTarget, home)
        return home
    }

    /**
     * 下载文件
     * @return 文件名称
     */
    private fun finalDownload(
        asset: Asset,
    ): String {
        val hostMachineConfig = hostMachine.getHostMachineConfig()
        val filename = asset.name

        localDownloadProxy?.let {
            val dest = hostMachineConfig.dataDirectory + "/${DOWNLOAD_DIRECTORY}/" + filename
            val local = it.getHostMachineConfig().dataDirectory + "/${DOWNLOAD_DIRECTORY}/" + filename
            localDownloadProxy.download(asset.browserDownloadUrl, local)
            hostMachine.mkdirs(hostMachineConfig.dataDirectory)
            hostMachine
                .transferFile(local, dest, hostMachine.getUserData(HostMachine.PROGRESS_INDICATOR)?.get())
        } ?: let {
            hostMachine.download(
                asset.browserDownloadUrl,
                hostMachineConfig.dataDirectory + "/${DOWNLOAD_DIRECTORY}/" + filename
            )
        }
        return filename
    }


    override fun getToolChainHomePath(toolChain: ToolChain): String {
        return when (toolChain) {
            ToolChain.JATTACH_BUNDLE -> preparePackage("jattach", "jattach/jattach") { assets ->
                when (hostMachine.getHostMachine().getOS()) {
                    OS.LINUX -> {
                        if (hostMachine.isArm()) {
                            assets.find { v -> v.name.endsWith("arm64.tgz") }
                        } else {
                            assets.find { v -> v.name.endsWith("x64.tgz") }
                        }
                    }

                    OS.WINDOWS -> {
                        assets.find { v -> v.name.endsWith("windows.zip") }
                    }

                    OS.MAC -> {
                        assets.find { v -> v.name.endsWith("macos.zip") }
                    }
                    else -> {
                        throw IllegalStateException("Unsupported OS: ${hostMachine.getHostMachine().getOS()}")
                    }
                }
            }
            ToolChain.ARTHAS_BUNDLE -> preparePackage("arthas", "alibaba/arthas") { assets -> assets.find { v -> v.name == "arthas-bin.zip" } }
        }
    }


    private fun fetchLatestData(pkg: String): ApiData {
        val url = URL("${mirror}/repos/$pkg/releases/latest")

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
                ), ApiData::class.java
            )
            return tree
        } finally {
            connection.inputStream.close()
            connection.disconnect()
        }
    }

}