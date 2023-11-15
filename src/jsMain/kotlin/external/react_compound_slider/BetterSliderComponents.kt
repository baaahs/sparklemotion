package external.react_compound_slider

import baaahs.app.ui.gadgets.slider.HandleProps
import baaahs.ui.mixin
import baaahs.ui.xComponent
import js.core.jso
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
    +react.Children.only(renderedChildren)
}

fun RBuilder.betterHandles(handler: RHandler<HandlesProps>) =
    child(BetterHandles, handler = handler)

val BetterTicks = xComponent<TicksProps>("BetterTicks") { props ->
    Ticks {
        mixin(props)
        attrs.children = { ticksObject ->
            ticksObject.scale = props.scale
            props.children.invoke(ticksObject)
        }
    }
}

fun RBuilder.betterTicks(handler: RHandler<TicksProps>) =
    child(BetterTicks, handler = handler)

val BetterTracks = xComponent<TracksProps>("BetterTracks") { props ->
    // render():
    Tracks {
        mixin(props)
        attrs.children = { tracksObject ->
            // Flip tracks if they're backwards.
            tracksObject.tracks = tracksObject.tracks.map { track ->
                val source = track.source
                val target = track.target
                if (source.value < target.value) {
                    jso {
                        this.id = track.id
                        this.source = target
                        this.target = source
                    }
                } else track
            }.toTypedArray()
            val origGetProps = tracksObject.getTrackProps
            tracksObject.getTrackProps = {
                origGetProps().apply {
                    onMouseDown = null
                    onTouchStart = null
                    onPointerDown = { e ->
                        props.onPointerDown?.invoke(e)
                        autofocus(e)
                        props.emitPointer?.invoke(e, Location.Track, null)
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
                        props.emitPointer?.invoke(e, Location.Rail, null)
                    }
                }
            }
            props.children.invoke(railObject)
        }
    }
}

fun RBuilder.betterRail(handler: RHandler<RailProps>) =
    child(BetterRail, handler = handler)
