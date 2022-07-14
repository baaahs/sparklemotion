package baaahs.app.ui.gadgets.xypad

import baaahs.Gadget
import baaahs.app.ui.appContext
import baaahs.app.ui.controls.XyPadStyles
import baaahs.app.ui.controls.inUseStyle
import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.ui.*
import kotlinx.css.*
import kotlinx.html.js.onMouseDownFunction
import kotlinx.html.js.onMouseMoveFunction
import kotlinx.html.js.onMouseUpFunction
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import styled.inlineStyles

private val XyPadView = xComponent<XyPadProps>("XyPad") { props ->
    val appContext = useContext(appContext)
    val controlsStyles = appContext.allStyles.controls

    val padSize = Vector2F(200f, 200f)
    val knobSize = Vector2F(20f, 20f)
    val helper = memo(padSize, knobSize, props.xyPad) {
        props.xyPad.getHelper(padSize, knobSize)
    }
    val backgroundRef = useRef<HTMLElement>()
    val knobRef = useRef<HTMLElement>()
    val crosshairXRef = useRef<HTMLElement>()
    val crosshairYRef = useRef<HTMLElement>()

    val updatePosition by handler(helper) {
        knobRef.current?.let { knob ->
            val newPosition = helper.knobPositionPx
            knob.style.left = newPosition.x.px.value
            knob.style.top = newPosition.y.px.value
        }
        val crosshairPosition = helper.crosshairPositionPx
        crosshairXRef.current?.let { el -> el.style.left = crosshairPosition.x.px.value }
        crosshairYRef.current?.let { el -> el.style.top = crosshairPosition.y.px.value }
        Unit
    }

    val gadgetListener by handler(updatePosition) { _: Gadget -> updatePosition() }

    onMount(props.xyPad) {
        props.xyPad.listen(gadgetListener)
        withCleanup { props.xyPad.unlisten(gadgetListener) }
    }

    val mouseDraggingState = useRef(false)
    val handleMouseEvent by eventHandler(props.xyPad, helper) { e: Event ->
        val bounds = backgroundRef.current!!.getBoundingClientRect()
        val clickPosPx = Vector2F(
            (e.clientX - bounds.left).toFloat(),
            (e.clientY - bounds.top).toFloat()
        )

        props.xyPad.position = helper.positionFromPx(clickPosPx)
    }

    val handleMouseDownEvent by eventHandler(handleMouseEvent) { e: Event ->
        if (e.buttons == Events.ButtonMask.primary) mouseDraggingState.current = true
        handleMouseEvent(e)
        e.preventDefault()
    }

    val handleMouseUpEvent by eventHandler(handleMouseEvent) { e: Event ->
        handleMouseEvent(e)
        mouseDraggingState.current = false
        e.preventDefault()
    }

    val handleMouseMoveEvent by eventHandler(handleMouseEvent) { e: Event ->
        if (mouseDraggingState.current == true && e.buttons == Events.ButtonMask.primary)
            handleMouseEvent(e)
        e.preventDefault()
    }

    val knobPositionPx = helper.knobPositionPx
    val crosshairPositionPx = helper.crosshairPositionPx

    div(props.backgroundClasses ?: +XyPadStyles.background) {
        ref = backgroundRef
        inlineStyles {
            width = padSize.x.px
            height = padSize.y.px
        }

        attrs.onMouseDownFunction = handleMouseDownEvent
        attrs.onMouseUpFunction = handleMouseUpEvent
        attrs.onMouseMoveFunction = handleMouseMoveEvent

        div(+XyPadStyles.centerLine) {
            inlineStyles {
                width = 1.px
                left = (padSize.x / 2).px
                height = padSize.y.px
            }
        }

        div(+XyPadStyles.centerLine) {
            inlineStyles {
                height = 1.px
                top = (padSize.y / 2).px
                width = padSize.x.px
            }
        }

        div(+XyPadStyles.crosshairs) {
            ref = crosshairXRef
            inlineStyles {
                width = 1.px
                left = crosshairPositionPx.x.px
                height = padSize.y.px
            }
        }

        div(+XyPadStyles.crosshairs) {
            ref = crosshairYRef
            inlineStyles {
                height = 1.px
                top = crosshairPositionPx.y.px
                width = padSize.x.px
            }
        }

        div(+XyPadStyles.knob) {
            ref = knobRef
            inlineStyles {
                width = knobSize.x.px
                height = knobSize.y.px
                left = knobPositionPx.x.px
                top = knobPositionPx.y.px
            }
        }
    }
}

external interface XyPadProps : Props {
    var xyPad: XyPad
    var backgroundClasses: String?
}

fun RBuilder.xyPad(handler: RHandler<XyPadProps>) =
    child(XyPadView, handler = handler)