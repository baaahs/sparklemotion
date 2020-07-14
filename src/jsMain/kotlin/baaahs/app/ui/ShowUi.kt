package baaahs.app.ui

import baaahs.OpenShow
import baaahs.ShowState
import baaahs.app.ui.controls.PatchSetList
import baaahs.app.ui.controls.SceneList
import baaahs.app.ui.controls.SpecialControlProps
import baaahs.glshaders.CorePlugin
import baaahs.jsx.RangeSlider
import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.show.Show
import baaahs.show.SpecialControl
import baaahs.ui.ControlRenderer
import baaahs.ui.showLayout
import baaahs.ui.xComponent
import external.dragDropContext
import kotlinext.js.jsObject
import materialui.Edit
import materialui.icon
import react.*
import react.dom.b
import react.dom.div

val ShowUi = xComponent<ShowUiProps>("ShowUi") { props ->
    val appContext = useContext(appContext)
    val show = props.show
    val showState = props.showState
    val currentLayoutName = "default"
    val currentLayout = show.layouts.map[currentLayoutName] ?: error("no such layout $currentLayoutName")

    val dragNDrop by state { DragNDrop() }
    if (!props.editMode) dragNDrop.reset()

    val handleEdit = handler("edit", props.onChange) { newShow: Show, newShowState: ShowState ->
        props.onChange(newShow, newShowState)
    }

    val layoutRenderers =
        show.layouts.panelNames.associateWith { LayoutRenderer() }
    fun getControlRenderer(control: Control): ControlRenderer {
        return {
            div {
                when (control) {
                    is SpecialControl -> {
                        val specialControlProps = jsObject<SpecialControlProps> {
                            this.show = show
                            this.showState = showState
                            this.onShowStateChange = { props.onShowStateChange(it) }
                            this.editMode = props.editMode
                            this.dragNDrop = dragNDrop
                            this.onEdit = handleEdit
                        }
                        when (control.pluginRef.resourceName) {
                            "Scenes" -> child(SceneList, specialControlProps)
                            "Patches" -> child(PatchSetList, specialControlProps)
                        }
                    }

                    is DataSource -> {
                        val dataFeed = appContext.showResources.useDataFeed(control)
                        when (control.getRenderType()) {
                            "Slider" -> {
                                RangeSlider {
                                    attrs.gadget = (dataFeed as CorePlugin.GadgetDataFeed).gadget
                                }
                                b { +control.dataSourceName }
                            }
                        }
                    }
                }

                if (props.editMode) {
                    icon(Edit)
                }
            }
        }
    }

    fun addControlsToPanels(
        layoutControls: Map<String, List<Control>>,
        block: LayoutRenderer.() -> MutableList<ControlRenderer>
    ) {
        layoutControls.forEach { (panelName, controls) ->
            controls.forEach { control ->
                val panelItems = layoutRenderers[panelName] ?: error("unknown panel $panelName")
                panelItems.block().add(getControlRenderer(control))
            }
        }
    }

    addControlsToPanels(show.controlLayout) { showControls }
    val scene = showState.findScene(show)
    scene?.let { addControlsToPanels(scene.controlLayout) { sceneControls } }
    val patchSet = showState.findPatchSet(show)
    patchSet?.let { addControlsToPanels(patchSet.controlLayout) { patchControls } }

    dragDropContext({
        onDragEnd = dragNDrop::onDragEnd
    }) {
        showLayout {
            attrs.layout = currentLayout
            attrs.layoutControls = layoutRenderers
        }
    }
}

class LayoutRenderer {
    val showControls = mutableListOf<ControlRenderer>()
    val sceneControls = mutableListOf<ControlRenderer>()
    val patchControls = mutableListOf<ControlRenderer>()
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
