package baaahs.app.ui

import baaahs.app.ui.controls.GenericControlProps
import baaahs.app.ui.controls.controlWrapper
import baaahs.show.Layout
import baaahs.show.live.ControlDisplay
import baaahs.show.live.OpenShow
import baaahs.show.mutable.PatchHolderEditContext
import baaahs.ui.*
import external.Direction
import external.draggable
import external.droppable
import external.mosaic.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import react.*
import react.dom.div
import kotlin.reflect.KClass

val ShowLayout = xComponent<ShowLayoutProps>("ShowLayout") { props ->
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
                    props.controlDisplay.render(panelTitle) { panelBucket ->
                        droppable({
                            this.droppableId = panelBucket.dropTargetId
                            this.type = panelBucket.type
                            this.direction = Direction.horizontal.name
                            this.isDropDisabled = !props.editMode
                        }) { droppableProvided, _ ->
                            val style = Styles.controlSections[panelBucket.section.depth]
                            div(+Styles.layoutControls and style) {
                                install(droppableProvided)

                                div(+Styles.controlPanelHelpText) { +panelBucket.section.title }
                                panelBucket.controls.forEachIndexed { index, placedControl ->
                                    val control = placedControl.control
                                    val draggableId = control.id

                                    draggable({
                                        this.key = draggableId
                                        this.draggableId = draggableId
                                        this.isDragDisabled = !props.editMode
                                        this.index = index
                                    }) { draggableProvided, _ ->
                                        controlWrapper {
                                            attrs.control = control
                                            attrs.genericControlProps = props.genericControlProps
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

        val jsonInst = Json
        val layoutRoot = props.layout.rootNode
        val asJson = jsonInst.encodeToString(JsonElement.serializer(), layoutRoot)
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
    var genericControlProps: GenericControlProps
    var editMode: Boolean
    var editPatchHolder: (PatchHolderEditContext) -> Unit
}

fun RBuilder.showLayout(handler: RHandler<ShowLayoutProps>): ReactElement =
    child(ShowLayout, handler = handler)

fun <T> RBuilder.mosaic(handler: MosaicControlledProps<T>.() -> Unit): ReactElement =
    child(Mosaic::class as KClass<out Component<MosaicControlledProps<T>, *>>) { attrs { handler() } }

fun <T> RBuilder.mosaicWindow(handler: MosaicWindowProps<T>.() -> Unit): ReactElement =
    child(MosaicWindow::class as KClass<out Component<MosaicWindowProps<T>, *>>) { attrs { handler() } }
