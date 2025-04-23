package io.github.vudsen.arthasui.conf

import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.EditorTextField
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import io.github.vudsen.arthasui.api.extension.HostMachineConnectManager
import io.github.vudsen.arthasui.api.conf.HostMachineConfig
import io.github.vudsen.arthasui.conf.ui.ScriptTestsDialog
import io.github.vudsen.arthasui.api.bean.JvmSearchGroup
import io.github.vudsen.arthasui.language.ognl.psi.OgnlFileType
import io.github.vudsen.arthasui.script.MyOgnlContext
import io.github.vudsen.arthasui.common.util.KMutableProperty2MutablePropertyAdapter;
import java.awt.Dimension
import javax.swing.JComponent

class JvmSearchGroupConfigurable(
    private val project: Project,
    private val hostMachineConfig: HostMachineConfig,
    oldState: JvmSearchGroup? = null,
) : Configurable {

    private val isCreate = oldState == null

    private val state = oldState ?: JvmSearchGroup()

    private var root: DialogPanel? = null

    companion object {
        private val logger = Logger.getInstance(JvmSearchGroupConfigurable::class.java)
    }

    override fun createComponent(): JComponent {
        val ognlTextArea = EditorTextField(project, OgnlFileType).apply {
            font = EditorColorsManager.getInstance()
                .globalScheme
                .getFont(EditorFontType.PLAIN)
        }
        ognlTextArea.setOneLineMode(false)
        ognlTextArea.preferredSize = Dimension(400,  150)

        val root = panel {
            row {
                textField().label("Name").bindText(state::name).enabled(isCreate).align(Align.FILL)
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
        root.preferredSize = Dimension(400, 300)

        this.root = root
        return root
    }

    private fun testScript(script: String) {
        val context = MyOgnlContext(
            service<HostMachineConnectManager>().connect(hostMachineConfig),
            hostMachineConfig
        )
        ScriptTestsDialog(script, context).show()
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
        val newEntity = JvmSearchGroup(state.name, state.script)
        val persistent = service<ArthasUISettingsPersistent>()

        val target = persistent.state.hostMachines.find { config -> config == hostMachineConfig }
        target ?: let {
            throw IllegalStateException("Unreachable code.")
        }

        val newSearchGroup = ArrayList<JvmSearchGroup>(target.searchGroups.size)
        var insert = true
        for (oldEntity in target.searchGroups) {
            if (oldEntity == newEntity) {
                newSearchGroup.add(newEntity)
                insert = false
            } else {
                newSearchGroup.add(oldEntity)
            }
        }
        if (insert) {
            newSearchGroup.add(newEntity)
        }

        target.searchGroups = newSearchGroup
        persistent.notifyStateUpdated()
    }

    override fun getDisplayName(): String {
        return "Jvm Search Group"
    }


}