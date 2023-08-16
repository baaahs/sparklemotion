package baaahs.app.ui.editor

import baaahs.control.ButtonGroupControl
import baaahs.control.MutableButtonGroupControl
import baaahs.ui.asTextNode
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.*
import react.*
import react.dom.div
import react.dom.events.ChangeEvent
import react.dom.html.ReactHTML
import web.html.HTMLInputElement

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

            FormControlLabel {
                attrs.control = buildElement {
                    Switch {
                        attrs.checked = props.mutableButtonGroupControl.showTitle
                        attrs.onChange = { _, checked ->
                            props.mutableButtonGroupControl.showTitle = checked
                            props.editableManager.onChange()
                        }
                    }
                }
                attrs.label = buildElement { +"Show Title" }
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

            FormControlLabel {
                attrs.control = buildElement {
                    Switch {
                        attrs.checked = props.mutableButtonGroupControl.allowMultiple ?: false
                        attrs.onChange = { _: ChangeEvent<HTMLInputElement>, checked: Boolean ->
                            props.mutableButtonGroupControl.allowMultiple = checked
                            props.editableManager.onChange()
                        }
                    }
                }
                attrs.label = buildElement { +"Allow Multiple Selected" }
            }
        }
    }

external interface ButtonGroupPropsEditorProps : Props {
    var editableManager: EditableManager<*>
    var mutableButtonGroupControl: MutableButtonGroupControl
}

fun RBuilder.buttonGroupPropsEditor(handler: RHandler<ButtonGroupPropsEditorProps>) =
    child(ButtonGroupPropsEditorView, handler = handler)