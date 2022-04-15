package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderPreview
import baaahs.control.ButtonControl
import baaahs.control.OpenButtonControl
import baaahs.show.live.ControlProps
import baaahs.ui.*
import kotlinx.js.jso
import mui.material.ToggleButton
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val ButtonView = xComponent<ButtonProps>("Button") { props ->
    val appContext = useContext(appContext)

    val buttonControl = props.buttonControl
    val onShowStateChange = props.controlProps.onShowStateChange
    val showPreview = appContext.uiSettings.renderButtonPreviews
    val shaderForPreview = if (showPreview) buttonControl.shaderForPreview() else null

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

    div(+Styles.controlRoot and Styles.controlButton) {
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
                ToggleButton {
                    if (showPreview) {
                        attrs.classes = jso {
                            root = -Styles.buttonLabelWhenPreview
                            selected = -Styles.buttonSelectedWhenPreview
                        }
                    }

                    attrs.value = "n/a"
                    // Yep, for some reason you need to set it directly or it doesn't work.
                    attrs.selected = buttonControl.isPressed
                    attrs.onClick = handleToggleClick.withTMouseEvent()

                    +buttonControl.title
                }

            ButtonControl.ActivationType.Momentary ->
                ToggleButton {
                    if (showPreview) {
                        attrs.classes = jso {
                            root = -Styles.buttonLabelWhenPreview
                            selected = -Styles.buttonSelectedWhenPreview
                        }
                    }

                    attrs.value = "n/a"
                    attrs.selected = buttonControl.isPressed
                    attrs.onMouseDown = handleMomentaryPress.withMouseEvent()
                    attrs.onMouseUp = handleMomentaryRelease.withMouseEvent()

                    +buttonControl.title
                }
        }
    }
}

// This is stupid. No evident way to override `selected` class?
internal enum class SelectedStyle { selected }


external interface ButtonProps : Props {
    var controlProps: ControlProps
    var buttonControl: OpenButtonControl
}

fun RBuilder.button(handler: RHandler<ButtonProps>) =
    child(ButtonView, handler = handler)