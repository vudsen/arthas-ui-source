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
import org.junit.jupiter.api.*
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.testcontainers.containers.GenericContainer
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
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
            server = BridgeTestUtil.setupContainer("vudsen/ssh-server-with-math-game:0.0.3", rootDisposable) {
                withExposedPorts(22)
            }
        }

        @AfterAll
        @JvmStatic
        fun afterEach() {
            Disposer.dispose(rootDisposable)
        }

    }



    @Test
    fun testBasicAttach() {
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
                createK8sHostMachine()

                button("OK").click()
                val tree = x(
                    xQuery { and(byType("com.intellij.ui.treeStructure.Tree"), byVisibleText("Self || Remote || Kubernetes")) },
                    JTreeUiComponent::class.java
                )
                tree.doubleClickRow(1)
                tree.doubleClickRow(2)
                tree.doubleClickRow(2)
                tree.doubleClickRow(3)
                testArthasRunning()
                tree.doubleClickRow(1)


                // test k8s
                tree.doubleClickRow(2)
                tree.doubleClickRow(3)
                tree.doubleClickRow(3)
                tree.doubleClickRow(4)
                tree.doubleClickRow(4)
                tree.doubleClickRow(5)
                testArthasRunning()
            }
        }
    }

    private fun IdeaFrameUI.testArthasRunning() {
        val editor = x(xQuery { byType("com.intellij.openapi.editor.impl.EditorComponentImpl") })
        editor.setFocus()
        editor.keyboard {
            Thread.sleep(1.seconds.inWholeMilliseconds)
            enterText("sc demo.*")
            hotKey(KeyEvent.VK_CONTROL, KeyEvent.VK_A)
            x(xQuery { byAccessibleName("Execute Command") }).click()
        }
        val outputEditor = x(xQuery {
            and(
                byType("com.intellij.openapi.editor.impl.EditorComponentImpl"),
                byAccessibleName("Editor")
            )
        })

        textAssert(outputEditor, "demo.MathGame")

        editor.setFocus()
        editor.keyboard {
            hotKey(KeyEvent.VK_CONTROL, KeyEvent.VK_A)
            backspace()
            enterText("echo hello")
            hotKey(KeyEvent.VK_CONTROL, KeyEvent.VK_A)
            x(xQuery { byAccessibleName("Execute Command") }).click()
        }
        Thread.sleep(1.seconds.inWholeMilliseconds)
        x(xQuery { byType("com.intellij.openapi.wm.impl.headertoolbar.MainToolbar") }).x(xQuery { byAttribute("myicon", "stop.svg") }).click()
        textAssert(outputEditor, "hello")
        // close the editor
        x(xQuery { byType("com.intellij.ui.tabs.impl.ActionButton\$2") }).click()
        x(xQuery { byAccessibleName("Hide") }).click()
    }

    private fun textAssert(comp:  UiComponent, expected: String) {
        for (spin in 0..10) {
            if (comp.getAllTexts(expected).size == 1) {
                return
            }
            Thread.sleep(1.seconds.inWholeMilliseconds)
        }
        Assertions.fail<Nothing>("No matching text: \"$expected\" found")
    }

    private fun IdeaFrameUI.createK8sHostMachine() {
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
                    enterText("Kubernetes")
                }
            }
            comboBox(xQuery { and(byAccessibleName("Connect type"), byType("com.intellij.openapi.ui.ComboBox")) }).click()
            list().clickItem("Kubernetes", false)

            x(xQuery { and(byAccessibleName("Url"), byType("com.intellij.ui.components.JBTextField")) }).let {
                it.setFocus()
                it.keyboard {
                    enterText(System.getenv("K8S_API_SERVER_URL") ?: "https://127.0.0.1:6443")
                }
            }
            x(xQuery { and(byAccessibleName("Token"), byType("com.intellij.ui.components.JBTextField")) }).let {
                it.setFocus()
                it.keyboard {
                    Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(System.getenv("K8S_TOKEN")), null)
                    hotKey(KeyEvent.VK_CONTROL, KeyEvent.VK_V)
                }
            }
            x(xQuery { byAccessibleName("Validate SSL") }).click()
            comboBox(xQuery { and(byAccessibleName("Transfer local file"), byType("com.intellij.openapi.ui.ComboBox")) }).click()
            list().clickItem("Self")
            button("Next").click()
            button("Create").click()
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

            x(xQuery { and(byAccessibleName("host"), byType("com.intellij.ui.components.JBTextField")) }).let {
                it.setFocus()
                it.keyboard {
                    enterText(server.host)
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
            x(xQuery { and(byAccessibleName("username"), byType("com.intellij.ui.components.JBTextField")) }).let {
                it.setFocus()
                it.keyboard {
                    enterText("root")
                }
            }
            x(xQuery { and(byAccessibleName("password"), byType("com.intellij.ui.components.JBPasswordField")) }).let {
                it.setFocus()
                it.keyboard {
                    enterText("root")
                }
            }
            comboBox(xQuery { and(byAccessibleName("Transfer local file"), byType("com.intellij.openapi.ui.ComboBox")) }).click()
            list().clickItem("Self")
            button("Next").click()
            x( xQuery {byAccessibleName("Enable") }).click()

            x(xQuery { and(byAccessibleName("Java home"), byType("com.intellij.ui.components.JBTextField")) }).let {
                it.setFocus()
                it.keyboard {
                    enterText("/opt/java")
                }
            }
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
                    enterText("Self")
                }
            }
            button("Next").click()
            button("Create").click()
        }
    }

}