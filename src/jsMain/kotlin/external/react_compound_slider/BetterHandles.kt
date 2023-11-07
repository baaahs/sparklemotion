package external.react_compound_slider

import baaahs.ui.mixin
import baaahs.ui.xComponent
import react.RBuilder
import react.RHandler
import react.dom.events.MouseEvent
import web.html.HTMLElement

fun autofocus(e: MouseEvent<*, *>) {
    val target = e.target
    if (target is HTMLElement) {
        target.focus()
    }
}

val BetterHandles = xComponent<HandlesProps>("BetterHandles") { props ->
    // render():
    Handles {
        mixin(props)
        attrs.children = { handlesObject ->
            val origGetHandleProps = handlesObject.getHandleProps
            handlesObject.getHandleProps = { id: String ->
                origGetHandleProps(id).apply {
                    onMouseDown = null
                    onTouchStart = null
                    onPointerDown = { e ->
                        props.onPointerDown?.invoke(e)
                        autofocus(e)
                        props.emitPointer?.invoke(e, "handle", id)
                    }
                }
            }
            props.children.invoke(handlesObject)
        }
    }
}

fun RBuilder.betterHandles(handler: RHandler<HandlesProps>) =
    child(BetterHandles, handler = handler)

val BetterTracks = xComponent<TracksProps>("BetterTracks") { props ->
    // render():
    Tracks {
        mixin(props)
        attrs.children = { tracksObject ->
            val origGetProps = tracksObject.getTrackProps
            tracksObject.getTrackProps = {
                origGetProps().apply {
                    onMouseDown = null
                    onTouchStart = null
                    onPointerDown = { e ->
                        props.onPointerDown?.invoke(e)
                        autofocus(e)
                        props.emitPointer?.invoke(e, "track", null)
                    }
                }
            }
            props.children.invoke(tracksObject)
        }
    }
}

fun RBuilder.betterTracks(handler: RHandler<TracksProps>) =
    child(BetterTracks, handler = handler)

val BetterRail = xComponent<RailProps>("BetterRail") { props ->
    // render():
    Rail {
        mixin(props)
        attrs.children = { railObject ->
            val origGetProps = railObject.getRailProps
            railObject.getRailProps = {
                origGetProps().apply {
                    onMouseDown = null
                    onTouchStart = null
                    onPointerDown = { e ->
                        props.onPointerDown?.invoke(e)
                        autofocus(e)
                        props.emitPointer?.invoke(e, "rail", null)
                    }
                }
            }
            props.children.invoke(railObject)
        }
    }
}

fun RBuilder.betterRail(handler: RHandler<RailProps>) =
    child(BetterRail, handler = handler)
