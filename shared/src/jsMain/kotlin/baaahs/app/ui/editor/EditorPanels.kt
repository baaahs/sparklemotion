package baaahs.app.ui.editor

import baaahs.app.ui.model.numberTextField
import baaahs.control.MutableButtonControl
import baaahs.control.MutableButtonGroupControl
import baaahs.control.MutableSliderControl
import baaahs.control.MutableVisualizerControl
import baaahs.model.ModelUnit
import baaahs.scene.MutableScene
import baaahs.show.mutable.*
import baaahs.ui.View
import baaahs.ui.render
import baaahs.ui.renderWrapper
import baaahs.ui.unaryPlus
import mui.material.Divider
import mui.material.DividerVariant
import mui.material.FormControlLabel
import mui.material.Switch
import react.buildElement
import react.dom.div
import react.dom.h2
import web.html.InputType

actual fun getEditorPanelViews(): EditorPanelViews = object : EditorPanelViews {
    override fun forSingleShaderSimplifiedEditorPanel(
        editableManager: EditableManager<*>,
        mutablePatchHolder: MutablePatchHolder
    ): View = renderWrapper {
        val mutablePatch = mutablePatchHolder.patches[0]
        val editorPanel = mutablePatch.getEditorPanel(editableManager)
        editorPanel.getView().render(this)
    }

    override fun forGenericPropertiesPanel(
        editableManager: EditableManager<*>,
        propsEditors: List<PropsEditor>
    ): View = renderWrapper {
        propsEditors.forEachIndexed { index, editorPanelComponent ->
            if (index > 0) {
                Divider {
                    attrs.variant = DividerVariant.middle
                }
            }

            editorPanelComponent.getView(editableManager)
                .render(this)
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
                attrs.helperText = if (mutablePatchHolder is MutableShow) {
                    "Visible in the header"
                } else {
                    "Visible on the button"
                }

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

        div(+EditableStyles.propertiesSection) {
            numberTextField<Float> {
                attrs.label = "Initial Viewing Angle (in radians)"
                attrs.isNullable = true
                attrs.value = mutableScene.model.initialViewingAngle
                attrs.onChange = { value ->
                    mutableScene.model.initialViewingAngle = value
                    editableManager.onChange()
                }
            }
        }
    }

    override fun forGridLayout(
        editableManager: EditableManager<*>,
        editor: Editor<MutableIGridLayout>
    ): View = renderWrapper {
        editor.edit(editableManager.currentMutableDocument as MutableShow) {
            val layout = this

            val canMatchParent = layout is MutableGridLayout
            val matchParent = (layout as? MutableGridLayout)?.matchParent == true

            div(+EditableStyles.propertiesSection) {
                h2 { +"Grid layout!" }

                if (canMatchParent) {
                    FormControlLabel {
//                    attrs.className = -styles.expandSwitchLabel

                        attrs.control = buildElement {
                            Switch {
                                attrs.checked = matchParent
                                attrs.onChange = { _, checked ->
                                    (layout as MutableGridLayout).matchParent = checked
                                    editableManager.onChange()
                                }
                            }
                        }
                        attrs.label = buildElement { +"Match Parent" }
                    }
                }

                div {
                    textFieldEditor {
                        attrs.type = InputType.number
                        attrs.label = "Columns"
                        attrs.disabled = matchParent
                        attrs.getValue = { layout.columns.toString() }
                        attrs.setValue = { newValue -> layout.columns = newValue.toInt() }
                        attrs.onChange = { _ -> editableManager.onChange() }
                    }

                    textFieldEditor {
                        attrs.type = InputType.number
                        attrs.label = "Rows"
                        attrs.disabled = matchParent
                        attrs.getValue = { layout.rows.toString() }
                        attrs.setValue = { newValue -> layout.rows = newValue.toInt() }
                        attrs.onChange = { _ -> editableManager.onChange() }
                    }
                }
            }
        }
    }
}