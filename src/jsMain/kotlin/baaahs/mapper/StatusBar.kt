package baaahs.mapper

import baaahs.MapperStatus
import baaahs.ui.xComponent
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.*
import kotlin.math.min

private val StatusBar = xComponent<StatusBarProps>("StatusBar") { props ->
    props.mapperStatus.let { observe(it) }

    val orderedPanels = props.mapperStatus.orderedPanels

    div("mapperUi-stats") {
        props.mapperStatus.stats?.invoke(this)
    }

    div("mapperUi-message") { +(props.mapperStatus.message ?: "") }

    div("mapperUi-message2") { +(props.mapperStatus.message2 ?: "") }

    div("mapperUi-table") {
        table {
            tr {
                th { +"Panel" }
                th { +"Centroid dist" }
            }

            orderedPanels.subList(0, min(5, orderedPanels.size)).forEach { (visibleSurface, distance) ->
                tr {
                    td { +visibleSurface.modelSurface.name }
                    td { +"$distance" }
                }
            }
        }
    }
}

external interface StatusBarProps : RProps {
    var mapperStatus: MapperStatus
}

fun RBuilder.statusBar(handler: RHandler<StatusBarProps>) =
    child(StatusBar, handler = handler)