package io.github.vudsen.arthasui.core

import ai.grazie.utils.WeakHashMap
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import io.github.vudsen.arthasui.api.*
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import java.lang.ref.WeakReference

/**
 * 协调命令的执行
 */
class ArthasExecutionManagerImpl() : ArthasExecutionManager {


    companion object {

        private val log = Logger.getInstance(ArthasExecutionManagerImpl::class.java)

        private data class ArthasBridgeHolder(
            val arthasBridge: ArthasBridgeTemplate,
            var hostMachineConfig: HostMachineConfig
        )

    }

    /**
     * 保存所有链接
     */
    private val bridges = WeakHashMap<JVM, ArthasBridgeHolder>()

    private fun getHolderAndEnsureAlive(jvm: JVM): ArthasBridgeHolder? {
        bridges[jvm] ?.let {
            if (it.arthasBridge.isClosed()) {
                bridges.delete(jvm)
                return null
            }
            return it
        }
        return null
    }


    private fun getOrInitHolder(jvm: JVM, hostMachineConfig: HostMachineConfig, providerConfig: JvmProviderConfig, progressIndicator: ProgressIndicator?): ArthasBridgeHolder {
        var holder = getHolderAndEnsureAlive(jvm)
        if (holder != null) {
            return holder
        }
        log.info("Creating new arthas bridge for $jvm")

        val factory = service<HostMachineConnectManager>()
        val template = factory.connect(hostMachineConfig)

        progressIndicator ?.let {
            template.putUserData(HostMachine.PROGRESS_INDICATOR, WeakReference(it))
        }

        val arthasBridgeFactory =
            service<JvmProviderManager>().getProvider(providerConfig).createArthasBridgeFactory(jvm, providerConfig)
        val arthasBridgeTemplate = ArthasBridgeTemplate(arthasBridgeFactory)

        arthasBridgeTemplate.addListener(object : ArthasBridgeListener() {
            override fun onClose() {
                bridges.delete(jvm)
            }
        })
        holder = ArthasBridgeHolder(arthasBridgeTemplate, hostMachineConfig)

        bridges[jvm] = holder
        return holder
    }


    /**
     * 初始化一个 [ArthasBridgeTemplate]
     */
    override fun initTemplate(jvm: JVM, hostMachineConfig: HostMachineConfig, providerConfig: JvmProviderConfig): ArthasBridgeTemplate {
        return getOrInitHolder(jvm, hostMachineConfig, providerConfig, null).arthasBridge
    }

    override fun initTemplate(
        jvm: JVM,
        hostMachineConfig: HostMachineConfig,
        providerConfig: JvmProviderConfig,
        progressIndicator: ProgressIndicator?
    ): ArthasBridgeTemplate {
        return getOrInitHolder(jvm, hostMachineConfig, providerConfig, progressIndicator).arthasBridge
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