package baaahs.app.ui

import baaahs.OpenShow
import baaahs.PubSub
import baaahs.ShowResources
import baaahs.ShowState
import baaahs.gadgets.Slider
import baaahs.glshaders.CorePlugin
import baaahs.jsx.RangeSlider
import baaahs.show.DataSource
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

    val handleEdit = useCallback(props.onChange) { newShow: Show, newShowState: ShowState ->
        props.onChange(newShow, newShowState)
    }

    val layoutRenderers =
        show.layouts.panelNames.associateWith { mutableListOf<GadgetRenderer>() }
    fun getControlRenderer(dataSource: DataSource): GadgetRenderer {
        return {
            when (dataSource.getRenderType()) {
                "SceneList" -> {
                    sceneList {
                        this.show = show
                        this.showState = showState
                        onSelect = { props.onShowStateChange(showState.selectScene(it)) }
                        this.editMode = props.editMode
                        onChange = handleEdit
                    }
                }
                "PatchList" -> {
                    println("Render PatchList with ${show.scenes[showState.selectedScene].patchSets.map { it.title }}")
                    patchSetList {
                        this.show = show
                        this.showState = showState
                        this.showResources = props.showResources
                        onSelect = { props.onShowStateChange(showState.selectPatchSet(it)) }
                        this.editMode = props.editMode
                        onChange = handleEdit
                    }
                }
                "Slider" -> {
                    dataSource as CorePlugin.SliderDataSource
                    RangeSlider {
                        attrs.pubSub = props.pubSub
                        attrs.gadget = Slider(
                            dataSource.title,
                            dataSource.initialValue,
                            dataSource.minValue,
                            dataSource.maxValue,
                            dataSource.stepValue
                        )
                    }
                    if (props.editMode) {
                        +"Edit…"
                    }
                }
            }
            p { +"Control: ${dataSource.getRenderType()}" }
        }
    }

    fun addControlsToPanels(layoutControls: Map<String, List<DataSource>>) {
        layoutControls.forEach { (panelName, dataSources) ->
            dataSources.forEach { dataSource ->
                val panelItems = layoutRenderers[panelName] ?: error("unknown panel $panelName")
                panelItems.add(getControlRenderer(dataSource))
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
    var showResources: ShowResources
    var show: OpenShow
    var showState: ShowState
    var editMode: Boolean
    var onChange: (Show, ShowState) -> Unit
    var onShowStateChange: (ShowState) -> Unit
}

fun RBuilder.showUi(handler: ShowUiProps.() -> Unit): ReactElement =
    child(ShowUi) { attrs { handler() } }
