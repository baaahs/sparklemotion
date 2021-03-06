package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderPreview
import baaahs.control.ButtonControl
import baaahs.control.OpenButtonControl
import baaahs.show.live.ControlProps
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.withEvent
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onMouseDownFunction
import kotlinx.html.js.onMouseUpFunction
import materialui.components.button.enums.ButtonStyle
import materialui.lab.components.togglebutton.enums.ToggleButtonStyle
import materialui.lab.components.togglebutton.toggleButton
import react.*
import react.dom.div
import materialui.components.button.button as muiButton

private val Button = xComponent<ButtonProps>("Button") { props ->
    val appContext = useContext(appContext)

    val buttonControl = props.buttonControl
    val onShowStateChange = props.controlProps.onShowStateChange
    val shaderForPreview = if (appContext.uiSettings.renderButtonPreviews)
        buttonControl.shaderForPreview() else null

    val handleToggleClick by eventHandler(onShowStateChange) {
        buttonControl.click()
        onShowStateChange()
    }

    val handleMomentaryPress by eventHandler(onShowStateChange) {
        if (!buttonControl.isPressed) buttonControl.click()
        onShowStateChange()
    }

    val handleMomentaryRelease by eventHandler(onShowStateChange) {
        if (buttonControl.isPressed) buttonControl.click()
        onShowStateChange()
    }

    div(+Styles.controlButton) {
        if (shaderForPreview != null) {
            div(+Styles.buttonShaderPreviewContainer) {
                shaderPreview {
                    attrs.shader = shaderForPreview.shader
                }
            }
        }

        when (buttonControl.type) {
            ButtonControl.ActivationType.Toggle ->
                // When previews are on:
                //   background: radial-gradient(rgba(255, 255, 255, .75), transparent);
                //   color: black
                toggleButton {
                    if (shaderForPreview != null) {
                        attrs.classes(Styles.buttonLabelWhenPreview on ToggleButtonStyle.label)
                    }

                    attrs["value"] = "n/a"
                    // Yep, for some reason you need to set it directly or it doesn't work.
                    attrs["selected"] = buttonControl.isPressed
                    attrs.onClickFunction = handleToggleClick.withEvent()

                    +buttonControl.title
                }

            ButtonControl.ActivationType.Momentary ->
                muiButton {
                    if (shaderForPreview != null) {
                        attrs.classes(Styles.buttonLabelWhenPreview on ButtonStyle.label)
                    }

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