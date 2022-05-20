package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.preview.ClientPreview
import baaahs.control.OpenVisualizerControl
import baaahs.show.live.ControlProps
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import kotlinx.js.jso
import mui.material.Card
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val VisualizerControlView = xComponent<VisualizerControlProps>("VisualizerControl") { props ->
    val appContext = useContext(appContext)

    val sceneManager = appContext.sceneManager
    observe(sceneManager)
    val model = sceneManager.openScene?.model

    val rootEl = ref<Element>()

    val clientPreview = memo(model) {
        model?.let {
            ClientPreview(it, appContext.showPlayer, appContext.clock, appContext.plugins)
        }
    }

    if (clientPreview != null) {
        clientPreview.visualizer.rotate = props.visualizerControl.rotate
        val visualizer = clientPreview.visualizer
        onMount(visualizer) {
            visualizer.container = rootEl.current as HTMLDivElement

            withCleanup {
                visualizer.container = null
                clientPreview.detach()
            }
        }

        useResizeListener(rootEl) {
            visualizer.resize()
        }
    }

    Card {
        ref = rootEl
        attrs.classes = jso { this.root = -Styles.visualizerCard }

        if (model == null) {
            +"No scene loaded!"
        }
    }
}

external interface VisualizerControlProps : Props {
    var controlProps: ControlProps
    var visualizerControl: OpenVisualizerControl
}

fun RBuilder.visualizerControl(handler: RHandler<VisualizerControlProps>) =
    child(VisualizerControlView, handler = handler)