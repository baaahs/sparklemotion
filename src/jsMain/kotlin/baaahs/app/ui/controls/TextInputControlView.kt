package baaahs.app.ui.controls

import baaahs.Gadget
import baaahs.app.ui.appContext
import baaahs.app.ui.editor.textFieldEditor
import baaahs.control.OpenTextInputControl
import baaahs.geom.Vector2F
import baaahs.show.live.ControlProps
import baaahs.ui.*
import kotlinx.css.*
import kotlinx.css.properties.boxShadow
import kotlinx.html.js.onChangeFunction
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.input
import styled.StyleSheet
import styled.inlineStyles
import mui.material.TextField
import mui.material.Button
import react.dom.events.FormEvent
import react.dom.onChange

private val TextInputControlView = xComponent<TextInputProps>("TextInputControl") { props ->
    val appContext = useContext(appContext)
    val controlsStyles = appContext.allStyles.controls

    val textInput = props.textInputControl.textInput

    var inputVal = ""
    val onChange by formEventHandler() { event: FormEvent<*> ->
        inputVal = event.target.value
    }

    val handleSave by mouseEventHandler() {
        println("handle save")
        println(inputVal)
        textInput.value = inputVal
    }

    div (+TextInputStyles.container) {
        TextField {
            attrs.label = buildElement { +"Add text to display" }
            // attrs.value = textInput.value
            attrs.onChange = onChange
        }
        Button {
            attrs.onClick = handleSave
            attrs.disabled = inputVal.length > 0
            +"Update"
        }
    }
}

external interface TextInputProps : Props {
    var controlProps: ControlProps
    var textInputControl: OpenTextInputControl
}

object TextInputStyles : StyleSheet("app-ui-controls-textinput", isStatic = true) {
    val container by css {
        display = Display.flex
        flexDirection = FlexDirection.column
        padding(1.em)
    }
}

fun RBuilder.textInputControl(handler: RHandler<TextInputProps>) =
    child(TextInputControlView, handler = handler)
