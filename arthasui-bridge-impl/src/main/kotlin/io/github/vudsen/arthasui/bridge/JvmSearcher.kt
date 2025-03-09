package io.github.vudsen.arthasui.bridge

import com.google.gson.Gson
import io.github.vudsen.arthasui.api.BridgeUtils
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.bridge.bean.DockerJvm

/**
 * 搜索指定位置的 jvm
 */
class JvmSearcher(private val hostMachine: HostMachine) {

    private val gson = Gson()

    private fun readLocalJvm(config: LocalJvmProviderConfig): List<JVM> {
        val result = hostMachine.execute("${config.jdkHome}/bin/jps", "-l")
        if (result.exitCode != 0) {
            TODO("handle non-zero exit code.")
        }
        return BridgeUtils.parseJpsOutput(result.stdout)
    }

    private fun readJvmInDocker(config: JvmInDockerProviderConfig): List<JVM> {
        val execResult = hostMachine.execute(config.dockerPath, "ps", "--format=json")
        if (execResult.exitCode != 0) {
            TODO("handle non-zero exit code.")
        }
        val jsonArray = "[" + execResult.stdout + "]"
        val tree = gson.toJsonTree(jsonArray)
        val result = mutableListOf<JVM>()
        for (element in tree.asJsonArray) {
            val obj = element.asJsonObject
            result.add(DockerJvm(obj.get("ID").asString, obj.get("Names").asString, obj.get("Image").asString))
        }
        return result
    }

    /**
     * 搜索指定配置中的 jvm
     */
    fun searchJvm(provider: JvmProviderConfig): List<JVM> {
        if (provider is LocalJvmProviderConfig) {
            return readLocalJvm(provider)
        } else if (provider is JvmInDockerProviderConfig) {
            return readJvmInDocker(provider)
        }
        return emptyList()
    }


}