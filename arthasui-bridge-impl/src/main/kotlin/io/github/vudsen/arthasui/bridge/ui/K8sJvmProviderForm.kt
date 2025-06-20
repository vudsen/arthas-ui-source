package io.github.vudsen.arthasui.bridge.ui

import com.intellij.icons.AllIcons
import com.intellij.ide.HelpTooltip
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.EditorTextField
import com.intellij.ui.TextAccessor
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComboBoxPredicate
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.bridge.conf.K8sJvmProviderConfig
import io.github.vudsen.arthasui.common.ui.CheckBoxPredicate
import io.github.vudsen.arthasui.common.util.KMutableProperty2MutablePropertyAdapter
import org.jetbrains.yaml.YAMLFileType
import java.awt.Component
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.ListCellRenderer

class K8sJvmProviderForm(
    oldState: JvmProviderConfig?,
    parentDisposable: Disposable
) : AbstractFormComponent<JvmProviderConfig>(parentDisposable) {

    private val state: K8sJvmProviderConfig = if (oldState is K8sJvmProviderConfig) {
        oldState.deepCopy()
    } else {
        K8sJvmProviderConfig()
    }

    private val tokenAuthorization = state.token ?: K8sJvmProviderConfig.TokenAuthorization()

    private var myAuthorizationType: K8sJvmProviderConfig.AuthorizationType? = state.authorizationType

    override fun getState(): JvmProviderConfig {
        state.token = tokenAuthorization
        state.authorizationType = myAuthorizationType ?: K8sJvmProviderConfig.AuthorizationType.BUILTIN
        return state
    }


    private fun createKubeConfigTextField(): EditorTextField {
        val textField = EditorTextField()

        textField.font = EditorColorsManager.getInstance()
            .globalScheme
            .getFont(EditorFontType.PLAIN)
        textField.fileType = YAMLFileType.YML
        textField.preferredSize = Dimension(textField.preferredSize.width, 280)
        textField.setOneLineMode(false)
        textField.setPlaceholder("Input Kubeconfig content here.")
        textField.autoscrolls = true
        textField.setDisposedWith(parentDisposable)
        val editorEx = textField.getEditor(true) as EditorEx
        editorEx.setHorizontalScrollbarVisible(true)
        editorEx.setVerticalScrollbarVisible(true)
        return textField
    }


    class TextAccessorGetter : (TextAccessor) -> String? {

        override fun invoke(p1: TextAccessor): String? {
            return p1.text
        }
    }

    class TextAccessorSetter : (TextAccessor, String?) -> Unit {
        override fun invoke(p1: TextAccessor, p2: String?) {
            p1.text = p2
        }

    }

    override fun createDialogPanel(): DialogPanel {
        val panel = panel {
            lateinit var predicate: CheckBoxPredicate
            row {
                val selected = checkBox("Enable").bindSelected(state::enabled)
                predicate = CheckBoxPredicate(selected, state.enabled)
            }
            lateinit var comboBox: ComboBox<K8sJvmProviderConfig.AuthorizationType>

            panel {
                row {
                    label("Authorization type")
                    val box = comboBox(
                        K8sJvmProviderConfig.AuthorizationType.entries,
                        object : ListCellRenderer<K8sJvmProviderConfig.AuthorizationType?> {
                            override fun getListCellRendererComponent(
                                list: JList<out K8sJvmProviderConfig.AuthorizationType?>?,
                                value: K8sJvmProviderConfig.AuthorizationType?,
                                index: Int,
                                isSelected: Boolean,
                                cellHasFocus: Boolean
                            ): Component {
                                return JLabel(value?.displayName)
                            }
                        }).bindItem(this@K8sJvmProviderForm::myAuthorizationType)
                    comboBox = box.component
                }
                row {
                    cell(createKubeConfigTextField())
                        .bind(
                            TextAccessorGetter(),
                            TextAccessorSetter(),
                            KMutableProperty2MutablePropertyAdapter(state::kubeConfig)
                        ).align(Align.FILL)
                }.visibleIf(ComboBoxPredicate(comboBox, { v -> v == K8sJvmProviderConfig.AuthorizationType.KUBE_CONFIG }))
                group("Connect Configuration") {
                    row {
                        textField().label("Kubeconfig path").bind(
                            TextAccessorGetter(),
                            TextAccessorSetter(),
                            KMutableProperty2MutablePropertyAdapter(state::kubeConfigFilePath)
                        ).align(Align.FILL)
                    }
                }.visibleIf(
                    ComboBoxPredicate(
                        comboBox,
                        { v -> v == K8sJvmProviderConfig.AuthorizationType.KUBE_CONFIG_FILE })
                )
                group("Connect Configuration") {
                    row {
                        textField().label("Url").bindText(tokenAuthorization::url).align(Align.FILL)
                    }
                    row {
                        textField().label("Token").bindText(tokenAuthorization::token).align(Align.FILL)
                    }
                }.visibleIf(ComboBoxPredicate(comboBox, { v -> v == K8sJvmProviderConfig.AuthorizationType.TOKEN }))

                group("Options") {
                    row {
                        checkBox("Validate SSL").bindSelected(state::validateSSL).align(Align.FILL)
                    }
                    row {
                        label("Data directory").gap(RightGap.SMALL)
                        icon(AllIcons.General.ContextHelp).applyToComponent {
                            HelpTooltip().setDescription("The data directory in pod").installOn(this)
                        }.gap(RightGap.SMALL)
                        textField().bindText(state::dataDirectory).align(Align.FILL)
                    }
                    row {
                        label("Kubectl version").gap(RightGap.SMALL)
                        icon(AllIcons.General.ContextHelp).applyToComponent {
                            HelpTooltip().setDescription("The version of kubectl, empty means latest").installOn(this)
                        }.gap(RightGap.SMALL)
                        textField().bindText(state::kubectlVersion).align(Align.FILL)
                    }.visibleIf(ComboBoxPredicate(comboBox, { v -> v != K8sJvmProviderConfig.AuthorizationType.BUILTIN }))
                }
            }.visibleIf(predicate)


        }
        panel.preferredSize =  Dimension(panel.preferredSize.width, 500)
        return panel
    }
}