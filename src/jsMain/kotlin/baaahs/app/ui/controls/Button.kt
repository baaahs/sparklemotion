package baaahs.app.ui.controls

import baaahs.app.ui.AppContext
import baaahs.app.ui.ControlEditIntent
import baaahs.show.ButtonControl
import baaahs.show.live.ControlProps
import baaahs.show.live.ControlView
import baaahs.show.live.OpenButtonControl
import baaahs.show.live.OpenControl
import baaahs.ui.unaryPlus
import baaahs.ui.withEvent
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseDownFunction
import kotlinx.html.js.onMouseUpFunction
import materialui.toggleButton
import react.FunctionalComponent
import react.RBuilder
import react.RHandler
import react.child
import react.dom.div
import materialui.components.button.button as muiButton

class ButtonControlView(private val openButtonControl: OpenButtonControl) : ControlView {
    override fun <P : ControlProps<in OpenControl>> getReactElement(): FunctionalComponent<P> {
        return button.unsafeCast<FunctionalComponent<P>>()
    }

    override fun onEdit(appContext: AppContext) {
        appContext.openEditor(ControlEditIntent(openButtonControl.id))
    }
}

private val button = xComponent<ButtonProps>("Button") { props ->
    val buttonControl = props.control

    val handleToggleClick = handler("toggle click") {
        buttonControl.click()
        props.onShowStateChange()
    }

    val handleMomentaryPress = handler("momentary press") {
        if (!buttonControl.isPressed) buttonControl.click()
        props.onShowStateChange()
    }

    val handleMomentaryRelease = handler("momentary release") {
        if (buttonControl.isPressed) buttonControl.click()
        props.onShowStateChange()
    }

    div(+Styles.controlButton) {
        when (buttonControl.type) {
            ButtonControl.ActivationType.Toggle ->
                toggleButton {
                    attrs["value"] = "n/a"
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

external interface ButtonProps : ControlProps<OpenButtonControl>

fun RBuilder.button(handler: RHandler<ButtonProps>) =
    child(button, handler = handler)