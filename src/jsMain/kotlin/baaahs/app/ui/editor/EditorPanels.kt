package baaahs.app.ui.editor

import baaahs.show.ButtonGroupControl
import baaahs.show.mutable.*
import baaahs.ui.Renderer
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrol.formControl
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.formlabel.formLabel
import materialui.components.radio.radio
import materialui.components.radiogroup.radioGroup
import org.w3c.dom.HTMLInputElement
import react.dom.div

actual fun getEditorPanelViews(): EditorPanelViews = object : EditorPanelViews {
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

    override fun forShow(
        editableManager: EditableManager,
        mutableShow: MutableShow
    ): Renderer = renderWrapper {
        div {}
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
}