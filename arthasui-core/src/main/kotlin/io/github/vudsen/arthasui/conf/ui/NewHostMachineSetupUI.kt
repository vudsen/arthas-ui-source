package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.application.runWriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.JBCardLayout
import com.intellij.ui.components.JBSlidingPanel
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.common.util.MessagesUtils
import io.github.vudsen.arthasui.api.util.collectStackTrace
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import java.awt.Dimension
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.JComponent
import kotlin.time.Duration.Companion.seconds

class NewHostMachineSetupUI(parentDisposable: Disposable,
                            private val onOk: (HostMachineConfig) -> Unit)
    : DialogWrapper(false) {

    companion object {

        private const val TAB_COUNT = 2

        private val logger = Logger.getInstance(NewHostMachineSetupUI::class.java)

        private const val PAGE1 = "New Host Machine"

        private const val PAGE2 = "Search Locations"
    }

    private val jvmConnectUI = JvmConnectSetupUI(parentDisposable)

    private val jvmProviderConfigUI: JvmProviderSetupUI = JvmProviderSetupUI(parentDisposable)

    private var currentIndex = 0

    private lateinit var root: JBSlidingPanel

    private lateinit var myBackAction: Action

    private lateinit var myNextAction: Action

    private var state: HostMachineConfig? = null

    init {
        title = "New Host Machine"
        setOKButtonText("Next")
        setCancelButtonText("Back")
        createSouthPanel()
        init()
    }


    override fun createCenterPanel(): JComponent {
        val panel = JBSlidingPanel()
        panel.preferredSize = Dimension(600, 500)
        panel.add(PAGE1, jvmConnectUI.getComponent())
        panel.add(PAGE2, jvmProviderConfigUI.getComponent())
        root = panel
        return panel
    }

    private fun updateButtonUI() {
        val next = buttonMap[myNextAction]!!
        if (currentIndex == TAB_COUNT - 1) {
            next.text = "Create"
        } else {
            next.text = "Next"
        }
        val back = buttonMap[myBackAction]!!
        back.isEnabled = currentIndex > 0
        next.updateUI()
        back.updateUI()
    }

    private fun isCurrentFormInvalid(): Boolean {
        if (currentIndex == 0) {
            return jvmConnectUI.apply() == null
        }
        return jvmProviderConfigUI.isInvalid()
    }

    override fun createActions(): Array<Action> {
        myBackAction = object : DialogWrapperAction("Back") {

            override fun doAction(p0: ActionEvent?) {
                currentIndex--
                updateButtonUI()
                root.swipe(PAGE1, JBCardLayout.SwipeDirection.BACKWARD)
            }
        }

        myNextAction = object : DialogWrapperAction("Next") {

            init {
                putValue("DefaultAction", true)
            }

            override fun doAction(p0: ActionEvent?) {
                if (isCurrentFormInvalid()) {
                    return
                }
                if (currentIndex == 0) {
                    val hostMachineConfig = jvmConnectUI.apply() ?: return
                    state = hostMachineConfig
                    ProgressManager.getInstance().runProcessWithProgressSynchronously({
                        try {
                            val hostMachine = service<HostMachineConnectManager>().connect(hostMachineConfig)
                            hostMachine.test()
                            currentIndex++
                            updateButtonUI()
                            root.swipe(PAGE2, JBCardLayout.SwipeDirection.FORWARD)
                            jvmProviderConfigUI.refresh(hostMachine)
                        } catch (e: Exception) {
                            if (logger.isDebugEnabled) {
                                logger.debug(e.collectStackTrace())
                            }
                            MessagesUtils.showErrorMessageLater("Test Connection Failed", e.message, null)
                        }
                    }, "Test Connection", true, null, this@NewHostMachineSetupUI.root.parent as JComponent)
                    return
                }
                // currentIndex = 1
                updateButtonUI()
                val state2 = jvmProviderConfigUI.apply() ?: return

                state!!.let {
                    it.providers = state2.providers
                    it.dataDirectory = state2.dataDirectory
                }

                close(OK_EXIT_CODE)
                onOk(state!!)
            }
        }

        return arrayOf(myBackAction, myNextAction)
    }


}