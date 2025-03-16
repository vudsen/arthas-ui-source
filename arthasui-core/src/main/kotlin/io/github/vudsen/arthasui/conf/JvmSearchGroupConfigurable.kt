package io.github.vudsen.arthasui.conf

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.EditorTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.util.preferredHeight
import io.github.vudsen.arthasui.api.HostMachineFactory
import io.github.vudsen.arthasui.common.ui.SimpleDialog
import io.github.vudsen.arthasui.conf.bean.JvmSearchGroup
import io.github.vudsen.arthasui.language.ognl.psi.OgnlFileType
import io.github.vudsen.arthasui.script.OgnlJvmSearcher
import io.github.vudsen.arthasui.script.MyOgnlContext
import io.github.vudsen.arthasui.util.ui.KMutableProperty2MutablePropertyAdapter;
import javax.swing.JComponent

class JvmSearchGroupConfigurable(
    private val project: Project,
    private val hostMachineConfigV2: HostMachineConfigV2 = HostMachineConfigV2(),
) : Configurable {


    private val state = State()

    private var root: DialogPanel? = null

    companion object {
        private val logger = Logger.getInstance(JvmSearchGroupConfigurable::class.java)
        data class State(
            var name: String = "",
            var script: String = ""
        )
    }

    override fun createComponent(): JComponent {
        val ognlTextArea = EditorTextField(project, OgnlFileType)
        ognlTextArea.setOneLineMode(false)
        ognlTextArea.preferredHeight = 150

        val root = panel {
            row {
                textField().label("Name").bindText(state::name)
            }
            group("Ognl Script") {
                row {
                    cell(ognlTextArea).bind({ area: EditorTextField ->
                        return@bind area.text
                    }, { area: EditorTextField, value: String ->
                        area.text = value
                    }, KMutableProperty2MutablePropertyAdapter(state::script)).align(Align.FILL)
                }
            }
            row {
                button("Test Script") {
                    testScript(ognlTextArea.text)
                }
            }
        }

        this.root = root
        return root
    }

    private fun testScript(script: String) {
        val context = MyOgnlContext(
            service<HostMachineFactory>().getHostMachine(hostMachineConfigV2.connect),
            hostMachineConfigV2
        )
        try {
            OgnlJvmSearcher.execute(
                script,
                context
            )
            val resultHolder = context.getResultHolder()
            SimpleDialog("Script execute success", "Searched jvms: ${resultHolder.result}\nDebug message:\n${resultHolder.collectDebugMessages()}").show()
        } catch (e: Exception) {
            SimpleDialog("Script execute failed", "${e.message ?: e.toString()}, debugMessage:\n${context.getResultHolder().collectDebugMessages()}").show()
            if (logger.isDebugEnabled) {
                logger.debug("Failed to execute script", e)
            }
        }
    }

    override fun isModified(): Boolean {
        return root?.isModified() ?: false
    }

    override fun apply() {
        val root = root ?: return
        root.apply()
        if (root.validateAll().isNotEmpty()) {
            return
        }
        val entity = JvmSearchGroup(state.name, hostMachineConfigV2.name, state.script)
        val persistent = project.getService(ArthasUISettingsPersistent::class.java)

        val target = persistent.state.hostMachines.find { config -> config == hostMachineConfigV2 }
        target ?: let {
            throw IllegalStateException("Unreachable code.")
        }

        val newSearchGroup = ArrayList<JvmSearchGroup>(target.searchGroups.size)
        var newGroup = true
        for (searchGroup in target.searchGroups) {
            if (searchGroup == entity) {
                newSearchGroup.add(entity)
                newGroup = false
            } else {
                newSearchGroup.add(searchGroup)
            }
        }
        if (newGroup) {
            newSearchGroup.add(entity)
        }

        target.searchGroups = newSearchGroup
    }

    override fun getDisplayName(): String {
        return "Jvm Search Group"
    }


}