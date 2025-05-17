package io.github.vudsen

import com.intellij.driver.sdk.ui.components.*
import com.intellij.driver.sdk.ui.xQuery
import com.intellij.driver.sdk.waitForIndicators
import com.intellij.ide.starter.ci.CIServer
import com.intellij.ide.starter.ci.NoCIServer
import com.intellij.ide.starter.di.di
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.plugins.PluginConfigurator
import com.intellij.ide.starter.project.LocalProjectInfo
import com.intellij.ide.starter.runner.Starter
import com.intellij.openapi.Disposable
import com.intellij.openapi.util.Disposer
import io.github.vudsen.test.BridgeTestUtil
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.testcontainers.containers.GenericContainer
import java.awt.event.KeyEvent
import java.io.File
import kotlin.io.path.Path
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class PluginTest  {



    init {
        di = DI {
            extend(di)
            bindSingleton<CIServer>(overrides = true) {
                object : CIServer by NoCIServer {
                    override fun reportTestFailure(
                        testName: String,
                        message: String,
                        details: String,
                        linkToLogs: String?
                    ) {
                        fail { "$testName fails: $message. \n$details" }
                    }
                }
            }
        }
    }


    companion object {

        lateinit var server: GenericContainer<*>

        val rootDisposable: Disposable = Disposable { }

        @BeforeAll
        @JvmStatic
        fun beforeEach() {
            server = BridgeTestUtil.setupContainer("vudsen/ssh-server-with-math-game:0.0.3", rootDisposable, null)
        }

        @AfterAll
        @JvmStatic
        fun afterEach() {
            Disposer.dispose(rootDisposable)
        }

    }



    @Test
    fun testWithoutProject() {
        Starter.newContext(
             "testExample",
            TestCase(IdeProductProvider.IC, LocalProjectInfo(Path("src/integrationTest/resources/test-projects/simple-project")))
                .withVersion(System.getProperty("platformVersion"))
        ).apply {
            val pathToPlugin = System.getProperty("path.to.build.plugin")
            PluginConfigurator(this).installPluginFromFolder(File(pathToPlugin))
            this.pluginConfigurator.assertPluginIsInstalled("io.github.vudsen.arthas-ui")

        }.runIdeWithDriver().useDriverAndCloseIde {
            waitForIndicators(5.minutes)

            ideFrame {
                x(xQuery { byAccessibleName("ArthasUI") }).click()
                // The toolwindow add icon.
                x(xQuery { byAccessibleName("Add") }).click()

                createLocalHostMachine()
                button("Apply").click()
                createSshHostMachine()

                Thread.sleep(30.minutes.inWholeMilliseconds)

            }
        }
    }


    private fun IdeaFrameUI.createSshHostMachine() {
        dialog {
            x(xQuery { byAccessibleName("Add") }).click()
        }

        dialog(title = "New Host Machine") {
            // Create a Local Host Machine.
            textField(
                xQuery{
                    and(byAccessibleName("Name"), byType("com.intellij.ui.components.JBTextField"))
                }
            ).apply {
                this.setFocus()
                keyboard {
                    enterText("Remote")
                }
            }
            comboBox(xQuery { and(byAccessibleName("Connect type"), byType("com.intellij.openapi.ui.ComboBox")) }).click()
            list().clickItem("SSH", false)

            Thread.sleep(1.seconds.inWholeMilliseconds)
            x(xQuery { and(byAccessibleName("host"), byType("com.intellij.ui.components.JBTextField")) }).let {
                it.setFocus()
                it.keyboard {
                    enterText(server.host)
                }
            }
            x(xQuery { and(byAccessibleName("username"), byType("com.intellij.ui.components.JBTextField")) }).let {
                it.setFocus()
                it.keyboard {
                    enterText("root")
                }
            }
            x(xQuery { and(byAccessibleName("port"), byType("com.intellij.ui.components.JBTextField")) }).let {
                it.setFocus()
                it.keyboard {
                    // ctrl + A
                    hotKey(KeyEvent.VK_CONTROL, KeyEvent.VK_A)
                    backspace()
                    enterText(server.firstMappedPort.toString())
                }
            }
            x(xQuery { and(byAccessibleName("password"), byType("com.intellij.ui.components.JBPasswordField")) }).let {
                it.setFocus()
                it.keyboard {
                    enterText("root")
                }
            }
            comboBox(xQuery { and(byAccessibleName("Transfer local file"), byType("com.intellij.openapi.ui.ComboBox")) }).click()
            list().clickItem("Local")
            button("Next").click()
            Thread.sleep(30.minutes.inWholeMilliseconds)
            // Finish the create.
            button("Create").click()
        }

    }


    private fun IdeaFrameUI.createLocalHostMachine() {
        dialog {
            x(xQuery { byAccessibleName("Add") }).click()
        }

        dialog(title = "New Host Machine") {
            // Create a Local Host Machine.
            textField(
                xQuery {
                    and(byAccessibleName("Name"), byType("com.intellij.ui.components.JBTextField"))
                }
            ).apply {
                this.setFocus()
                keyboard {
                    enterText("Local")
                }
            }
            button("Next").click()
            button("Create").click()
        }
    }

}