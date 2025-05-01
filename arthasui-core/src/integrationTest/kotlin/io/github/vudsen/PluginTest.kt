package io.github.vudsen

import com.intellij.driver.sdk.waitForIndicators
import com.intellij.ide.starter.ci.CIServer
import com.intellij.ide.starter.ci.NoCIServer
import com.intellij.ide.starter.di.di
import com.intellij.ide.starter.driver.engine.runIdeWithDriver
import com.intellij.ide.starter.ide.IdeProductProvider
import com.intellij.ide.starter.models.TestCase
import com.intellij.ide.starter.plugins.PluginConfigurator
import com.intellij.ide.starter.project.NoProject
import com.intellij.ide.starter.runner.Starter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import java.io.File
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

    @Test
    fun testWithoutProject() {
        Starter.newContext(
             "testExample",
            TestCase(IdeProductProvider.IC, NoProject)
                .withVersion(System.getProperty("platformVersion"))
        ).apply {
            val pathToPlugin = System.getProperty("path.to.build.plugin")
            PluginConfigurator(this).installPluginFromFolder(File(pathToPlugin))
            this.pluginConfigurator.assertPluginIsInstalled("io.github.vudsen.arthas-ui")

        }.runIdeWithDriver().useDriverAndCloseIde {
            waitForIndicators(30.seconds)
        }
    }

}