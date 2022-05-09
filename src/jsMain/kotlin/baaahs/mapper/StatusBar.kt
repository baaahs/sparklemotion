package baaahs.mapper

import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.useContext
import kotlin.math.min

private val StatusBar = xComponent<StatusBarProps>("StatusBar") { props ->
    val appContext = useContext(mapperAppContext)
    val styles = appContext.allStyles.mapper

    props.mapperStatus.let { observe(it) }

    val orderedPanels = props.mapperStatus.orderedPanels

    div(+styles.stats) {
        props.mapperStatus.stats?.invoke(this)
    }

    div(+styles.message) { +(props.mapperStatus.message ?: "") }

    div(+styles.message2) { +(props.mapperStatus.message2 ?: "") }

    if (orderedPanels.isNotEmpty()) {
        div(+styles.table) {
            table {
                thead {
                    tr {
                        th { +"Panel" }
                        th { +"Centroid dist" }
                    }
                }

                tbody {
                    orderedPanels.subList(0, min(5, orderedPanels.size)).forEach { (visibleSurface, distance) ->
                        tr {
                            td { +visibleSurface.entity.name }
                            td { +"$distance" }
                        }
                    }
                }
            }
        }
    }
}

external interface StatusBarProps : Props {
    var mapperStatus: MapperStatus
}

fun RBuilder.statusBar(handler: RHandler<StatusBarProps>) =
    child(StatusBar, handler = handler)