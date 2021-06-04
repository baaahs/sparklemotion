package baaahs.app.ui

import baaahs.app.ui.controls.controlWrapper
import baaahs.show.live.ControlDisplay
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenShow
import baaahs.ui.*
import external.Direction
import external.draggable
import external.droppable
import external.react_draggable.Draggable
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import materialui.components.typography.typographyH6
import materialui.icon
import materialui.icons.Icons
import org.w3c.dom.HTMLElement
import react.*
import react.dom.div

val ControlsPalette = xComponent<ControlsPaletteProps>("ControlsPalette") { props ->
    val unplacedControlPaletteDiv = ref<HTMLElement?>()

    val editModeStyle =
        if (props.editMode) Styles.editModeOn else Styles.editModeOff

    Draggable {
        val randomStyleForHandle = "ControlsPaletteHandle"
        attrs.handle = ".$randomStyleForHandle"

        div(+editModeStyle and Styles.unplacedControlsPalette) {
            ref = unplacedControlPaletteDiv

            div(+Styles.dragHandle and randomStyleForHandle) {
                icon(Icons.DragIndicator)
            }

            paper(Styles.unplacedControlsPaper on PaperStyle.root) {
                attrs.elevation = 3

                typographyH6 { +"Unplaced Controls" }

                if (props.editMode) {
                    droppable({
                        this.droppableId = props.controlDisplay.unplacedControlsDropTargetId
                        this.type = props.controlDisplay.unplacedControlsDropTarget.type
                        this.direction = Direction.vertical.name
                        this.isDropDisabled = !props.editMode
                    }) { droppableProvided, _ ->
                        div(+Styles.unplacedControlsDroppable) {
                            install(droppableProvided)

                            props.controlDisplay.relevantUnplacedControls
                                .forEachIndexed { index, unplacedControl ->
                                    val draggableId = "unplaced-${unplacedControl.id}"
                                    draggable({
                                        this.key = draggableId
                                        this.draggableId = draggableId
                                        this.isDragDisabled = !props.editMode
                                        this.index = index
                                    }) { draggableProvided, snapshot ->
                                        if (snapshot.isDragging) {
//                                    // Correct for translated parent.
//                                    unplacedControlPaletteDiv.current?.let {
//                                        val draggableStyle = draggableProvided.draggableProps.asDynamic().style
//                                        draggableStyle.left -= it.offsetLeft
//                                        draggableStyle.top -= it.offsetTop
//                                    }
                                        }

                                        controlWrapper {
                                            attrs.control = unplacedControl
                                            attrs.controlProps = props.controlProps
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
}

external interface ControlsPaletteProps : RProps {
    var controlDisplay: ControlDisplay
    var controlProps: ControlProps
    var show: OpenShow
    var editMode: Boolean
}

fun RBuilder.controlsPalette(handler: RHandler<ControlsPaletteProps>) =
    child(ControlsPalette, handler = handler)