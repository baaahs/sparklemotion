package baaahs.app.ui.editor

import baaahs.control.ButtonGroupControl
import baaahs.control.MutableButtonGroupControl
import baaahs.ui.asTextNode
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.*
import org.w3c.dom.HTMLInputElement
import react.Props
import react.RBuilder
import react.RHandler
import react.create
import react.dom.div
import react.dom.events.ChangeEvent
import react.dom.html.ReactHTML

private val ButtonGroupPropsEditorView =
    xComponent<ButtonGroupPropsEditorProps>("ButtonGroupPropsEditor") { props ->
        div(+EditableStyles.propertiesSection) {
            textFieldEditor {
                attrs.label = "Title"
                attrs.helperText = "Not actually displayed anywhere"
                attrs.autoFocus = true
                attrs.getValue = { props.mutableButtonGroupControl.title }
                attrs.setValue = { value -> props.mutableButtonGroupControl.title = value }
                attrs.editableManager = props.editableManager
            }
        }

        div(+EditableStyles.propertiesSection) {
            FormControl {
                InputLabel {
                    attrs.component = ReactHTML.legend
                    +"Direction"
                }

                RadioGroup {
                    attrs.value = props.mutableButtonGroupControl.direction.name
                    attrs.onChange = { _: ChangeEvent<HTMLInputElement>, value: String ->
                        props.mutableButtonGroupControl.direction = ButtonGroupControl.Direction.valueOf(value)
                        props.editableManager.onChange()
                    }

                    ButtonGroupControl.Direction.values().forEach {
                        FormControlLabel {
                            attrs.value = it.name
                            attrs.control = Radio.create()
                            attrs.label = it.name.asTextNode()
                        }
                    }
                }
            }
        }
    }

external interface ButtonGroupPropsEditorProps : Props {
    var editableManager: EditableManager<*>
    var mutableButtonGroupControl: MutableButtonGroupControl
}

fun RBuilder.buttonGroupPropsEditor(handler: RHandler<ButtonGroupPropsEditorProps>) =
    child(ButtonGroupPropsEditorView, handler = handler)