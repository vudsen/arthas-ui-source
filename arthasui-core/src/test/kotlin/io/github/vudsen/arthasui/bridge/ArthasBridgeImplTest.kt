package io.github.vudsen.arthasui.bridge

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.github.vudsen.arthasui.BridgeTestUtil
import io.github.vudsen.arthasui.TestProgressIndicator
import io.github.vudsen.arthasui.api.ArthasBridgeListener
import io.github.vudsen.arthasui.api.ArthasExecutionManager
import io.github.vudsen.arthasui.api.ArthasResultItem
import io.github.vudsen.arthasui.api.HostMachine
import io.github.vudsen.arthasui.api.JVM
import io.github.vudsen.arthasui.api.conf.ArthasUISettings
import io.github.vudsen.arthasui.api.conf.ArthasUISettingsPersistent
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.extension.JvmProviderManager
import io.github.vudsen.arthasui.bridge.conf.K8sConnectConfig
import io.github.vudsen.arthasui.bridge.conf.K8sJvmProviderConfig
import io.github.vudsen.arthasui.bridge.conf.LocalJvmProviderConfig
import io.github.vudsen.arthasui.bridge.host.K8sHostMachine
import io.github.vudsen.arthasui.core.ArthasExecutionManagerImpl
import org.junit.Assert

/**
 * Test attach.
 *
 * For chinese developer, you can add environment variable `TOOLCHAIN_MIRROR=https://5j9g3t.site/github-mirror`
 * to avoid download failed from GitHub.
 */
class ArthasBridgeImplTest : BasePlatformTestCase() {


    fun testAttachLinuxLocal() {
        val template = BridgeTestUtil.createMathGameSshMachine(testRootDisposable)
        val localJvmProviderConfig = template.getHostMachineConfig().providers.find { config -> config is LocalJvmProviderConfig }!!
        // Local
        val provider = service<JvmProviderManager>().getProvider(localJvmProviderConfig)
        val jvm = provider.searchJvm(template, localJvmProviderConfig).result!!.find { jvm -> jvm.name.contains("math-game.jar")}!!


        testBridge(jvm, template, localJvmProviderConfig)
    }

    private fun testBridge(
        jvm: JVM,
        template: HostMachine,
        providerConfig: JvmProviderConfig
    ) {
        val executionManager = project.getService(ArthasExecutionManager::class.java) as ArthasExecutionManagerImpl
        val builder = StringBuilder()
        val executedCommand = mutableListOf<String>()
        val executeResult = mutableListOf<String>()
        try {
            val arthasBridge = executionManager.initTemplate(
                jvm,
                template.getHostMachineConfig(),
                providerConfig,
                TestProgressIndicator()
            )
            arthasBridge.addListener(object : ArthasBridgeListener() {
                override fun onContent(result: String) {
                    builder.append(result)
                }

                override fun onFinish(command: String, result: ArthasResultItem, rawContent: String) {
                    executedCommand.add(command)
                    executeResult.add(rawContent)
                }

                override fun onClose() {
                    println("closed")
                }
            })
            arthasBridge.attachNow()
            arthasBridge.waitUntilAttached()

            executionManager.getTemplate(jvm)!!.let {
                it.execute("sc demo.*")
                it.execute("echo hello")
                it.stop()
            }
            Assert.assertEquals(mutableListOf("sc demo.*", "echo hello"), executedCommand)
            Assert.assertTrue(executeResult[0].startsWith("demo.MathGame\nAffect(row-cnt:1)"))
            Assert.assertTrue(executeResult[1].startsWith("hello"))
        } catch (e: Exception) {
            if (System.getenv("RUNNER_DEBUG") == "1") {
                println(builder.toString())
            }
            throw e;
        }
    }

    /**
     * Test attach to the jvm in kubernetes pod.
     *
     * You have to set up a kubernetes cluster in your local machine before run this test. After that, apply the `k8s-ci-setup.yaml` file in the resources folder.
     */
    fun testKubernetes() {
        val localHostMachine = BridgeTestUtil.createLocalHostMachine()

        // maybe `https://127.0.0.1:6443`
        val apiServerUrl = System.getenv("K8S_API_SERVER_URL")
        // `kubectl create token arthas-ui-ci -n arthas-ui-test`
        val token = System.getenv("K8S_TOKEN")
        val providerConfig = K8sJvmProviderConfig(true)
        val k8sHostMachineConfig = HostMachineConfig(
            -1,
            "Kubernetes",
            K8sConnectConfig(
                token = K8sConnectConfig.TokenAuthorization(token, apiServerUrl),
                authorizationType = K8sConnectConfig.AuthorizationType.TOKEN,
                validateSSL = false,
                localPkgSourceId = localHostMachine.getHostMachineConfig().id
            ),
            mutableListOf(providerConfig),
            mutableListOf(),
            "/opt/arthas-ui"
        )
        val hostMachine = service<HostMachineConnectManager>().connect(k8sHostMachineConfig)


        val jvmProvider = service<JvmProviderManager>().getProvider(providerConfig)
        val result = jvmProvider.searchJvm(hostMachine, providerConfig)
        val namespace = result.childs!!.find { ns -> ns.getName() == "arthas-ui-test" }
        val mathGame = namespace!!.load().result!!.find { pod -> pod.name.contains("math-game") }!!

        val persistent = service<ArthasUISettingsPersistent>()
        val old = persistent.state
        try {
            persistent.loadState(
                ArthasUISettings(
                    mutableListOf(
                        k8sHostMachineConfig,
                        localHostMachine.getHostMachineConfig()
                    )
                )
            )
            testBridge(mathGame, hostMachine, providerConfig)
        } finally {
            persistent.loadState(old)
        }
    }
}