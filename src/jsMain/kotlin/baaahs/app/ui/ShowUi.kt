package baaahs.app.ui

import baaahs.PubSub
import baaahs.ShowResources
import baaahs.ShowState
import baaahs.gadgets.Slider
import baaahs.jsx.RangeSlider
import baaahs.ports.DataSourceRef
import baaahs.replacing
import baaahs.show.PatchSet
import baaahs.show.Show
import baaahs.ui.GadgetRenderer
import baaahs.ui.gadgets.patchSetList
import baaahs.ui.gadgets.sceneList
import baaahs.ui.showLayout
import baaahs.ui.useCallback
import react.*
import react.dom.p

val ShowUi = functionalComponent<ShowUiProps> { props ->
    val show = props.show
    val showState = props.showState
    val currentLayoutName = "default"
    val currentLayout = show.layouts.map[currentLayoutName] ?: error("no such layout $currentLayoutName")

    val modifyShow = useCallback(show, props.onChange) { handler: (Show) -> Show ->
        props.onChange(handler(show))
    }

    val handleChangePatchSet = useCallback(show, modifyShow, showState) { newPatchSets: List<PatchSet> ->
        val currentScene = show.scenes[showState.selectedScene]
        modifyShow {
            it.copy(
                scenes = show.scenes.replacing(
                    showState.selectedScene,
                    currentScene.copy(patchSets = newPatchSets)
                )
            )
        }
    }

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
                            this.editMode = props.editMode
                        }
                    }
                    "PatchList" -> {
                        println("Render PatchList with ${show.scenes[showState.selectedScene].patchSets.map { it.title }}")
                        patchSetList {
                            this.pubSub = pubSub
                            this.showResources = props.showResources
                            this.patchSets = show.scenes[showState.selectedScene].patchSets
                            this.selected = showState.selectedPatchSet
                            onSelect = { props.onShowStateChange(showState.withPatchSet(it)) }
                            this.editMode = props.editMode
                            onChange = handleChangePatchSet
                        }
                    }
                    "Slider" -> {
                        RangeSlider {
                            attrs.pubSub = props.pubSub
                            attrs.gadget = Slider(dataSourceProvider.id)
                        }
                        if (props.editMode) {
                            +"Edit…"
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
    var showResources: ShowResources
    var showState: ShowState
    var onShowStateChange: (ShowState) -> Unit
    var editMode: Boolean
    var onChange: (Show) -> Unit
}

fun RBuilder.showUi(handler: ShowUiProps.() -> Unit): ReactElement =
    child(ShowUi) { attrs { handler() } }
