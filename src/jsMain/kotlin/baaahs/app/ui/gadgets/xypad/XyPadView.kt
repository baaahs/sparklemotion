package baaahs.app.ui.gadgets.xypad

import baaahs.Gadget
import baaahs.app.ui.controls.XyPadStyles
import baaahs.gadgets.XyPad
import baaahs.geom.Vector2F
import baaahs.ui.icons.ResetIcon
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.*
import react.*
import react.dom.*
import styled.inlineStyles
import web.html.HTMLElement

private val XyPadView = xComponent<XyPadProps>("XyPad") { props ->
    val padSize = props.padSize ?: Vector2F(200f, 200f)
    val knobSize = props.knobSize ?: Vector2F(20f, 20f)
    val knobBufferZone = props.knobBufferZone ?: true
    val helper = memo(props.xyPad, padSize, knobSize, knobBufferZone) {
        props.xyPad.getHelper(padSize, knobSize, knobBufferZone)
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

    val pointerIsDown = useRef<Number>(null)
    val pointerDownOffset = useRef(Vector2F(0f, 0f))
    val handlePointerEvent by pointerEventHandler(props.xyPad, helper) { e ->
        val positionPx = e.relativePositionPx(backgroundRef) -
                pointerDownOffset.current!!

        props.xyPad.position = helper.positionFromPx(positionPx)
    }

    val handlePointerDownEvent by pointerEventHandler(handlePointerEvent) { e ->
        if (pointerIsDown.current == null) {
            pointerIsDown.current = e.pointerId
            e.currentTarget.setPointerCapture(e.pointerId)

            // If the pointer starts on the knob, move it relative to the pointer, otherwise jump to the pointer.
            val downPositionPx = e.relativePositionPx(backgroundRef)
            val crosshairPositionPx = helper.crosshairPositionPx
            val distFromKnobCenter = downPositionPx - crosshairPositionPx
            val absDist = distFromKnobCenter.abs()
            val halfKnob = knobSize / 2f
            if (absDist.x < halfKnob.x && absDist.y < halfKnob.y) {
                pointerDownOffset.current = distFromKnobCenter
            } else {
                pointerDownOffset.current = Vector2F(0f, 0f)
            }

            handlePointerEvent(e)
            e.preventDefault()
        }
    }

    val handlePointerMoveEvent by pointerEventHandler(handlePointerEvent) { e ->
        if (pointerIsDown.current == e.pointerId) {
            handlePointerEvent(e)
            e.preventDefault()
        }
    }

    val handlePointerUpEvent by pointerEventHandler(handlePointerEvent) { e ->
        if (pointerIsDown.current == e.pointerId) {
            handlePointerEvent(e)
            pointerIsDown.current = null
            e.currentTarget.releasePointerCapture(e.pointerId)
            e.preventDefault()
        }
    }

    val knobPositionPx = helper.knobPositionPx
    val crosshairPositionPx = helper.crosshairPositionPx

    val ignorePointerDown by pointerEventHandler { it.stopPropagation() }
    val handleReset by mouseEventHandler(props.xyPad) {
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

        div(props.knobClasses ?: +XyPadStyles.knob) {
            ref = knobRef
            inlineStyles {
                width = knobSize.x.px
                height = knobSize.y.px
                left = knobPositionPx.x.px
                top = knobPositionPx.y.px
            }
        }

        div(+XyPadStyles.resetButton) {
            attrs.onPointerDown = ignorePointerDown
            attrs.onClick = handleReset

            ResetIcon {}
        }
    }
}

private fun react.dom.events.PointerEvent<*>.relativePositionPx(
    backgroundRef: MutableRefObject<HTMLElement>
): Vector2F {
    val bounds = backgroundRef.current!!.getBoundingClientRect()
    val clickPosPx = Vector2F(
        (clientX - bounds.left).toFloat(),
        (clientY - bounds.top).toFloat()
    )
    return clickPosPx
}

external interface XyPadProps : Props {
    var xyPad: XyPad
    var backgroundClasses: String?
    var padSize: Vector2F?
    var knobSize: Vector2F?
    var knobClasses: String?
    var knobBufferZone: Boolean?
}

fun RBuilder.xyPad(handler: RHandler<XyPadProps>) =
    child(XyPadView, handler = handler)