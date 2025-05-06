package io.github.vudsen.arthasui.run

import com.intellij.execution.process.*
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import io.github.vudsen.arthasui.api.*
import kotlinx.coroutines.runBlocking
import java.io.OutputStream
import java.io.PrintWriter
import java.io.StringWriter

/**
 * 负责调用 attach 方法.
 */
class ArthasProcessHandler(
    private val project: Project,
    private val jvm: JVM,
) : ProcessHandler(), AnsiEscapeDecoder.ColoredTextAcceptor {

    private val myAnsiEscapeDecoder = AnsiEscapeDecoder()

    private var arthasBridgeTemplate: ArthasBridgeTemplate? = null


    override fun destroyProcessImpl() {
        notifyProcessTerminated(arthasBridgeTemplate?.stop() ?: 1)
    }

    override fun detachProcessImpl() {
        arthasBridgeTemplate?.stop()
    }

    override fun detachIsDefault(): Boolean {
        return false
    }

    override fun getProcessInput(): OutputStream? {
        return null
    }

    override fun startNotify() {
        addProcessListener(object : ProcessListener {

            override fun startNotified(event: ProcessEvent) {
                ProcessIOExecutorService.INSTANCE.execute {
                    runBlocking {
                        notifyTextAvailable("Trying to attach to target jvm: ${jvm.name}\n", ProcessOutputTypes.STDOUT)
                        try {
                            val arthasExecutionManager = project.service<ArthasExecutionManager>()
                            val bridgeTemplate = arthasExecutionManager.getTemplate(jvm)!!
                            this@ArthasProcessHandler.arthasBridgeTemplate = bridgeTemplate
                            bridgeTemplate.addListener(object : ArthasBridgeListener() {
                                override fun onContent(result: String) {
                                    notifyTextAvailable(result, ProcessOutputTypes.STDOUT)
                                }

                                override fun onClose() {
                                    notifyProcessTerminated(bridgeTemplate.stop())
                                }
                            })

                            bridgeTemplate.attachNow()
                            if (!bridgeTemplate.isAlive()) {
                                notifyProcessTerminated(bridgeTemplate.stop())
                                return@runBlocking
                            }
                        } catch (e: Exception) {
                            StringWriter(1024).use { result ->
                                PrintWriter(result).use { pw ->
                                    e.printStackTrace(pw)
                                    notifyTextAvailable(
                                        "\nFailed to attach target jvm: ${jvm.name}\n" + result.toString(),
                                        ProcessOutputTypes.STDERR
                                    )
                                }
                            }
                            notifyProcessTerminated(1)
                        }
                    }
                }
            }
        })
        super.startNotify()
    }

    override fun notifyTextAvailable(text: String, outputType: Key<*>) {
        myAnsiEscapeDecoder.escapeText(text, outputType, this)
    }

    override fun coloredTextAvailable(text: String, attributes: Key<*>) {
        super.notifyTextAvailable(text, attributes)
    }



}