package baaahs.app.ui.controls

import baaahs.control.ButtonControl
import baaahs.control.OpenButtonControl
import baaahs.show.live.ControlProps
import baaahs.ui.unaryPlus
import baaahs.ui.withEvent
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseDownFunction
import kotlinx.html.js.onMouseUpFunction
import materialui.lab.components.togglebutton.toggleButton
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.div
import materialui.components.button.button as muiButton

private val Button = xComponent<ButtonProps>("Button") { props ->
    val buttonControl = props.buttonControl
    val onShowStateChange = props.controlProps.onShowStateChange

    val handleToggleClick = handler("toggle click", onShowStateChange) {
        buttonControl.click()
        onShowStateChange()
    }

    val handleMomentaryPress = handler("momentary press", onShowStateChange) {
        if (!buttonControl.isPressed) buttonControl.click()
        onShowStateChange()
    }

    val handleMomentaryRelease = handler("momentary release", onShowStateChange) {
        if (buttonControl.isPressed) buttonControl.click()
        onShowStateChange()
    }

    div(+Styles.controlButton) {
        when (buttonControl.type) {
            ButtonControl.ActivationType.Toggle ->
                toggleButton {
                    attrs["value"] = "n/a"
                    // Yep, for some reason you need to set it directly or it doesn't work.
                    attrs["selected"] = buttonControl.isPressed
                    attrs.onClickFunction = handleToggleClick.withEvent()

                    +buttonControl.title
                }

            ButtonControl.ActivationType.Momentary ->
                muiButton {
                    attrs["value"] = "n/a"
                    attrs["selected"] = buttonControl.isPressed
                    attrs.onMouseDownFunction = handleMomentaryPress.withEvent()
                    attrs.onMouseUpFunction = handleMomentaryRelease.withEvent()

                    +buttonControl.title
                }
        }
    }
}

external interface ButtonProps : RProps {
    var controlProps: ControlProps
    var buttonControl: OpenButtonControl
}

fun RBuilder.button(handler: RHandler<ButtonProps>) =
    child(Button, handler = handler)