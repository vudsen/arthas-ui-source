package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextBrowseFolderListener
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.EditorTextField
import com.intellij.ui.TextAccessor
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ComponentPredicate
import io.github.vudsen.arthasui.api.conf.JvmProviderConfig
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.bridge.conf.K8sJvmProviderConfig
import io.github.vudsen.arthasui.common.ui.CheckBoxPredicate
import io.github.vudsen.arthasui.common.util.KMutableProperty2MutablePropertyAdapter
import org.jetbrains.yaml.YAMLFileType
import java.awt.Component
import java.awt.Dimension
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


    private val comboBox = MyComboBind(state)

    override fun getState(): JvmProviderConfig {
        state.token = tokenAuthorization
        return state
    }


    private fun createKubeConfigTextField(): EditorTextField {
        val textField  = EditorTextField()

        textField.font = EditorColorsManager.getInstance()
            .globalScheme
            .getFont(EditorFontType.PLAIN)
        textField.fileType = YAMLFileType.YML
        textField.preferredSize = Dimension(textField.preferredSize.width, 280)
        textField.setOneLineMode(false)
        textField.setPlaceholder("Input Kubeconfig content here.")
        textField.autoscrolls = true
        textField.setDisposedWith(parentDisposable)
        ApplicationManager.getApplication().invokeLater {
            val editorEx = textField.getEditor(true) as EditorEx
            editorEx.setHorizontalScrollbarVisible(true)
            editorEx.setVerticalScrollbarVisible(true)
        }
        return textField
    }


    private fun createFileChooser(): TextFieldWithBrowseButton {
        val fileChooser = TextFieldWithBrowseButton()
        val fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
        fileChooser.addBrowseFolderListener(TextBrowseFolderListener(fileChooserDescriptor))
        return fileChooser
    }

    class MyComboBind(private val state: K8sJvmProviderConfig) :
        ObservableMutableProperty<K8sJvmProviderConfig.AuthorizationType> {

        val listeners: MutableList<(K8sJvmProviderConfig.AuthorizationType) -> Unit> = mutableListOf()

        override fun set(value: K8sJvmProviderConfig.AuthorizationType) {
            if (value == state.authorizationType) {
                return
            }
            state.authorizationType = value
            for (function in listeners) {
                function(value)
            }
        }

        override fun afterChange(
            parentDisposable: Disposable?,
            listener: (K8sJvmProviderConfig.AuthorizationType) -> Unit
        ) {
            listeners.add(listener)
        }

        override fun get(): K8sJvmProviderConfig.AuthorizationType {
            return state.authorizationType
        }

    }

    inner class SegmentButtonPredicate(
        private val expected: K8sJvmProviderConfig.AuthorizationType
    ) : ComponentPredicate() {
        override fun addListener(listener: (Boolean) -> Unit) {
            comboBox.listeners.add { t -> listener(t == expected) }
        }

        override fun invoke(): Boolean {
            return state.authorizationType == expected
        }

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
        return panel {
            lateinit var predicate: CheckBoxPredicate
            row {
                val selected = checkBox("Enable").bindSelected(state::enabled)
                predicate = CheckBoxPredicate(selected, state.enabled)
            }

            panel {
                row {
                    label("Authorization type")
                    comboBox(K8sJvmProviderConfig.AuthorizationType.entries, object :ListCellRenderer<K8sJvmProviderConfig.AuthorizationType?> {
                        override fun getListCellRendererComponent(
                            list: JList<out K8sJvmProviderConfig.AuthorizationType?>?,
                            value: K8sJvmProviderConfig.AuthorizationType?,
                            index: Int,
                            isSelected: Boolean,
                            cellHasFocus: Boolean
                        ): Component {
                            return JLabel(value?.displayName)
                        }

                    }).bindItem(comboBox)
                }
                group("Connect Configuration") {
                    row {
                        cell(createKubeConfigTextField())
                            .bind(
                                TextAccessorGetter(),
                                TextAccessorSetter(),
                                KMutableProperty2MutablePropertyAdapter(state::kubeConfig)
                            ).align(Align.FILL)
                    }
                }.visibleIf(SegmentButtonPredicate(K8sJvmProviderConfig.AuthorizationType.KUBE_CONFIG))
                group("Connect Configuration") {
                    row("Kubeconfig File") {
                        cell(createFileChooser())
                            .bind(
                                TextAccessorGetter(),
                                TextAccessorSetter(),
                                KMutableProperty2MutablePropertyAdapter(state::kubeConfigFilePath)
                            ).align(Align.FILL)
                    }
                }.visibleIf(SegmentButtonPredicate(K8sJvmProviderConfig.AuthorizationType.KUBE_CONFIG_FILE))
                group("Connect Configuration") {
                    row {
                        textField().label("Url").bindText(tokenAuthorization::url).align(Align.FILL)
                    }
                    row {
                        textField().label("Token").bindText(tokenAuthorization::token).align(Align.FILL)
                    }
                }.visibleIf(SegmentButtonPredicate(K8sJvmProviderConfig.AuthorizationType.TOKEN))

                group("Feature") {
                    row {
                        checkBox("Validate SSL").bindSelected(state::validateSSL).align(Align.FILL)
                    }
                }
            }.visibleIf(predicate)
        }
    }
}