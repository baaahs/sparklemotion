package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.preview.ClientPreview
import baaahs.control.OpenVisualizerControl
import baaahs.show.live.ControlProps
import baaahs.ui.on
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import materialui.components.card.card
import materialui.components.paper.enums.PaperStyle
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.i
import react.useContext

val Visualizer = xComponent<VisualizerProps>("Visualizer") { props ->
    val appContext = useContext(appContext)

    try {
        val clientPreview by state {
            ClientPreview(appContext.webClient.model, appContext.showPlayer, appContext.clock, appContext.plugins)
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
        logger.error(e) { "Error rendering Visualizer control." }
        card(Styles.visualizerCard on PaperStyle.root) {
            i { +"Not supported on iOS yet :-(" }
        }
    }
}

external interface VisualizerProps : Props {
    var controlProps: ControlProps
    var visualizerControl: OpenVisualizerControl
}

fun RBuilder.visualizer(handler: RHandler<VisualizerProps>) =
    child(Visualizer, handler = handler)