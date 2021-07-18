package baaahs.app.ui.editor

import baaahs.control.MutableButtonControl
import baaahs.control.MutableButtonGroupControl
import baaahs.control.MutableVisualizerControl
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShaderInstance
import baaahs.ui.View
import baaahs.ui.renderWrapper
import baaahs.ui.unaryPlus
import materialui.components.divider.divider
import materialui.components.divider.enums.DividerVariant
import react.dom.div

actual fun getEditorPanelViews(): EditorPanelViews = object : EditorPanelViews {
    override fun forGenericPropertiesPanel(
        editableManager: EditableManager,
        propsEditors: List<PropsEditor>
    ): View = renderWrapper {
        propsEditors.forEachIndexed { index, editorPanelComponent ->
            if (index > 0) {
                divider {
                    attrs.variant = DividerVariant.middle
                }
            }

            with(editorPanelComponent.getView(editableManager)) {
                render()
            }
        }
    }

    override fun forPatchHolder(
        editableManager: EditableManager,
        mutablePatchHolder: MutablePatchHolder
    ): View = renderWrapper {
        fixturesList {
            attrs.editableManager = editableManager
            attrs.mutablePatchHolder = mutablePatchHolder
        }
    }

    override fun forPatch(
        editableManager: EditableManager,
        mutablePatch: MutablePatch
    ): View = renderWrapper {
        patchOverview {
            attrs.editableManager = editableManager
            attrs.mutablePatch = mutablePatch
        }
    }

    override fun forShaderInstance(
        editableManager: EditableManager,
        mutablePatch: MutablePatch,
        mutableShaderInstance: MutableShaderInstance
    ): View =
        renderWrapper {
            shaderInstanceEditor {
                attrs.editableManager = editableManager
                attrs.mutablePatch = mutablePatch
                attrs.mutableShaderInstance = mutableShaderInstance
            }
        }

    override fun forButton(
        editableManager: EditableManager,
        mutableButtonControl: MutableButtonControl
    ) = renderWrapper {
        buttonPropsEditor {
            attrs.editableManager = editableManager
            attrs.mutableButtonControl = mutableButtonControl
        }
    }

    override fun forButtonGroup(
        editableManager: EditableManager,
        mutableButtonGroupControl: MutableButtonGroupControl
    ) = renderWrapper {
        buttonGroupPropsEditor {
            attrs.editableManager = editableManager
            attrs.mutableButtonGroupControl = mutableButtonGroupControl
        }
    }

    override fun forVisualizer(
        editableManager: EditableManager,
        mutableVisualizerControl: MutableVisualizerControl
    ) = renderWrapper {
        visualizerPropsEditor {
            attrs.editableManager = editableManager
            attrs.mutableVisualizerControl = mutableVisualizerControl
        }
    }

    override fun forTitleComponent(
        editableManager: EditableManager,
        mutablePatchHolder: MutablePatchHolder
    ): View = renderWrapper {
        div(+EditableStyles.propertiesSection) {
            textFieldEditor {
                attrs.label = "Title"
                attrs.helperText = "Visible on the button"

                attrs.getValue = { mutablePatchHolder.title }
                attrs.setValue = { value -> mutablePatchHolder.title = value }
                attrs.editableManager = editableManager
            }
        }
    }
}