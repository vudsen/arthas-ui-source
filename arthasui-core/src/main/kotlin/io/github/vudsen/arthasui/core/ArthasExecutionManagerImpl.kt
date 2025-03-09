package io.github.vudsen.arthasui.core

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import io.github.vudsen.arthasui.api.*
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.bridge.ArthasAttachHelper
import java.util.concurrent.ConcurrentHashMap

/**
 * 协调命令的执行
 */
class ArthasExecutionManagerImpl : ArthasExecutionManager {


    companion object {

        private val log = Logger.getInstance(ArthasExecutionManagerImpl::class.java)

        private data class ArthasBridgeHolder(
            val arthasBridge: ArthasBridgeTemplate,
            var hostMachineConfig: HostMachineConnectConfig
        )

    }

    /**
     * 保存所有链接
     */
    private val bridges = ConcurrentHashMap<JVM, ArthasBridgeHolder>()


    private fun getHolderAndEnsureAlive(jvm: JVM): ArthasBridgeHolder? {
        bridges[jvm] ?.let {
            if (it.arthasBridge.isAlive()) {
                return it
            }
            bridges.remove(jvm)
            return null
        } ?: return null
    }


    private fun getOrInitHolder(jvm: JVM, hostMachineConfig: HostMachineConnectConfig, providerConfig: JvmProviderConfig): ArthasBridgeHolder {
        var holder = getHolderAndEnsureAlive(jvm)
        if (holder != null) {
            return holder
        }
        log.info("Creating new arthas bridge for $jvm")

        val factory = service<HostMachineFactory>()
        val hostMachine = factory.getHostMachine(hostMachineConfig)
        val arthasBridgeFactory =
            service<ArthasAttachHelper>().createArthasBridgeFactory(hostMachine, jvm, providerConfig)
        val arthasBridgeTemplate = ArthasBridgeTemplate(arthasBridgeFactory)

        arthasBridgeTemplate.addListener(object : ArthasBridgeListener() {
            override fun onClose() {
                bridges.remove(jvm)
            }
        })
        holder = ArthasBridgeHolder(arthasBridgeTemplate, hostMachineConfig)

        bridges[jvm] = holder
        return holder
    }


    /**
     * 初始化一个 [ArthasBridgeTemplate]
     */
    override fun initTemplate(jvm: JVM, hostMachineConfig: HostMachineConnectConfig, providerConfig: JvmProviderConfig): ArthasBridgeTemplate {
        return getOrInitHolder(jvm, hostMachineConfig, providerConfig).arthasBridge
    }

    /**
     * 是否已经连接过了
     */
    override fun isAttached(jvm: JVM): Boolean{
        return getHolderAndEnsureAlive(jvm) != null
    }

    /**
     * 获取已经创建的 [ArthasBridgeTemplate]，需要先调用 [initTemplate] 来缓存
     */
    override fun getTemplate(jvm: JVM): ArthasBridgeTemplate? {
        return getHolderAndEnsureAlive(jvm)?.arthasBridge
    }


}