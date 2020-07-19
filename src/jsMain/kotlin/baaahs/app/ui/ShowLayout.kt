package baaahs.app.ui

import baaahs.OpenShow
import baaahs.ShowState
import baaahs.app.ui.controls.ControlDisplay
import baaahs.app.ui.controls.ControlDisplay.PanelBuckets.PanelBucket.PlacedControl
import baaahs.app.ui.controls.SpecialControlProps
import baaahs.app.ui.controls.control
import baaahs.show.Layout
import baaahs.show.LayoutNode
import baaahs.show.Show
import baaahs.show.ShowBuilder
import baaahs.ui.*
import external.Direction
import external.draggable
import external.droppable
import external.mosaic.Mosaic
import external.mosaic.MosaicControlledProps
import external.mosaic.MosaicWindow
import external.mosaic.MosaicWindowProps
import kotlinext.js.jsObject
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import react.*
import react.dom.div
import kotlin.reflect.KClass

val ShowLayout = xComponent<ShowLayoutProps>("ShowLayout") { props ->
    val appContext = useContext(appContext)

    val editModeStyle =
        if (props.editMode) Styles.editModeOn else Styles.editModeOff

    var controlDisplay by state<ControlDisplay> { nuffin() }
    onChange("show/state", props.show, props.showState, props.editMode, props.onEdit, appContext.dragNDrop) {
        controlDisplay = ControlDisplay(props.show, props.showState, props.editMode, props.onEdit, appContext.dragNDrop)

        withCleanup {
            controlDisplay.release()
        }
    }

    val showBuilder = ShowBuilder()

    val specialControlProps = jsObject<SpecialControlProps> {
        this.show = props.show
        this.showState = props.showState
        this.onShowStateChange = props.onShowStateChange
        this.editMode = props.editMode
        this.onEdit = props.onEdit
    }

    layoutContainer {
        attrs.node = props.layout.rootNode
        attrs.id = "app-ui-layoutContainer"
        attrs.renderPanel = { layoutPanel: LayoutNode.Panel, layoutClasses: String ->
            paper(layoutClasses and editModeStyle on PaperStyle.root) {
                div(+Styles.title) { +layoutPanel.title }

                controlDisplay.render(layoutPanel.title) { dropTargetId: String,
                                                           section: ControlDisplay.Section,
                                                           controls: List<PlacedControl> ->
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
                                val draggableId = "control_${control.toControlRef(showBuilder).toShortString()}"

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

                            insertPlaceholder(droppableProvided)
                        }
                    }
                }
            }
        }
    }

    controlsPalette {
        attrs.controlDisplay = controlDisplay
        attrs.specialControlProps = specialControlProps
        attrs.show = props.show
        attrs.editMode = props.editMode
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
