package baaahs.app.ui

import baaahs.OpenShow
import baaahs.ShowState
import baaahs.app.ui.controls.SpecialControlProps
import baaahs.show.Control
import baaahs.show.Show
import baaahs.ui.xComponent
import external.dragDropContext
import kotlinext.js.jsObject
import react.*

val ShowUi = xComponent<ShowUiProps>("ShowUi") { props ->
    val appContext = useContext(appContext)
    val show = props.show
    val showState = props.showState
    val currentLayoutName = "default"
    val currentLayout = show.layouts.map[currentLayoutName] ?: error("no such layout $currentLayoutName")

    if (!props.editMode) appContext.dragNDrop.reset()

    val handleEdit = handler("edit", props.onChange) { newShow: Show, newShowState: ShowState ->
        props.onChange(newShow, newShowState)
    }

    val panelControls =
        show.layouts.panelNames.associateWith { PanelControls() }

    fun addControlsToPanels(
        layoutControls: Map<String, List<Control>>,
        block: PanelControls.() -> MutableList<Control>
    ) {
        layoutControls.forEach { (panelName, controls) ->
            controls.forEach { control ->
                val panelItems = panelControls[panelName] ?: error("unknown panel $panelName")
                panelItems.block().add(control)
            }
        }
    }

    addControlsToPanels(show.controlLayout) { showControls }
    val scene = showState.findScene(show)
    scene?.let { addControlsToPanels(scene.controlLayout) { sceneControls } }
    val patchSet = showState.findPatchSet(show)
    patchSet?.let { addControlsToPanels(patchSet.controlLayout) { patchControls } }

    val specialControlProps = jsObject<SpecialControlProps> {
        this.show = show
        this.showState = showState
        this.onShowStateChange = { props.onShowStateChange(it) }
        this.editMode = props.editMode
        this.onEdit = handleEdit
    }

    dragDropContext({
        onDragEnd = appContext.dragNDrop::onDragEnd
    }) {
        showLayout {
            attrs.layout = currentLayout
            attrs.panelControls = panelControls
            attrs.specialControlProps = specialControlProps
            attrs.editMode = props.editMode
        }
    }
}

class PanelControls {
    val showControls = mutableListOf<Control>()
    val sceneControls = mutableListOf<Control>()
    val patchControls = mutableListOf<Control>()
}


external interface ShowUiProps : RProps {
    var show: OpenShow
    var showState: ShowState
    var editMode: Boolean
    var onChange: (Show, ShowState) -> Unit
    var onShowStateChange: (ShowState) -> Unit
}

fun RBuilder.showUi(handler: RHandler<ShowUiProps>): ReactElement =
    child(ShowUi, handler = handler)
