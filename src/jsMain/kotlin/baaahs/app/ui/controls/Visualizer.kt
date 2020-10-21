package baaahs.app.ui.controls

import baaahs.app.ui.AppContext
import baaahs.app.ui.ControlEditIntent
import baaahs.app.ui.appContext
import baaahs.jsx.useResizeListener
import baaahs.show.live.ControlProps
import baaahs.show.live.ControlView
import baaahs.show.live.OpenControl
import baaahs.show.live.OpenVisualizerControl
import baaahs.ui.on
import baaahs.ui.xComponent
import baaahs.visualizer.SurfaceGeometry
import materialui.components.card.card
import materialui.components.paper.enums.PaperStyle
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.*

class VisualizerControlView(val openControl: OpenVisualizerControl) : ControlView {
    override fun <P : ControlProps<in OpenControl>> getReactElement(): FunctionalComponent<P> {
        return Visualizer.unsafeCast<FunctionalComponent<P>>()
    }

    override fun onEdit(appContext: AppContext) {
        appContext.openEditor(ControlEditIntent(openControl.id))
    }
}

val Visualizer = xComponent<VisualizerProps>("Visualizer") { props ->
    val appContext = useContext(appContext)

    val rootEl = ref<Element>()
    val visualizer by state {
        val model = appContext.webClient.model
        baaahs.visualizer.Visualizer(model)
            .also { viz ->
                model.allSurfaces.forEach { surface ->
                    val vizSurface = viz.addSurface(SurfaceGeometry(surface))
                    // TODO: Bind this to renderer output.
                }

            }
            .facade }
    visualizer.rotate = props.control.visualizerControl.rotate

    onMount {
        visualizer.container = rootEl.current as HTMLDivElement
        withCleanup {
            visualizer.container = null
        }
    }

    useResizeListener(rootEl) {
        visualizer.resize()
    }

    card(Styles.visualizerCard on PaperStyle.root) {
        ref = rootEl
    }
}

external interface VisualizerProps : ControlProps<OpenVisualizerControl>

fun RBuilder.Visualizer(handler: RHandler<VisualizerProps>) =
    child(Visualizer, handler = handler)