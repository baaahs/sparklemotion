package baaahs.app.ui

import baaahs.OpenShow
import baaahs.app.ui.controls.ControlDisplay
import baaahs.app.ui.controls.SpecialControlProps
import baaahs.app.ui.controls.control
import baaahs.show.ShowBuilder
import baaahs.ui.*
import external.Direction
import external.draggable
import external.droppable
import external.react_draggable.Draggable
import materialui.DragIndicator
import materialui.components.paper.enums.PaperStyle
import materialui.components.paper.paper
import materialui.components.portal.portal
import materialui.components.typography.typographyH6
import materialui.icon
import org.w3c.dom.HTMLElement
import react.*
import react.dom.div
import kotlin.random.Random

val ControlsPalette = xComponent<ControlsPaletteProps>("ControlsPalette") { props ->
    val unplacedControlPaletteDiv = ref<HTMLElement>()

    val editModeStyle =
        if (props.editMode) Styles.editModeOn else Styles.editModeOff

    val showBuilder = ShowBuilder()

    portal {
        Draggable {
            val randomStyleForHandle = "handle-${Random.nextInt()}"
            attrs.handle = ".$randomStyleForHandle"

            div(+editModeStyle and Styles.unplacedControlsPalette) {
                ref = unplacedControlPaletteDiv

                div(+Styles.dragHandle and randomStyleForHandle) {
                    icon(DragIndicator)
                }

                paper(Styles.unplacedControlsPaper on PaperStyle.root) {
                    attrs.elevation = 3

                    typographyH6 { +"Unplaced Controls" }

                    droppable({
                        this.droppableId = props.controlDisplay.unplacedControlsDropTargetId
                        this.type = "ControlPanel"
                        this.direction = Direction.vertical.name
                        this.isDropDisabled = !props.editMode
                    }) { droppableProvided, snapshot ->
                        div(+Styles.unplacedControlsDroppable) {
                            install(droppableProvided)

                            props.controlDisplay.renderUnplacedControls { index, unplacedControl ->
                                val draggableId = "unplaced_${unplacedControl.toControlRef(showBuilder).toShortString()}"
                                val key = "unplaced_$index"
                                draggable({
                                    this.key = draggableId
                                    this.draggableId = draggableId
                                    this.isDragDisabled = !props.editMode
                                    this.index = index
                                }) { draggableProvided, snapshot ->
                                    control {
                                        attrs.control = unplacedControl
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
}

external interface ControlsPaletteProps : RProps {
    var controlDisplay: ControlDisplay
    var specialControlProps: SpecialControlProps
    var show: OpenShow
    var editMode: Boolean
}

fun RBuilder.controlsPalette(handler: RHandler<ControlsPaletteProps>) =
    child(ControlsPalette, handler = handler)