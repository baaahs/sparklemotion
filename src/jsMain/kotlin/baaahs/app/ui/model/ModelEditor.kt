package baaahs.app.ui.model

import baaahs.app.ui.appContext
import baaahs.ui.xComponent
import baaahs.visualizer.Visualizer
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val ModelEditorView = xComponent<ModelEditorProps>("ModelEditor") { props ->
    val appContext = useContext(appContext)

    val visualizer = memo { Visualizer(appContext.webClient.modelProvider, appContext.clock) }
    val visualizerEl = ref<Element>()

    onMount {
        visualizer.facade.container = visualizerEl.current as HTMLDivElement

        withCleanup {
            visualizer.facade.container = null
        }
    }


    div {
        div {
            ref = visualizerEl
        }
    }
}

external interface ModelEditorProps : Props {
}

fun RBuilder.modelEditor(handler: RHandler<ModelEditorProps>) =
    child(ModelEditorView, handler = handler)