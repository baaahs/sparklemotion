package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.patchmod.patchMod
import baaahs.app.ui.shaderPreview
import baaahs.control.ButtonControl
import baaahs.control.OpenButtonControl
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.util.useResizeListener
import js.core.jso
import mui.material.ToggleButton
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import web.events.EventType
import web.html.HTMLButtonElement
import web.html.HTMLDivElement

private val ButtonControlView = xComponent<ButtonProps>("ButtonControl") { props ->
    val appContext = useContext(appContext)

    val buttonControl = props.buttonControl
    val onShowStateChange = props.controlProps.onShowStateChange
    val showPreview = appContext.uiSettings.renderButtonPreviews
    val patchForPreview = if (showPreview) buttonControl.patchForPreview() else null

    val handleToggleClick by eventHandler(props.buttonControl, onShowStateChange) {
        buttonControl.click()
        onShowStateChange()
    }

    val handleMomentaryPress by eventHandler(props.buttonControl, onShowStateChange) {
        if (!buttonControl.isPressed) buttonControl.click()
        onShowStateChange()
    }

    val handleMomentaryRelease by eventHandler(props.buttonControl, onShowStateChange) {
        if (buttonControl.isPressed) buttonControl.click()
        onShowStateChange()
    }

    val handlePatchModSwitch by handler(props.buttonControl, onShowStateChange) {
        buttonControl.click()
        onShowStateChange()
    }

    val buttonRef = ref<HTMLButtonElement>()
    val titleDivRef = ref<HTMLDivElement>()
    useResizeListener(buttonRef) { _, _ ->
        titleDivRef.current?.fitText()
    }

    var lightboxOpen by state { false }
    onMount(buttonControl, buttonRef.current) {
        val buttonEl = buttonRef.current
        if (buttonControl.expandsOnLongPress && buttonEl != null) {
            buttonEl.setAttribute("data-long-press-delay", "750")
            buttonEl.addEventListener(EventType("long-press"), callback = { e ->
                val isPrimaryButton = true // (e as MouseEvent).button.toInt() == Events.Button.primary
                if (isPrimaryButton && appContext.showManager.editMode.isOff) {
                    lightboxOpen = true
                }
            })
        }
    }

    div(+Styles.controlRoot and Styles.controlButton) {
        if (patchForPreview != null) {
            div(+Styles.buttonShaderPreviewContainer) {
                shaderPreview {
                    attrs.shader = patchForPreview.shader.shader
//                    attrs.patch = patchForPreview
                }
            }
        }

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

            when (buttonControl.type) {
                ButtonControl.ActivationType.Toggle -> {
                    attrs.onClick = handleToggleClick.withTMouseEvent()
                }

                ButtonControl.ActivationType.Momentary -> {
                    attrs.onMouseDown = handleMomentaryPress.withMouseEvent()
                    attrs.onMouseUp = handleMomentaryRelease.withMouseEvent()
                }
            }

            div {
                ref = titleDivRef
                +buttonControl.title
            }
        }
    }

    if (lightboxOpen && patchForPreview != null) {
        patchMod {
            attrs.title = buttonControl.title
            attrs.patchHolder = buttonControl
            attrs.isActive = buttonControl.isPressed
            attrs.onToggle = handlePatchModSwitch
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