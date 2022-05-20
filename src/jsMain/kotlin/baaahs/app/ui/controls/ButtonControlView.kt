package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.shaderPreview
import baaahs.control.ButtonControl
import baaahs.control.OpenButtonControl
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.util.useResizeListener
import kotlinx.js.jso
import mui.material.ToggleButton
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val ButtonControlView = xComponent<ButtonProps>("ButtonControl") { props ->
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

    val buttonRef = ref<HTMLButtonElement>()
    val titleDivRef = ref<HTMLDivElement>()
    useResizeListener(buttonRef) {
        titleDivRef.current!!.fitText()
    }

    var lightboxOpen by state { false }
    onMount(buttonRef.current) {
        val buttonEl = buttonRef.current
        if (buttonEl != null) {
            buttonEl.setAttribute("data-long-press-delay", "750")
            buttonEl.addEventListener("long-press", callback = {
                if (appContext.showManager.editMode.isOff) {
                    lightboxOpen = true
                }
            })
        }
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
                    ref = buttonRef

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

                    div {
                        ref = titleDivRef
                        +buttonControl.title
                    }
                }

            ButtonControl.ActivationType.Momentary ->
                ToggleButton {
                    ref = buttonRef

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

                    div {
                        ref = titleDivRef
                        +buttonControl.title
                    }
                }
        }
    }

    if (lightboxOpen && shaderForPreview != null) {
        patchMod {
            attrs.title = buttonControl.title
            attrs.shader = shaderForPreview.shader
            attrs.onClose = { lightboxOpen = false }
        }
    }
}

// This is stupid. No evident way to override `selected` class?
internal enum class SelectedStyle { selected }


external interface ButtonProps : Props {
    var controlProps: ControlProps
    var buttonControl: OpenButtonControl
}

fun RBuilder.buttonControl(handler: RHandler<ButtonProps>) =
    child(ButtonControlView, handler = handler)