package io.github.vudsen.arthasui.bridge.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.observable.properties.ObservableMutableProperty
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.EditorTextField
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.TextAccessor
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.ComponentPredicate
import io.github.vudsen.arthasui.api.conf.HostMachineConnectConfig
import io.github.vudsen.arthasui.api.ui.AbstractFormComponent
import io.github.vudsen.arthasui.bridge.conf.K8sConnectConfig
import io.github.vudsen.arthasui.common.util.KMutableProperty2MutablePropertyAdapter
import org.jetbrains.yaml.YAMLFileType
import java.awt.Dimension

class K8sConnectForm(
    oldState: HostMachineConnectConfig?,
    parentDisposable: Disposable
) : AbstractFormComponent<HostMachineConnectConfig>(parentDisposable) {

    private val state: K8sConnectConfig = if (oldState is K8sConnectConfig) {
        oldState.deepCopy()
    } else {
        K8sConnectConfig()
    }

    private val tokenAuthorization = state.token ?: K8sConnectConfig.TokenAuthorization()


    private val segmentButtonBind = SegmentButtonBind(state)

    override fun getState(): K8sConnectConfig {
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
        val editorEx = textField.getEditor(true) as EditorEx
        editorEx.setHorizontalScrollbarVisible(true)
        editorEx.setVerticalScrollbarVisible(true)
        return textField
    }


    private fun createFileChooser(): TextFieldWithBrowseButton {
        val fileChooser = TextFieldWithBrowseButton()
        val fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileDescriptor()
        fileChooser.addBrowseFolderListener(
            null,
            fileChooserDescriptor
        )
        return fileChooser
    }

    class SegmentButtonBind(private val state: K8sConnectConfig) : ObservableMutableProperty<K8sConnectConfig.AuthorizationType> {

        val listeners: MutableList<(K8sConnectConfig.AuthorizationType) -> Unit> = mutableListOf()

        override fun set(value: K8sConnectConfig.AuthorizationType) {
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
            listener: (K8sConnectConfig.AuthorizationType) -> Unit
        ) {
            listeners.add(listener)
        }

        override fun get(): K8sConnectConfig.AuthorizationType {
            return state.authorizationType
        }

    }

    inner class SegmentButtonPredicate(
        private val expected: K8sConnectConfig.AuthorizationType
    ) : ComponentPredicate() {
        override fun addListener(listener: (Boolean) -> Unit) {
            segmentButtonBind.listeners.add { t -> listener(t == expected) }
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
            row {
                label("Authorization type")
                segmentedButton(K8sConnectConfig.AuthorizationType.entries) { r -> text = r.displayName }.bind(segmentButtonBind)
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
            }.visibleIf(SegmentButtonPredicate(K8sConnectConfig.AuthorizationType.KUBE_CONFIG))
            group("Connect Configuration") {
                row("Kubeconfig File") {
                    cell(createFileChooser())
                        .bind(
                            TextAccessorGetter(),
                            TextAccessorSetter(),
                            KMutableProperty2MutablePropertyAdapter(state::kubeConfigFilePath)
                        ).align(Align.FILL)
                }
            }.visibleIf(SegmentButtonPredicate(K8sConnectConfig.AuthorizationType.KUBE_CONFIG_FILE))
            group("Connect Configuration") {
                row {
                    textField().label("Url").bindText(tokenAuthorization::url).align(Align.FILL)
                }
                row {
                    textField().label("Token").bindText(tokenAuthorization::token).align(Align.FILL)
                }
            }.visibleIf(SegmentButtonPredicate(K8sConnectConfig.AuthorizationType.TOKEN))
        }
    }

}