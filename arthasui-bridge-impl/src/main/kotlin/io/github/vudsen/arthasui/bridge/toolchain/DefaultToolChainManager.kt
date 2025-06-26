package io.github.vudsen.arthasui.bridge.toolchain

import com.google.gson.annotations.SerializedName
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import io.github.vudsen.arthasui.api.OS
import io.github.vudsen.arthasui.api.host.ShellAvailableHostMachine
import io.github.vudsen.arthasui.api.host.isFileExist
import io.github.vudsen.arthasui.api.toolchain.ToolChain
import io.github.vudsen.arthasui.api.toolchain.ToolchainManager
import io.github.vudsen.arthasui.common.util.ProgressIndicatorStack
import io.github.vudsen.arthasui.common.util.SingletonInstanceHolderService
import org.apache.http.HttpStatus
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * 默认的工具链管理。
 *
 * 所有的工具链名称，都会以`<用户uid>_<用户gid>开头`(windows 和 mac 除外)，不同的用户，即使是同一个版本的工具链，也会下载多次。
 */
open class DefaultToolChainManager(
    /**
     * 目标宿主机
     */
    private val hostMachine: ShellAvailableHostMachine,
    /**
     * 下载的本地代理宿主机
     */
    private val localDownloadProxy: ShellAvailableHostMachine?,
    /**
     * 下载镜像
     */
    mirror: String? = null,
    /**
     * 当前 uid
     */
    currentUid: String? = null,
    /**
     * 当前 gid
     */
    currentGid: String? = null,
) :
    ToolchainManager {

    private var mirror = mirror ?: "https://api.github.com"

    private val httpClient = HttpClientBuilder.create().build()

    private val privilegePrefix: String


    init {
        val actualUid: String?
        val actualGid: String?

        if (currentUid != null && currentGid != null) {
            actualUid = currentUid
            actualGid = currentGid
        } else if (hostMachine.getOS() == OS.LINUX) {
            actualUid = hostMachine.execute("id", "-u").tryUnwrap()?.trim()
            actualGid = hostMachine.execute("id", "-g").tryUnwrap()?.trim()
        } else {
            actualUid = null
            actualGid = null
        }

        privilegePrefix = if (actualUid != null && actualGid != null) {
            "${actualUid}_${actualGid}_"
        } else {
            ""
        }
    }

    companion object {
        private val logger = Logger.getInstance(DefaultToolChainManager::class.java)
        const val DOWNLOAD_DIRECTORY = "downloads"
        const val TOOLCHAIN_VERSION_FILENAME = "tc_version.txt"
        const val CURRENT_VERSION = 0

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

    /**
     * 搜索已有的包
     * @return 包的绝对路径
     */
    private fun searchPkg(search: String): String? {
        val files = hostMachine.listFiles(getDownloadDirectory())
        for (file in files) {
            if (file.contains(search)) {
                if (file.startsWith(privilegePrefix)) {
                    return "${getDownloadDirectory()}/$file"
                }
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
        val home = preparePackage("punzip", "ybirader/pzip") {
            when (hostMachine.getHostMachine().getOS()) {
                OS.LINUX -> {
                    if (hostMachine.isArm()) {
                        "punzip_Linux_arm64.tar.gz"
                    } else {
                        "punzip_Linux_x86_64.tar.gz"
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
     * @param pkgName 最终要保存的包名
     * @param repo 要使用的仓库
     * @param pkgNameSupplier 使用要使用的包，当等于返回值的字符串时，将会使用该包
     * @return 工具的 home 目录
     */
    private fun preparePackage(pkgName: String, repo: String, pkgNameSupplier: () -> String): String {
        val hostMachineConfig = hostMachine.getHostMachineConfig()
        val home = "${hostMachineConfig.dataDirectory}/pkg/${appendPriviligePrefix(pkgName)}"
        if (hostMachine.isDirectoryExist(home)) {
            return home
        }
        val expected = pkgNameSupplier()
        val target: String = searchPkg(appendPriviligePrefix(expected)) ?: let {
            val asset = fetchLatestData(repo).assets.find { asset -> asset.name == expected } ?: throw IllegalStateException(
                "No suitable asset found for ${repo}, os is: ${
                    hostMachine.getHostMachine().getOS()
                }, arm: ${hostMachine.isArm()}, expected package name:: ${expected}"
            )
            finalDownload(asset)
        }
        hostMachine.mkdirs(home)

        if (hostMachine.tryUnzip(target, home)) {
            return home
        }
        handleUnzipFailed(target, home)
        return home
    }

    private fun appendPriviligePrefix(filename: String): String {
        return "${privilegePrefix}${filename}"
    }

    /**
     * 下载文件，提供委托实现
     * @param url 下载路径
     * @param filename 文件名称
     * @return 文件最终路径
     */
    private fun finalDownload(url: String, filename: String): String {
        val hostMachineConfig = hostMachine.getHostMachineConfig()

        val dest = "${hostMachineConfig.dataDirectory}/${DOWNLOAD_DIRECTORY}/${appendPriviligePrefix(filename)}"
        localDownloadProxy?.let {
            // 下载到本地时不带权限标识, 方便离线安装
            val local = "${it.getHostMachineConfig().dataDirectory}/${DOWNLOAD_DIRECTORY}/${filename}"
            localDownloadProxy.download(url, local)
            hostMachine
                .transferFile(local, dest, ProgressIndicatorStack.currentIndicator())
        } ?: let {
            hostMachine.download(
                url,
                dest
            )
        }
        return dest
    }

    private fun finalDownload(
        asset: Asset,
    ): String {
        return finalDownload(asset.browserDownloadUrl, asset.name)
    }


    override fun getToolChainHomePath(toolChain: ToolChain, version: String?): String {
        initDirectories()
        return when (toolChain) {
            ToolChain.JATTACH_BUNDLE -> preparePackage("jattach", "jattach/jattach") {
                when (hostMachine.getHostMachine().getOS()) {
                    OS.LINUX -> {
                        if (hostMachine.isArm()) {
                            return@preparePackage "jattach-linux-arm64.tgz"
                        } else {
                            return@preparePackage "jattach-linux-x64.tgz"
                        }
                    }
                    OS.WINDOWS -> {
                        return@preparePackage "jattach-windows.zip"
                    }
                    OS.MAC -> {
                        return@preparePackage "jattach-macos.zip"
                    }
                    else -> {
                        throw IllegalStateException("Unsupported OS: ${hostMachine.getHostMachine().getOS()}")
                    }
                }
            }

            ToolChain.ARTHAS_BUNDLE -> preparePackage(
                "arthas",
                "alibaba/arthas"
            ) { "arthas-bin.zip" }

            ToolChain.KUBECTL -> prepareKubectl(version)
        }
    }


    private fun initDirectories() {
        val dataDirectory = hostMachine.getHostMachineConfig().dataDirectory
        val versionFile = "${dataDirectory}/$TOOLCHAIN_VERSION_FILENAME"
        if (hostMachine.isFileExist(versionFile)) {
            return
        }

        hostMachine.mkdirs("$dataDirectory/${DOWNLOAD_DIRECTORY}")
        hostMachine.mkdirs("$dataDirectory/pkg")
        if (hostMachine.getOS() == OS.LINUX) {
            hostMachine.execute("chmod", "777", dataDirectory).ok()
        }
        hostMachine.createFile(versionFile, CURRENT_VERSION.toString())
    }


    private fun prepareKubectl(expectedVersion: String?): String {
        val version: String = resolveKubectlVersion(expectedVersion)
        val downloadUrl: String
        val filename: String
        when (hostMachine.getOS()) {
            OS.WINDOWS -> {
                downloadUrl = "https://dl.k8s.io/release/${version}/bin/windows/amd64/kubectl.exe"
                filename = "kubectl.exe"
            }
            OS.LINUX -> {
                if (hostMachine.isArm()) {
                    downloadUrl = "https://dl.k8s.io/release/${version}/bin/linux/arm64/kubectl"
                    filename = "kubectl-linux-arm"
                } else {
                    downloadUrl = "https://dl.k8s.io/release/${version}/bin/linux/amd64/kubectl"
                    filename = "kubectl-linux"
                }
            }

            OS.MAC -> {
                downloadUrl = "https://dl.k8s.io/release/${version}/bin/darwin/amd64/kubectl"
                filename = "kubectl-darwin"
            }
            else -> throw IllegalStateException("Unknown OS")
        }
        val finalFilePath = "${hostMachine.getHostMachineConfig().dataDirectory}/pkg/$filename"
        if (hostMachine.isFileExist(finalFilePath)) {
            return finalFilePath
        }
        val downloaded = finalDownload(downloadUrl, filename)

        hostMachine.mv(downloaded, finalFilePath, false)
        if (hostMachine.getOS() == OS.LINUX) {
            hostMachine.execute("chmod", "u+x", finalFilePath).ok()
        }
        return finalFilePath
    }

    private fun resolveKubectlVersion(version: String?): String {
        if (!version.isNullOrBlank()) {
            return version
        }
        val httpGet = HttpGet("https://cdn.dl.k8s.io/release/stable.txt")
        httpGet.config = RequestConfig.custom()
            .setConnectTimeout(2000)
            .setConnectionRequestTimeout(2000)
            .setSocketTimeout(2000)
            .build()

        val version: String = httpClient.execute(httpGet).use { response ->
            val body = EntityUtils.toString(response.entity, StandardCharsets.UTF_8)
            if (response.statusLine.statusCode != HttpStatus.SC_OK) {
                throw IllegalStateException("Failed to query latest version of kubernetes, body: $body")
            }
            return@use body
        }
        return version
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