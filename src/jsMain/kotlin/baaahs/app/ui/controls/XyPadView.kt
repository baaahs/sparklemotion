package baaahs.app.ui.controls

import baaahs.Gadget
import baaahs.control.OpenXyPadControl
import baaahs.geom.Vector2F
import baaahs.show.live.ControlProps
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.*
import kotlinx.css.properties.boxShadow
import kotlinx.html.js.onMouseDownFunction
import kotlinx.html.js.onMouseMoveFunction
import kotlinx.html.js.onMouseUpFunction
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import styled.StyleSheet
import styled.inlineStyles


val XyPad = xComponent<XyPadProps>("XyPad") { props ->
    val padSize = Vector2F(200f, 200f)
    val knobSize = Vector2F(20f, 20f)
    val helper = memo(padSize, knobSize, props.xyPadControl) {
        props.xyPadControl.getHelper(padSize, knobSize)
    }
    val backgroundRef = useRef<HTMLElement>()
    val knobRef = useRef<HTMLElement>()
    val crosshairXRef = useRef<HTMLElement>()
    val crosshairYRef = useRef<HTMLElement>()
    val xyPad = props.xyPadControl.xyPad

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

    onMount(xyPad) {
        xyPad.listen(gadgetListener)
        withCleanup { xyPad.unlisten(gadgetListener) }
    }

    val mouseDraggingState = useRef(false)
    val handleMouseEvent by eventHandler(xyPad, helper) { e: Event ->
        val bounds = backgroundRef.current!!.getBoundingClientRect()
        val clickPosPx = Vector2F(
            (e.clientX - bounds.left).toFloat(),
            (e.clientY - bounds.top).toFloat()
        )

        xyPad.position = helper.positionFromPx(clickPosPx)
    }

    val handleMouseDownEvent by eventHandler(handleMouseEvent) { e: Event ->
        if (e.buttons == primaryButton) mouseDraggingState.current = true
        handleMouseEvent(e)
        e.preventDefault()
    }

    val handleMouseUpEvent by eventHandler(handleMouseEvent) { e: Event ->
        handleMouseEvent(e)
        mouseDraggingState.current = false
        e.preventDefault()
    }

    val handleMouseMoveEvent by eventHandler(handleMouseEvent) { e: Event ->
        if (mouseDraggingState.current == true && e.buttons == primaryButton)
            handleMouseEvent(e)
        e.preventDefault()
    }

    val knobPositionPx = helper.knobPositionPx
    val crosshairPositionPx = helper.crosshairPositionPx
    div {
        div(+XyPadStyles.container + props.xyPadControl.inUseStyle) {
            div(+XyPadStyles.background) {
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
        div(+Styles.dataSourceTitle) { +xyPad.title }
    }
}

private val primaryButton = 1
private val Event.buttons: Int get() = asDynamic().buttons as Int
private val Event.clientX: Int get() = asDynamic().clientX as Int
private val Event.clientY: Int get() = asDynamic().clientY as Int

external interface XyPadProps : RProps {
    var controlProps: ControlProps
    var xyPadControl: OpenXyPadControl
}

object XyPadStyles : StyleSheet("app-ui-controls-xypad", isStatic = true) {
    val container by css {
        display = Display.flex
        border = "3px inset"
    }

    val background by css {
        position = Position.relative
        background = "linear-gradient(#DDFFDD, #88EE88)"
    }

    val centerLine by css {
        position = Position.absolute
        backgroundColor = Color("#222222").withAlpha(.5)
        boxShadow(rgba(0, 0, 0, .2), 0.px, 0.px, 1.px, 1.px)
        pointerEvents = PointerEvents.none
    }

    val knob by css {
        width = 20.px
        height = 20.px
        border = "2px outset"

        borderRadius = 3.px
        boxShadow(Color("#222222").withAlpha(.3), 1.px, 1.px, 1.px, 1.px)
        background = "linear-gradient(#6ABBC0, #00A4D1)"

        position = Position.absolute
        cursor = Cursor.move
    }

    val crosshairs by css {
        position = Position.absolute
        backgroundColor = Color("#44FF44")
        boxShadow(Color("#224422").withAlpha(.3), 0.px, 0.px, 1.px, 1.px)
        pointerEvents = PointerEvents.none
    }
}

fun RBuilder.xyPad(handler: RHandler<XyPadProps>) =
    child(XyPad, handler = handler)