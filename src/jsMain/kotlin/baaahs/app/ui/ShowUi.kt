package baaahs.app.ui

import baaahs.OpenShow
import baaahs.ShowState
import baaahs.glshaders.CorePlugin
import baaahs.jsx.RangeSlider
import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.show.Show
import baaahs.show.SpecialControl
import baaahs.ui.GadgetRenderer
import baaahs.ui.gadgets.patchSetList
import baaahs.ui.gadgets.sceneList
import baaahs.ui.showLayout
import baaahs.ui.xComponent
import external.dragDropContext
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
        show.layouts.panelNames.associateWith { mutableListOf<GadgetRenderer>() }
    fun getControlRenderer(control: Control): GadgetRenderer {
        return {
            div {
                when (control) {
                    is SpecialControl -> {

                        when (control.pluginRef.resourceName) {
                            "SceneList" -> {
                                sceneList {
                                    attrs.show = show
                                    attrs.showState = showState
                                    attrs.onSelect = { props.onShowStateChange(showState.selectScene(it)) }
                                    attrs.editMode = props.editMode
                                    attrs.dragNDrop = dragNDrop
                                    attrs.onChange = handleEdit
                                }
                            }
                            "PatchList" -> {
                                patchSetList {
                                    attrs.show = show
                                    attrs.showState = showState
                                    attrs.onSelect = { props.onShowStateChange(showState.selectPatchSet(it)) }
                                    attrs.editMode = props.editMode
                                    attrs.dragNDrop = dragNDrop
                                    attrs.onChange = handleEdit
                                }
                            }
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

    fun addControlsToPanels(layoutControls: Map<String, List<Control>>) {
        layoutControls.forEach { (panelName, controls) ->
            controls.forEach { control ->
                val panelItems = layoutRenderers[panelName] ?: error("unknown panel $panelName")
                panelItems.add(getControlRenderer(control))
            }
        }
    }

    addControlsToPanels(show.controlLayout)
    val scene = showState.findScene(show)
    scene?.let { addControlsToPanels(scene.controlLayout) }
    val patchSet = showState.findPatchSet(show)
    patchSet?.let { addControlsToPanels(patchSet.controlLayout) }

    dragDropContext({
        onDragEnd = dragNDrop::onDragEnd
    }) {
        showLayout {
            attrs.layout = currentLayout
            attrs.layoutControls = layoutRenderers
        }
    }
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
