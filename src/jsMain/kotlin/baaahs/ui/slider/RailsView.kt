package baaahs.ui.slider

import baaahs.ui.*
import js.core.jso
import react.*
import react.dom.events.PointerEventHandler

private val RailsView = xComponent<RailsProps>("Rails") { props ->
    val onPointerDown = callback<PointerEventHandler<*>>(props.emitPointer) {
        props.emitPointer?.invoke(it, Location.Rail, null)
    }
    props.renderRail(jso {
        this.onPointerDown = onPointerDown
    })
}

external interface RailsProps : Props {
    var renderRail: ((RailProps) -> ReactNode)
    var emitPointer: EmitPointer?
}

fun RBuilder.rails(handler: RHandler<RailsProps>) =
    child(RailsView, handler = handler)