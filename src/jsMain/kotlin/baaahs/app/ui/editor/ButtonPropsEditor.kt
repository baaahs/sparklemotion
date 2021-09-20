package baaahs.app.ui.editor

import baaahs.control.ButtonControl
import baaahs.control.MutableButtonControl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.formcontrol.formControl
import materialui.components.formcontrollabel.formControlLabel
import materialui.components.formlabel.formLabel
import materialui.components.radio.radio
import materialui.components.radiogroup.radioGroup
import org.w3c.dom.HTMLInputElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

private val buttonPropsEditor =
    xComponent<ButtonPropsEditorProps>("ButtonPropsEditor") { props ->
        div(+EditableStyles.propertiesSection) {
            formControl {
                formLabel {
                    attrs.component = "legend"
                    +"Button Type"
                }

                radioGroup {
                    attrs.value(props.mutableButtonControl.activationType.name)
                    attrs.onChangeFunction = {
                        val value = (it.target as HTMLInputElement).value
                        props.mutableButtonControl.activationType = ButtonControl.ActivationType.valueOf(value)
                        props.editableManager.onChange()
                    }

                    formControlLabel {
                        attrs.value = "Toggle"
                        attrs.control { radio {} }
                        attrs.label { +"Toggle" }
                    }
                    formControlLabel {
                        attrs.value = "Momentary"
                        attrs.control { radio {} }
                        attrs.label { +"Momentary" }
                    }
                }
            }
        }
    }

external interface ButtonPropsEditorProps : Props {
    var editableManager: EditableManager
    var mutableButtonControl: MutableButtonControl
}

fun RBuilder.buttonPropsEditor(handler: RHandler<ButtonPropsEditorProps>) =
    child(buttonPropsEditor, handler = handler)