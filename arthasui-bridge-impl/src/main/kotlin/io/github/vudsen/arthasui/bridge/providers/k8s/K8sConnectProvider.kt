package io.github.vudsen.arthasui.bridge.providers.k8s

import com.intellij.openapi.Disposable
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.bean.UIContext
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectProvider
import io.github.vudsen.arthasui.api.ui.FormComponent
import io.github.vudsen.arthasui.bridge.conf.K8sConnectConfig
import io.github.vudsen.arthasui.bridge.host.K8sHostMachine
import io.github.vudsen.arthasui.bridge.ui.K8sConnectForm

class K8sConnectProvider : HostMachineConnectProvider {

    override fun getName(): String {
        return "Kubernetes"
    }

    override fun createForm(
        oldEntity: HostMachineConnectConfig?,
        parentDisposable: Disposable
    ): FormComponent<HostMachineConnectConfig> {
        return K8sConnectForm(oldEntity, parentDisposable)
    }



    override fun connect(config: HostMachineConfig): HostMachine {
        return K8sHostMachine(config)
    }

    override fun getConfigClass(): Class<out HostMachineConnectConfig> {
        return K8sConnectConfig::class.java
    }

    override fun isCloseableHostMachine(): Boolean {
        return false
    }
}