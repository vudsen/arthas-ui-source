package io.github.vudsen.arthasui.bridge.util

import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.bean.LocalJVM
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.bridge.bean.DockerJvm
import io.github.vudsen.arthasui.bridge.conf.JvmInDockerProviderConfig
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig

object BridgeUtils {


    /**
     * 根据 jvm 找到对应的 provider
     */
    fun findProvider(providers: List<JvmProviderConfig>, jvm: JVM): JvmProviderConfig? {
        val expectedClass = when(jvm::class) {
            LocalJVM::class -> LocalJvmProviderConfig::class
            DockerJvm::class -> JvmInDockerProviderConfig::class
            else -> throw IllegalStateException("Unreachable code.")
        }
        for (provider in providers) {
            if (provider::class == expectedClass) {
                return provider
            }
        }
        return null
    }

}