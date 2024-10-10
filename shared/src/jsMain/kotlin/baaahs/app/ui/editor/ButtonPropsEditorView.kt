package baaahs.app.ui.editor

import baaahs.control.ButtonControl
import baaahs.control.MutableButtonControl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.*
import mui.types.PropsWithComponent
import react.*
import react.dom.div
import react.dom.events.ChangeEvent
import react.dom.html.ReactHTML

private val ButtonPropsEditorView =
    xComponent<ButtonPropsEditorProps>("ButtonPropsEditor") { props ->
        div(+EditableStyles.propertiesSection) {
            FormControl {
                FormLabel {
                    (attrs as PropsWithComponent).component = ReactHTML.legend
                    +"Button Type"
                }

                RadioGroup {
                    attrs.value = props.mutableButtonControl.activationType.name
                    attrs.onChange = { _: ChangeEvent<*>, value: String ->
                        props.mutableButtonControl.activationType = ButtonControl.ActivationType.valueOf(value)
                        props.editableManager.onChange()
                    }

                    FormControlLabel {
                        attrs.value = "Toggle"
                        attrs.control = Radio.create()
                        attrs.label = buildElement { +"Toggle" }
                    }
                    FormControlLabel {
                        attrs.value = "Momentary"
                        attrs.control = Radio.create()
                        attrs.label = buildElement { +"Momentary" }
                    }
                }
            }
        }
    }

external interface ButtonPropsEditorProps : Props {
    var editableManager: EditableManager<*>
    var mutableButtonControl: MutableButtonControl
}

fun RBuilder.buttonPropsEditor(handler: RHandler<ButtonPropsEditorProps>) =
    child(ButtonPropsEditorView, handler = handler)