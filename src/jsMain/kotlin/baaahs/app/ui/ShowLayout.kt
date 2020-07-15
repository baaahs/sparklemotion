package baaahs.app.ui

import baaahs.OpenShow
import baaahs.ShowState
import baaahs.app.ui.controls.ControlDisplay
import baaahs.app.ui.controls.SpecialControlProps
import baaahs.app.ui.controls.control
import baaahs.show.Layout
import baaahs.show.Show
import baaahs.ui.*
import external.Direction
import external.draggable
import external.droppable
import external.mosaic.*
import kotlinext.js.jsObject
import kotlinx.css.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElementSerializer
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import react.*
import react.dom.div
import styled.css
import styled.styledDiv
import kotlin.reflect.KClass

val ShowLayout = xComponent<ShowLayoutProps>("ShowLayout") { props ->
    val appContext = useContext(appContext)
    val handleCreateNode = useCallback { args: Array<Any> ->
        console.log("ShowLayout:handleCreateNode", args)
    }

    val editModeStyle =
        if (props.editMode) Styles.editModeOn else Styles.editModeOff

    var controlDisplay by state<ControlDisplay> { nuffin() }
    onChange("show/state", props.show, props.showState, props.editMode, props.onEdit, appContext.dragNDrop) {
        controlDisplay = ControlDisplay(props.show, props.showState, props.editMode, props.onEdit, appContext.dragNDrop)

        withCleanup {
            controlDisplay.release()
        }
    }

    val specialControlProps = jsObject<SpecialControlProps> {
        this.show = props.show
        this.showState = props.showState
        this.onShowStateChange = props.onShowStateChange
        this.editMode = props.editMode
        this.onEdit = props.onEdit
    }

    styledDiv {
        css {
            width = 100.pct
            height = 100.pct
            display = Display.flex
            flexDirection = FlexDirection.column
            position = Position.absolute
            top = 40.px
            left = 0.px
        }

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
                        controlDisplay.render(panelTitle) {
                                dropTargetId: String,
                                section: ControlDisplay.Section,
                                controls: List<ControlDisplay.PanelBuckets.PanelBucket.PlacedControl> ->

                            droppable({
                                this.droppableId = dropTargetId
                                this.type = "ControlPanel"
                                this.direction = Direction.horizontal.name
                                this.isDropDisabled = !props.editMode
                            }) { droppableProvided, snapshot ->
                                val style = when (section) {
                                    ControlDisplay.Section.Show -> Styles.showControls
                                    ControlDisplay.Section.Scene -> Styles.sceneControls
                                    ControlDisplay.Section.Patch -> Styles.patchControls
                                }
                                div(+Styles.layoutControls and style) {
                                    install(droppableProvided)

                                    div(+Styles.controlPanelHelpText) { +section.title }
                                    controls.forEach { placedControl ->
                                        val control = placedControl.control
                                        val draggableId = "control_${placedControl.id}"

                                        draggable({
                                            this.key = draggableId
                                            this.draggableId = draggableId
                                            this.isDragDisabled = !props.editMode
                                            this.index = placedControl.index
                                        }) { draggableProvided, snapshot ->
                                            control {
                                                attrs.control = control
                                                attrs.specialControlProps = specialControlProps
                                                attrs.draggableProvided = draggableProvided
                                            }
                                        }
                                    }
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
            println("asJson = ${asJson}")
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "UNCHECKED_CAST")
            value = layoutRootJs as MosaicParent<String>
            //            onChange = { onChange }
//            onRelease = { onRelease }
//            className = "mosaic mosaic-blueprint-theme bp3-dark"
        }
    }
}

external interface ShowLayoutProps : RProps {
    var show: OpenShow
    var showState: ShowState
    var onShowStateChange: (ShowState) -> Unit
    var layout: Layout
    var editMode: Boolean
    var onEdit: (Show, ShowState) -> Unit
}

fun RBuilder.showLayout(handler: RHandler<ShowLayoutProps>): ReactElement =
    child(ShowLayout, handler = handler)

fun <T> RBuilder.mosaic(handler: MosaicControlledProps<T>.() -> Unit): ReactElement =
    child(Mosaic::class as KClass<out Component<MosaicControlledProps<T>, *>>) { attrs { handler() } }

fun <T> RBuilder.mosaicWindow(handler: MosaicWindowProps<T>.() -> Unit): ReactElement =
    child(MosaicWindow::class as KClass<out Component<MosaicWindowProps<T>, *>>) { attrs { handler() } }
