package baaahs.app.ui.editor

import baaahs.show.mutable.*
import baaahs.ui.Renderer
import baaahs.ui.unaryPlus
import materialui.components.divider.divider
import materialui.components.divider.enums.DividerVariant
import react.dom.div

actual fun getEditorPanelViews(): EditorPanelViews = object : EditorPanelViews {
    override fun forGenericPropertiesPanel(
        editableManager: EditableManager,
        propsEditors: List<PropsEditor>
    ): Renderer = renderWrapper {
        propsEditors.forEachIndexed { index, editorPanelComponent ->
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
    ): Renderer = renderWrapper {
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