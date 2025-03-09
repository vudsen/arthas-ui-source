package io.github.vudsen.arthasui.run

import com.intellij.execution.process.*
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import io.github.vudsen.arthasui.api.ArthasBridge
import io.github.vudsen.arthasui.api.ArthasBridgeListener
import io.github.vudsen.arthasui.api.ArthasExecutionManager
import io.github.vudsen.arthasui.api.JVM
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

    private var arthasBridge: ArthasBridge? = null


    override fun destroyProcessImpl() {
        notifyProcessTerminated(arthasBridge?.stop() ?: 1)
    }

    override fun detachProcessImpl() {

    }

    override fun detachIsDefault(): Boolean {
        return false
    }

    override fun getProcessInput(): OutputStream? {
        return null
    }

    override fun startNotify() {
        addProcessListener(object : ProcessListener {

            override fun processTerminated(event: ProcessEvent) {
                // TODO
                super.processTerminated(event)
            }

            override fun startNotified(event: ProcessEvent) {
                ProcessIOExecutorService.INSTANCE.execute {
                    runBlocking {
                        notifyTextAvailable("Trying to attach to target jvm: ${jvm.getDisplayName()}\n", ProcessOutputTypes.STDOUT)
                        val coordinator = project.service<ArthasExecutionManager>()
                        arthasBridge = try {
                            val arthasBridgeTemplate = coordinator.getTemplate(jvm)!!
                            arthasBridgeTemplate.addListener(object : ArthasBridgeListener() {
                                override fun onContent(result: String) {
                                    notifyTextAvailable(result, ProcessOutputTypes.STDOUT)
                                }
                            })
                            arthasBridgeTemplate.attachNow()

                            arthasBridgeTemplate
                        } catch (e: Exception) {
                            StringWriter(1024).use { result ->
                                PrintWriter(result).use { pw ->
                                    e.printStackTrace(pw)
                                    notifyTextAvailable(
                                        "\nFailed to attach target jvm: ${jvm.getDisplayName()}\n" + result.toString(),
                                        ProcessOutputTypes.STDERR
                                    )
                                }
                            }
                            notifyProcessTerminated(1)
                            return@runBlocking
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