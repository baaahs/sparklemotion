package baaahs.app.ui

import baaahs.PubSub
import baaahs.ShowState
import baaahs.gadgets.Slider
import baaahs.jsx.RangeSlider
import baaahs.ports.DataSourceRef
import baaahs.show.Show
import baaahs.ui.GadgetRenderer
import baaahs.ui.gadgets.patchSetList
import baaahs.ui.gadgets.sceneList
import baaahs.ui.showLayout
import react.*
import react.dom.p

val ShowUi = functionalComponent<ShowUiProps> { props ->
    val show = props.show
    val showState = props.showState
    val currentLayoutName = "default"
    val currentLayout = show.layouts.map[currentLayoutName] ?: error("no such layout $currentLayoutName")

    val layoutRenderers =
        show.layouts.panelNames.associateWith { mutableListOf<GadgetRenderer>() }
    val controlRenderers: Map<String, GadgetRenderer> =
        show.dataSources.associate { dataSourceProvider ->
            val renderer: GadgetRenderer = {
                when (dataSourceProvider.getRenderType()) {
                    "SceneList" -> {
                        sceneList {
                            this.pubSub = pubSub
                            this.scenes = show.scenes
                            this.selected = showState.selectedScene
                            onSelect = { props.onShowStateChange(showState.withScene(it)) }
                        }
                    }
                    "PatchList" -> {
                        patchSetList {
                            this.pubSub = pubSub
                            this.patchSets = show.scenes[showState.selectedScene].patchSets
                            this.selected = showState.selectedPatchSet
                            onSelect = { props.onShowStateChange(showState.withPatchSet(it)) }
                        }
                    }
                    "Slider" -> {
                        RangeSlider {
                            attrs.pubSub = props.pubSub
                            attrs.gadget = Slider(dataSourceProvider.id)
                        }
                    }
                }
                p { +"Control: ${dataSourceProvider.getRenderType()}" }
            }
            dataSourceProvider.id to renderer
        }

    fun addControlsToPanels(layoutControls: Map<String, List<DataSourceRef>>) {
        layoutControls.forEach { (panelName, dataSourceRefs) ->
            dataSourceRefs.forEach { dataSourceRef ->
                val panelItems = layoutRenderers[panelName] ?: error("unknown panel $panelName")
                val controlRenderer = controlRenderers[dataSourceRef.id]
                    ?: error("no such control \"${dataSourceRef.id}\" among [${controlRenderers.keys}]")
                panelItems.add(controlRenderer)
            }
        }
    }

    addControlsToPanels(show.controlLayout)
    addControlsToPanels(showState.findPatchSet(show).controlLayout)

    showLayout {
        this.layout = currentLayout
        this.layoutControls = layoutRenderers
    }
}

external interface ShowUiProps : RProps {
    var pubSub: PubSub.Client
    var show: Show
    var showState: ShowState
    var onShowStateChange: (ShowState) -> Unit
}

fun RBuilder.showUi(handler: ShowUiProps.() -> Unit): ReactElement =
    child(ShowUi) { attrs { handler() } }
