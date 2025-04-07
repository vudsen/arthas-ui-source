package io.github.vudsen.arthasui.conf.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBSlidingPanel
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.common.util.collectStackTrace
import io.github.vudsen.arthasui.conf.HostMachineConfig
import java.awt.event.ActionEvent
import javax.swing.Action
import javax.swing.JComponent

class NewHostMachineSetupUI(private val project: Project,
                            parentDisposable: Disposable,
                            private val onOk: (HostMachineConfig) -> Unit)
    : DialogWrapper(project, false) {

    companion object {

        private const val TAB_COUNT = 2

        private val logger = Logger.getInstance(NewHostMachineSetupUI::class.java)

    }

    private val jvmConnectUI = JvmConnectSetupUI(parentDisposable)

    private val jvmProviderConfigUI: JvmProviderSetupUI = JvmProviderSetupUI(parentDisposable)

    private var currentIndex = 0

    private lateinit var root: JBSlidingPanel

    private lateinit var myBackAction: Action

    private lateinit var myNextAction: Action


    init {
        title = "New Host Machine"
        setOKButtonText("Next")
        setCancelButtonText("Back")
        createSouthPanel()
        init()
    }


    override fun createCenterPanel(): JComponent {
        val panel = JBSlidingPanel()
        panel.add("New Host Machine", jvmConnectUI.getComponent())
        panel.add("Search Locations", jvmProviderConfigUI.getComponent())
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
                if (currentIndex > 0) {
                    root.goLeft()
                }
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
                    ProgressManager.getInstance().run(object : Task.Modal(project, "Test Connection", true) {

                        override fun run(p0: ProgressIndicator) {
                            val hostMachine = service<HostMachineConnectManager>().connect(jvmConnectUI.apply()!!.connect)
                            try {
                                hostMachine.execute("echo", "hello")
                                currentIndex++
                                updateButtonUI()
                                root.goRight()
                            } catch (e: Exception) {
                                if (logger.isDebugEnabled) {
                                    logger.debug(e.collectStackTrace())
                                }
                                Messages.showErrorDialog(e.message, "Test Connection Failed")
                            }
                        }
                    })
                    return
                }
                currentIndex++
                updateButtonUI()
                if (currentIndex < TAB_COUNT - 1) {
                    root.goRight()
                    return
                }
                TODO("Create the host machine.")
            }
        }

        return arrayOf(myBackAction, myNextAction)
    }


}