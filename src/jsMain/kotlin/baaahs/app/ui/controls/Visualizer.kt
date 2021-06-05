package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.preview.ClientPreview
import baaahs.control.OpenVisualizerControl
import baaahs.jsx.useResizeListener
import baaahs.show.live.ControlProps
import baaahs.ui.on
import baaahs.ui.xComponent
import materialui.components.card.card
import materialui.components.paper.enums.PaperStyle
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.*
import react.dom.i

val Visualizer = xComponent<VisualizerProps>("Visualizer") { props ->
    val appContext = useContext(appContext)

    try {
        val clientPreview by state {
            ClientPreview(appContext.webClient.model, appContext.showPlayer, appContext.clock)
        }

        val rootEl = ref<Element>()
        clientPreview.visualizer.rotate = props.visualizerControl.rotate

        val visualizer = clientPreview.visualizer
        onMount {
            visualizer.container = rootEl.current as HTMLDivElement

            withCleanup {
                visualizer.container = null
                clientPreview.detach()
            }
        }

        useResizeListener(rootEl) {
            visualizer.resize()
        }

        card(Styles.visualizerCard on PaperStyle.root) {
            ref = rootEl
        }
    } catch (e: Exception) {
        card(Styles.visualizerCard on PaperStyle.root) {
            i { +"Not supported on iOS yet :-(" }
        }
    }
}

external interface VisualizerProps : RProps {
    var controlProps: ControlProps
    var visualizerControl: OpenVisualizerControl
}

fun RBuilder.visualizer(handler: RHandler<VisualizerProps>) =
    child(Visualizer, handler = handler)