package baaahs.app.ui.gadgets.xypad

import baaahs.Gadget
import baaahs.app.ui.appContext
import baaahs.app.ui.controls.XyPadStyles
import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.ui.unaryPlus
import baaahs.ui.withEvent
import baaahs.ui.xComponent
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLElement
import react.*
import react.dom.*
import styled.inlineStyles

private val XyPadView = xComponent<XyPadProps>("XyPad") { props ->
    val appContext = useContext(appContext)
    val controlsStyles = appContext.allStyles.controls

    val padSize = props.padSize ?: Vector2F(200f, 200f)
    val knobSize = props.knobSize ?: Vector2F(20f, 20f)
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

    onMount(props.xyPad, gadgetListener) {
        props.xyPad.listen(gadgetListener)
        withCleanup { props.xyPad.unlisten(gadgetListener) }
    }

    val pointerDraggingState = useRef(false)
    val handlePointerEvent by pointerEventHandler(props.xyPad, helper) { e ->
        val bounds = backgroundRef.current!!.getBoundingClientRect()
        val clickPosPx = Vector2F(
            (e.clientX - bounds.left).toFloat(),
            (e.clientY - bounds.top).toFloat()
        )

        props.xyPad.position = helper.positionFromPx(clickPosPx)
    }

    val handlePointerDownEvent by pointerEventHandler(handlePointerEvent) { e ->
        if (e.isPrimary) pointerDraggingState.current = true
        handlePointerEvent(e)
        e.preventDefault()
    }

    val handlePointerUpEvent by pointerEventHandler(handlePointerEvent) { e ->
        handlePointerEvent(e)
        pointerDraggingState.current = false
        e.preventDefault()
    }

    val handlePointerMoveEvent by pointerEventHandler(handlePointerEvent) { e ->
        if (pointerDraggingState.current == true && e.isPrimary)
            handlePointerEvent(e)
        e.preventDefault()
    }

    val knobPositionPx = helper.knobPositionPx
    val crosshairPositionPx = helper.crosshairPositionPx

    val handleReset by eventHandler {
        props.xyPad.position = props.xyPad.initialValue
    }

    div(props.backgroundClasses ?: +XyPadStyles.background) {
        ref = backgroundRef
        inlineStyles {
            width = padSize.x.px
            height = padSize.y.px
        }

        attrs.onPointerDown = handlePointerDownEvent
        attrs.onPointerUp = handlePointerUpEvent
        attrs.onPointerMove = handlePointerMoveEvent

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

        div(+XyPadStyles.resetButton) {
            attrs.onClickFunction = handleReset.withEvent()
            b { +"R" }
        }
    }
}

external interface XyPadProps : Props {
    var xyPad: XyPad
    var backgroundClasses: String?
    var padSize: Vector2F?
    var knobSize: Vector2F?
}

fun RBuilder.xyPad(handler: RHandler<XyPadProps>) =
    child(XyPadView, handler = handler)