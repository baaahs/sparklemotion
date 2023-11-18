package baaahs.ui.slider

import baaahs.app.ui.gadgets.slider.HandleProps
import baaahs.ui.xComponent
import js.core.jso
import react.*
import react.dom.events.MouseEvent
import web.html.HTMLElement

external interface HandlesObject {
    var handles: Array<BetterSliderItem>
    var activeHandleID: String?
    var getHandleProps: GetHandleProps
}

fun autofocus(e: MouseEvent<*, *>) {
    val target = e.target
    if (target is HTMLElement) {
        target.focus()
    }
}

private val Handles = xComponent<HandlesProps>("BetterHandles") { props ->
    val getHandleProps = { handleId: String ->
        jso<HandleProps> {
            onKeyDown = { e ->
                props.onKeyDown?.invoke(e)
                props.emitKeyboard?.invoke(e, handleId)
            }
            onPointerDown = { e ->
                props.onPointerDown?.invoke(e)
                autofocus(e)
                props.emitPointer?.invoke(e, Location.Handle, handleId)
            }
        }
    }

    val renderedChildren = props.children.invoke(jso {
        this.handles = props.handles
        this.activeHandleID = props.activeHandleID
        this.getHandleProps = getHandleProps
    })
    +Children.only(renderedChildren)
}

external interface HandlesProps : Props, StandardEventHandlers, StandardEventEmitters {
    var handles: Array<BetterSliderItem>
    var activeHandleID: String?
    var children: (handlesObject: HandlesObject) -> ReactElement<*>
}

fun RBuilder.handles(handler: RHandler<HandlesProps>) =
    child(Handles, handler = handler)

external interface HandleItem {
    var key: String
    var `val`: Double
}
