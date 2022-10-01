package baaahs.ui.diagnostics

import baaahs.app.ui.appContext
import baaahs.dmx.DmxUniverseListener
import baaahs.ui.components.palette
import baaahs.ui.withMouseEvent
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import baaahs.window
import kotlinx.css.Overflow
import kotlinx.css.overflow
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.useContext
import styled.inlineStyles

val DmxDiagnosticsView = xComponent<DmxDiagnosticsProps>("DmxDiagnostics") { props ->
    val appContext = useContext(appContext)

    var dmxUniverses by state<Map<String, DmxUniverseListener.LastFrame>> { emptyMap() }
    val handleUpdateChannels by handler(appContext.webClient) {
        globalLaunch {
            dmxUniverses = appContext.webClient.listDmxUniverses().universes
        }
    }

    onMount(handleUpdateChannels) {
        handleUpdateChannels()
    }

    palette {
        attrs.title = "Dmx Diagnostics"
        attrs.initialWidth = window.innerWidth / 3
        attrs.initialHeight = window.innerHeight * 2 / 3
        attrs.onClose = props.onClose

        button {
            attrs.onClick = handleUpdateChannels.withMouseEvent()
            +"Update"
        }

        div {
            inlineStyles { overflow = Overflow.scroll }

            if (dmxUniverses.isEmpty()) {
                i { +"No Universes!" }
            } else {
                dmxUniverses.forEach { (id, lastFrame) ->
                    header { +id }
                    pre {
                        +buildString(lastFrame.channels.size * 3) {
                            lastFrame.channels.forEachIndexed { index, v ->
                                val hex = ((v.toInt() or 0x100) and 0x1ff).toString(16).substring(1)
                                append(hex)
                                if (index % 16 == 15) {
                                    append('\n')
                                } else if (index % 4 == 3) {
                                    append(' ')
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface DmxDiagnosticsProps : Props {
    var onClose: (() -> Unit)?
}

fun RBuilder.dmxDiagnostics(handler: RHandler<DmxDiagnosticsProps>) =
    child(DmxDiagnosticsView, handler = handler)