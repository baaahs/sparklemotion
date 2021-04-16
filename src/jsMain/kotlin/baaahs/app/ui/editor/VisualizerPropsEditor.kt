package baaahs.app.ui.editor

import baaahs.control.MutableVisualizerControl
import baaahs.control.VisualizerControl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrol.formControl
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.formlabel.formLabel
import materialui.components.radio.radio
import materialui.components.radiogroup.radioGroup
import materialui.components.switches.switch
import materialui.components.typography.typographyH6
import org.w3c.dom.HTMLInputElement
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.div

private val visualizerPropsEditor = xComponent<VisualizerPropsEditorProps>("VisualizerPropsEditor") { props ->

    div(+EditableStyles.propertiesSection) {
        formControl {
            formLabel {
                attrs.component = "legend"
                +"Surface Display Mode"
            }

            radioGroup {
                attrs.value(props.mutableVisualizerControl.surfaceDisplayMode.name)
                attrs.onChangeFunction = {
                    val value = (it.target as HTMLInputElement).value
                    props.mutableVisualizerControl.surfaceDisplayMode = VisualizerControl.SurfaceDisplayMode.valueOf(value)
                    props.editableManager.onChange()
                }

                VisualizerControl.SurfaceDisplayMode.values().forEach {
                    formControlLabel {
                        attrs.value = it.name
                        attrs.control = radio {}
                        attrs.label { +it.name }
                    }
                }
            }
        }
    }

    div(+EditableStyles.propertiesSection) {
        formControlLabel {
            attrs.control {
                switch {
                    attrs.value(props.mutableVisualizerControl.rotate)
                    attrs.onChangeFunction = {
                        val value = (it.target as HTMLInputElement).value
                        props.mutableVisualizerControl.rotate = value.asDynamic()
                        props.editableManager.onChange()
                    }
                }
            }
            attrs.label { typographyH6 { +"Rotate" } }
        }
    }
}

external interface VisualizerPropsEditorProps : RProps {
    var editableManager: EditableManager
    var mutableVisualizerControl: MutableVisualizerControl
}

fun RBuilder.visualizerPropsEditor(handler: RHandler<VisualizerPropsEditorProps>) =
    child(visualizerPropsEditor, handler = handler)