package baaahs.app.ui.editor

import baaahs.show.ButtonGroupControl
import baaahs.show.mutable.MutableButtonGroupControl
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShaderInstance
import baaahs.ui.Renderer
import kotlinx.html.js.onChangeFunction
import materialui.components.divider.divider
import materialui.components.divider.enums.DividerVariant
import materialui.components.formcontrol.formControl
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.formlabel.formLabel
import materialui.components.radio.radio
import materialui.components.radiogroup.radioGroup
import org.w3c.dom.HTMLInputElement

actual fun getEditorPanelViews(): EditorPanelViews = object : EditorPanelViews {
    override fun forGenericPropertiesPanel(
        editableManager: EditableManager,
        components: List<EditorPanelComponent>
    ): Renderer = renderWrapper {
        components.forEachIndexed { index, editorPanelComponent ->
            if (index > 0) {
                divider {
                    attrs.variant = DividerVariant.middle
                }
            }

            with(editorPanelComponent.getRenderer(editableManager)) {
                render()
            }
        }
    }

    override fun forPatchHolder(
        editableManager: EditableManager,
        mutablePatchHolder: MutablePatchHolder
    ): Renderer = renderWrapper {
        fixturesList {
            attrs.editableManager = editableManager
            attrs.mutablePatchHolder = mutablePatchHolder
        }
    }

    override fun forPatch(
        editableManager: EditableManager,
        mutablePatch: MutablePatch
    ): Renderer = renderWrapper {
        patchOverview {
            attrs.editableManager = editableManager
            attrs.mutablePatch = mutablePatch
//            attrs.onSelectShaderInstance =
        }
    }

    override fun forShaderInstance(
        editableManager: EditableManager,
        mutablePatch: MutablePatch,
        mutableShaderInstance: MutableShaderInstance
    ): Renderer =
        renderWrapper {
            shaderInstanceEditor {
                attrs.editableManager = editableManager
                attrs.mutablePatch = mutablePatch
                attrs.mutableShaderInstance = mutableShaderInstance
                attrs.shaderChannels = emptySet() // TODO
            }
        }

    override fun forButtonGroup(
        editableManager: EditableManager,
        mutableButtonGroupControl: MutableButtonGroupControl
    ) = renderWrapper {
        formControl {
            formLabel {
                attrs.component = "legend"
                +"Direction"
            }

            radioGroup {
                attrs.value(mutableButtonGroupControl.direction.name)
                attrs.onChangeFunction = {
                    val value = (it.target as HTMLInputElement).value
                    mutableButtonGroupControl.direction = ButtonGroupControl.Direction.valueOf(value)
                    editableManager.onChange()
                }

                formControlLabel {
                    attrs.value = "Horizontal"
                    attrs.control = radio {}
                    attrs.label { +"Horizontal" }
                }
                formControlLabel {
                    attrs.value = "Vertical"
                    attrs.control = radio {}
                    attrs.label { +"Vertical" }
                }
            }
        }
    }

    override fun forTitleComponent(
        editableManager: EditableManager,
        mutablePatchHolder: MutablePatchHolder
    ): Renderer = renderWrapper {
        textFieldEditor {
            attrs.label = "Title"
            attrs.getValue = { mutablePatchHolder.title }
            attrs.setValue = { value -> mutablePatchHolder.title = value }
            attrs.editableManager = editableManager
        }
    }
}