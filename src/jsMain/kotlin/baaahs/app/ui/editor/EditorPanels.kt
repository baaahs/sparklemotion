package baaahs.app.ui.editor

import baaahs.control.MutableButtonControl
import baaahs.control.MutableButtonGroupControl
import baaahs.control.MutableSliderControl
import baaahs.control.MutableVisualizerControl
import baaahs.model.ModelUnit
import baaahs.scene.MutableScene
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.ui.View
import baaahs.ui.renderWrapper
import baaahs.ui.unaryPlus
import materialui.components.divider.divider
import materialui.components.divider.enums.DividerVariant
import react.dom.div

actual fun getEditorPanelViews(): EditorPanelViews = object : EditorPanelViews {
    override fun forGenericPropertiesPanel(
        editableManager: EditableManager<*>,
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
        editableManager: EditableManager<*>,
        mutablePatchHolder: MutablePatchHolder
    ): View = renderWrapper {
        patchesOverview {
            attrs.editableManager = editableManager
            attrs.mutablePatchHolder = mutablePatchHolder
        }
    }

    override fun forPatch(
        editableManager: EditableManager<*>,
        mutablePatch: MutablePatch
    ): View =
        renderWrapper {
            patchEditor {
                attrs.editableManager = editableManager
                attrs.mutablePatch = mutablePatch
            }
        }

    override fun forButton(
        editableManager: EditableManager<*>,
        mutableButtonControl: MutableButtonControl
    ) = renderWrapper {
        buttonPropsEditor {
            attrs.editableManager = editableManager
            attrs.mutableButtonControl = mutableButtonControl
        }
    }

    override fun forButtonGroup(
        editableManager: EditableManager<*>,
        mutableButtonGroupControl: MutableButtonGroupControl
    ) = renderWrapper {
        buttonGroupPropsEditor {
            attrs.editableManager = editableManager
            attrs.mutableButtonGroupControl = mutableButtonGroupControl
        }
    }

    override fun forSlider(
        editableManager: EditableManager<*>,
        mutableSliderControl: MutableSliderControl
    ) = renderWrapper {
        sliderPropsEditor {
            attrs.editableManager = editableManager
            attrs.mutableSliderControl = mutableSliderControl
        }
    }

    override fun forVisualizer(
        editableManager: EditableManager<*>,
        mutableVisualizerControl: MutableVisualizerControl
    ) = renderWrapper {
        visualizerPropsEditor {
            attrs.editableManager = editableManager
            attrs.mutableVisualizerControl = mutableVisualizerControl
        }
    }

    override fun forTitleComponent(
        editableManager: EditableManager<*>,
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

    override fun forSceneTitleComponent(
        editableManager: EditableManager<*>,
        mutableScene: MutableScene
    ): View = renderWrapper {
        div(+EditableStyles.propertiesSection) {
            textFieldEditor {
                attrs.label = "Title"

                attrs.getValue = { mutableScene.title }
                attrs.setValue = { value -> mutableScene.title = value }
                attrs.editableManager = editableManager
            }
        }
    }

    override fun forModelUnitsComponent(
        editableManager: EditableManager<*>,
        mutableScene: MutableScene
    ): View = renderWrapper {
        div(+EditableStyles.propertiesSection) {
            betterSelect<ModelUnit> {
                attrs.label = "Unit"
                attrs.values = ModelUnit.values().toList()
                attrs.value = mutableScene.model.units
                attrs.onChange = { value ->
                    mutableScene.model.units = value
                    editableManager.onChange()
                }
            }
        }
    }
}