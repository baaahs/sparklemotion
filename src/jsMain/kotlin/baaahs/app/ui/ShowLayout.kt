package baaahs.app.ui

import baaahs.app.ui.controls.SpecialControlProps
import baaahs.app.ui.controls.control
import baaahs.show.Layout
import baaahs.show.live.ControlDisplay
import baaahs.show.live.OpenShow
import baaahs.show.mutable.PatchHolderEditContext
import baaahs.ui.*
import external.Direction
import external.draggable
import external.droppable
import external.mosaic.*
import kotlinext.js.jsObject
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElementSerializer
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import react.*
import react.dom.div
import kotlin.reflect.KClass

val ShowLayout = xComponent<ShowLayoutProps>("ShowLayout") { props ->
    val appContext = useContext(appContext)

    val handleCreateNode = useCallback { args: Array<Any> ->
        console.log("ShowLayout:handleCreateNode", args)
    }

    val editModeStyle =
        if (props.editMode) Styles.editModeOn else Styles.editModeOff

//    <MosiacMenuBar />
    mosaic<String> {
        renderTile = { panelTitle, path ->
            MosaicWindow {
                attrs {
                    draggable = false
//                    additionalControls = if (type === "") additionalControls else emptyArray<String>()
                    title = panelTitle
                    createNode = handleCreateNode
                    this.path = path
                    onDragStart = { console.log("MosaicWindow.onDragStart") }
                    onDragEnd = { type -> console.log("MosaicWindow.onDragEnd", type) }
                    renderToolbar = { props: MosaicWindowProps<*>, draggable: Boolean? ->
                        div { +props.title }
                    }
                }

                paper(Styles.layoutPanel and editModeStyle on PaperStyle.root) {
                    props.controlDisplay.render(panelTitle) { dropTargetId: String,
                                                        section: ControlDisplay.Section,
                                                        controls: List<ControlDisplay.PanelBuckets.PanelBucket.PlacedControl> ->

                        droppable({
                            this.droppableId = dropTargetId
                            this.type = "ControlPanel"
                            this.direction = Direction.horizontal.name
                            this.isDropDisabled = !props.editMode
                        }) { droppableProvided, _ ->
                            val style = Styles.controlSections[section.depth]
                            div(+Styles.layoutControls and style) {
                                install(droppableProvided)

                                div(+Styles.controlPanelHelpText) { +section.title }
                                controls.forEachIndexed { index, placedControl ->
                                    val control = placedControl.control
                                    val draggableId = control.id

                                    draggable({
                                        this.key = draggableId
                                        this.draggableId = draggableId
                                        this.isDragDisabled = !props.editMode
                                        this.index = index
                                    }) { draggableProvided, _ ->
                                        control {
                                            attrs.control = control
                                            attrs.specialControlProps = props.specialControlProps
                                            attrs.draggableProvided = draggableProvided
                                        }
                                    }
                                }

                                insertPlaceholder(droppableProvided)
                            }
                        }
                    }

                }
            }
        }

        val jsonInst =
            Json(JsonConfiguration.Stable)
        val layoutRoot = props.layout.rootNode
        val asJson =
            jsonInst.stringify(JsonElementSerializer, layoutRoot)
        val layoutRootJs = JSON.parse<dynamic>(asJson)
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
        value = layoutRootJs as MosaicParent<String>
        //            onChange = { onChange }
//            onRelease = { onRelease }
//            className = "mosaic mosaic-blueprint-theme bp3-dark"
    }
}

external interface ShowLayoutProps : RProps {
    var show: OpenShow
    var onShowStateChange: () -> Unit
    var layout: Layout
    var controlDisplay: ControlDisplay
    var specialControlProps: SpecialControlProps
    var editMode: Boolean
    var editPatchHolder: (PatchHolderEditContext) -> Unit
}

fun RBuilder.showLayout(handler: RHandler<ShowLayoutProps>): ReactElement =
    child(ShowLayout, handler = handler)

fun <T> RBuilder.mosaic(handler: MosaicControlledProps<T>.() -> Unit): ReactElement =
    child(Mosaic::class as KClass<out Component<MosaicControlledProps<T>, *>>) { attrs { handler() } }

fun <T> RBuilder.mosaicWindow(handler: MosaicWindowProps<T>.() -> Unit): ReactElement =
    child(MosaicWindow::class as KClass<out Component<MosaicWindowProps<T>, *>>) { attrs { handler() } }
