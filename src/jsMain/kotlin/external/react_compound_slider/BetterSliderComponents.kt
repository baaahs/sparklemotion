package external.react_compound_slider

import baaahs.app.ui.gadgets.slider.HandleProps
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
    val scale = props.scale
    val ticks = memo(props.values, scale, props.count) {
        (props.values ?: scale.getTicks(props.count)).map { value ->
            jso<SliderItem> {
                id = "$$-$value"
                this.value = value
                percent = scale.getValue(value)
            }
        }.toTypedArray()
    }

    val ticksObject = jso<TicksObject> {
        this.scale = props.scale
        this.ticks = ticks
        activeHandleId = props.activeHandleId
        getEventData = props.getEventData

    }

    +react.Children.only(props.children.invoke(ticksObject))
}

fun RBuilder.betterTicks(handler: RHandler<TicksProps>) =
    child(BetterTicks, handler = handler)

val BetterTracks = xComponent<TracksProps>("BetterTracks") { props ->
    val domain = props.scale?.domain ?: 0.0..1.0
    val handles = props.handles ?: emptyArray()
    val left = props.left != false
    val right = props.right != false

    val tracks = memo(left, right, domain, handles) {
        buildList<TrackItem> {
            for (i in 0 until handles.size + 1) {
                val source = when {
                    i == 0 && left -> jso { id = "$"; value = domain.start; percent = 0.0 }
                    i == 0 -> null
                    else -> handles[i - 1]
                }

                val target = when {
                    i == handles.size && right -> jso { id = "$"; value = domain.endInclusive; percent = 100.0 }
                    i == handles.size -> null
                    else -> handles[i]
                }

                if (source != null && target != null) {
                    add(jso {
                        this.id = "${source.id}-${target.id}"
                        this.source = source
                        this.target = target
                    })
                }
            }
        }.toTypedArray()
    }

    val getTrackProps = callback(props.onPointerDown, props.emitPointer) {
        jso<TracksProps> {
            onPointerDown = { e ->
                props.onPointerDown?.invoke(e)
                props.emitPointer?.invoke(e, Location.Track, null)
            }
        }
    }

    // render():
    tracks.forEach { track ->
        val tracksObject = jso<TracksObject> {
            this.tracks = tracks
            this.activeHandleId = props.activeHandleId
            this.getEventData = props.getEventData
            this.getTrackProps = getTrackProps
        }

        +react.Children.only(props.children.invoke(tracksObject))
    }
}

fun RBuilder.betterTracks(handler: RHandler<TracksProps>) =
    child(BetterTracks, handler = handler)

val BetterRail = xComponent<RailProps>("BetterRail") { props ->
    // render():
    val railObject = jso<RailObject> {
        getRailProps = {
            jso {
                onPointerDown = { e ->
                    props.onPointerDown?.invoke(e)
                    props.emitPointer?.invoke(e, Location.Rail, null)
                }
            }
        }
    }
    +react.Children.only(props.children.invoke(railObject))
}

fun RBuilder.betterRail(handler: RHandler<RailProps>) =
    child(BetterRail, handler = handler)
