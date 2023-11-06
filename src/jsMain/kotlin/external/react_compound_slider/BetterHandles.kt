package external.react_compound_slider

import baaahs.app.ui.gadgets.slider.HandleProps
import baaahs.ui.xComponent
import js.core.Object
import js.core.jso
import react.RBuilder
import react.RHandler
import react.ReactElement
import react.dom.events.KeyboardEvent
import react.dom.events.MouseEvent
import react.dom.events.TouchEvent
import web.html.HTMLElement

fun autofocus(e: MouseEvent<*, *>) {
    val target = e.target
    if (target is HTMLElement) {
        target.focus()
    }
}

val BetterHandles = xComponent<HandlesProps>("BetterHandles") { props ->
    val getHandlePropsFn = callback(props.onKeyDown, props.onMouseDown, props.onTouchStart) { id: String ->
        val handleProps = jso<HandleProps> {}
        Object.assign(handleProps, props).apply {
            this.onKeyDown = { e: KeyboardEvent<*> ->
                listOf(
                    props.onKeyDown,
                    { e1: KeyboardEvent<*> -> props.emitKeyboard?.invoke(e1, id) }
                ).filterNotNull().forEach { it.invoke(e) }
            }

            this.onMouseDown = { e ->
                listOf(
                    props.onMouseDown,
                    ::autofocus,
                    { e1: MouseEvent<*, *> -> props.emitMouse?.invoke(e1, id) }
                ).filterNotNull().forEach { it.invoke(e) }
            }

            this.onTouchStart = { e ->
                listOf(
                    props.onTouchStart,
                    { e1: TouchEvent<*> -> props.emitTouch?.invoke(e1, id) }
                ).filterNotNull().forEach { it.invoke(e) }
            }
        }
        handleProps
    }

    // render():
    val renderedChildren = (props.children.unsafeCast<(handlesObject: HandlesObject) -> ReactElement<*>>()).invoke(
        jso {
            this.handles = props.handles ?: emptyArray()
            this.activeHandleID = props.activeHandleID ?: ""
            this.getHandleProps = getHandlePropsFn
        }
    )

    +react.Children.only(renderedChildren)
}

fun RBuilder.betterHandles(handler: RHandler<HandlesProps>) =
    child(BetterHandles, handler = handler)
