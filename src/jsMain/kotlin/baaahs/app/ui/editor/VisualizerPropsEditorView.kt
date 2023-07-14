package baaahs.app.ui.editor

import baaahs.control.MutableVisualizerControl
import baaahs.control.VisualizerControl
import baaahs.ui.typographyH6
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.*
import mui.types.PropsWithComponent
import react.*
import react.dom.div
import react.dom.events.ChangeEvent
import react.dom.html.ReactHTML
import web.html.HTMLInputElement

private val VisualizerPropsEditorView = xComponent<VisualizerPropsEditorProps>("VisualizerPropsEditor") { props ->

    div(+EditableStyles.propertiesSection) {
        FormControl {
            FormLabel {
                (attrs as PropsWithComponent).component = ReactHTML.legend
                +"Surface Display Mode"
            }

            RadioGroup {
                attrs.value = props.mutableVisualizerControl.surfaceDisplayMode.name
                attrs.onChange = { _: ChangeEvent<HTMLInputElement>, value: String ->
                    props.mutableVisualizerControl.surfaceDisplayMode = VisualizerControl.SurfaceDisplayMode.valueOf(value)
                    props.editableManager.onChange()
                }

                VisualizerControl.SurfaceDisplayMode.values().forEach {
                    FormControlLabel {
                        attrs.value = it.name
                        attrs.control = Radio.create()
                        attrs.label = buildElement { +it.name }
                    }
                }
            }
        }
    }

    div(+EditableStyles.propertiesSection) {
        FormControlLabel {
            attrs.control = buildElement {
                Switch {
                    attrs.checked = props.mutableVisualizerControl.rotate
                    attrs.onChange = { _, checked ->
                        props.mutableVisualizerControl.rotate = checked
                        props.editableManager.onChange()
                    }
                }
            }
            attrs.label = buildElement { typographyH6 { +"Rotate" } }
        }
    }
}

external interface VisualizerPropsEditorProps : Props {
    var editableManager: EditableManager<*>
    var mutableVisualizerControl: MutableVisualizerControl
}

fun RBuilder.visualizerPropsEditor(handler: RHandler<VisualizerPropsEditorProps>) =
    child(VisualizerPropsEditorView, handler = handler)