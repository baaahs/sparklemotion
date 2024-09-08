package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.patchmod.patchMod
import baaahs.app.ui.shaderPreview
import baaahs.control.ButtonControl.ActivationType.Momentary
import baaahs.control.ButtonControl.ActivationType.Toggle
import baaahs.control.OpenButtonControl
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.util.useResizeListener
import mui.material.ToggleButton
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.PointerEvent
import react.useContext
import web.events.EventType
import web.events.addEventListener
import web.html.HTMLButtonElement
import web.html.HTMLDivElement
import web.uievents.MouseButton
import web.uievents.MouseEvent
import web.uievents.MouseEventInit

private val ButtonControlView = xComponent<ButtonProps>("ButtonControl") { props ->
    val appContext = useContext(appContext)

    val buttonControl = props.buttonControl
    observe(buttonControl.switch.observable)

    val showPreview = appContext.uiSettings.renderButtonPreviews
    val patchForPreview = if (showPreview) buttonControl.patchForPreview() else null

    val isPressed = ref(false)

    val handleToggleRelease by pointerEventHandler(buttonControl) { e ->
        if (isPressed.current == true) {
            buttonControl.click()
            redispatchAsMouseClick(e)
        }
    }

    val handlePointerDown by pointerEventHandler(buttonControl) {
        isPressed.current = true

        when (buttonControl.type) {
            Momentary -> if (!buttonControl.isPressed) buttonControl.click()
            Toggle -> {}
        }
    }

    val handlePointerUp by pointerEventHandler(buttonControl, handleToggleRelease) {
        try {
            when (buttonControl.type) {
                Momentary -> if (buttonControl.isPressed) buttonControl.click()
                Toggle -> handleToggleRelease(it)
            }
        } finally {
            isPressed.current = false
        }
    }

    val handlePatchModSwitch by handler(buttonControl) {
        buttonControl.click()
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
            buttonEl.addEventListener(EventType("long-press"), { e ->
                val originalEvent = e.asDynamic().detail.originalEvent as web.uievents.PointerEvent
                val isPrimaryButton = originalEvent.button == MouseButton.MAIN && !originalEvent.ctrlKey
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
                attrs.className = -Styles.buttonLabelWhenPreview
                attrs.classes = muiClasses {
                    selected = -Styles.buttonSelectedWhenPreview
                }
            }

            attrs.value = "n/a"
            // Yep, for some reason you need to set it directly or it doesn't work.
            attrs.selected = buttonControl.isPressed

            attrs.onPointerDown = handlePointerDown
            attrs.onPointerUp = handlePointerUp

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

/** [GridButtonGroupControlView] needs a click event so it can deselect other buttons in the group. */
private fun redispatchAsMouseClick(e: PointerEvent<*>) {
    val event = MouseEvent(MouseEvent.CLICK, e.unsafeCast<MouseEventInit>())
    e.currentTarget.parentElement?.dispatchEvent(event)
}

// This is stupid. No evident way to override `selected` class?
internal enum class SelectedStyle { selected }


external interface ButtonProps : Props {
    var controlProps: ControlProps
    var buttonControl: OpenButtonControl
}

fun RBuilder.buttonControl(handler: RHandler<ButtonProps>) =
    child(ButtonControlView, handler = handler)