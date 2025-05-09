package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.bridge.conf.TunnelServerConnectConfig
import io.github.vudsen.arthasui.common.validation.TextComponentValidators

class TunnelServerConnectForm(
    oldState: HostMachineConnectConfig?,
    parentDisposable: Disposable
) : AbstractFormComponent<HostMachineConnectConfig>(parentDisposable){

    private val state: TunnelServerConnectConfig = oldState as? TunnelServerConnectConfig ?: TunnelServerConnectConfig()

    override fun getState(): TunnelServerConnectConfig {
        return state
    }

    override fun createDialogPanel(): DialogPanel {
        return panel {
            group("Connection Config") {
                val textComponentValidators = TextComponentValidators()
                row("Websocket path") {
                    textField().bindText(state::wsPath).align(Align.FILL).validationOnApply(textComponentValidators)
                }
                row("Api base url") {
                    textField().bindText(state::baseUrl).align(Align.FILL).validationOnApply(textComponentValidators)
                }
            }
        }
    }
}