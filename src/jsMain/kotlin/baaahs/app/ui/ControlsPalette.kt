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
import kotlinx.js.jso
import materialui.icon
import mui.material.Paper
import org.w3c.dom.HTMLElement
import react.*
import react.dom.div

val ControlsPalette = xComponent<ControlsPaletteProps>("ControlsPalette") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.showManager.editMode)
    val unplacedControlPaletteDiv = ref<HTMLElement>()

    val editModeStyle =
        if (editMode.isOn) Styles.editModeOn else Styles.editModeOff

    Draggable {
        val randomStyleForHandle = "ControlsPaletteHandle"
        attrs.handle = ".$randomStyleForHandle"

        div(+editModeStyle and Styles.unplacedControlsPalette) {
            ref = unplacedControlPaletteDiv

            div(+Styles.dragHandle and randomStyleForHandle) {
                icon(mui.icons.material.DragIndicator)
            }

            Paper {
                attrs.classes = jso { root = -Styles.unplacedControlsPaper }
                attrs.elevation = 3

                typographyH6 { +"Unplaced Controls" }

                if (editMode.isOn) {
                    droppable({
                        this.droppableId = props.controlDisplay.unplacedControlsDropTargetId
                        this.type = props.controlDisplay.unplacedControlsDropTarget.type
                        this.direction = Direction.vertical.name
                        this.isDropDisabled = !editMode.isOn
                    }) { droppableProvided, _ ->
                        buildElement {
                            div(+Styles.unplacedControlsDroppable) {
                                install(droppableProvided)

                                props.controlDisplay.relevantUnplacedControls
                                    .forEachIndexed { index, unplacedControl ->
                                        val draggableId = "unplaced-${unplacedControl.id}"
                                        draggable({
                                            this.key = draggableId
                                            this.draggableId = draggableId
                                            this.isDragDisabled = !editMode.isOn
                                            this.index = index
                                        }) { draggableProvided, snapshot ->
                                            buildElement {
                                                if (snapshot.isDragging) {
//                                            // Correct for translated parent.
//                                            unplacedControlPaletteDiv.current?.let {
//                                                val draggableStyle = draggableProvided.draggableProps.asDynamic().style
//                                                draggableStyle.left -= it.offsetLeft
//                                                draggableStyle.top -= it.offsetTop
//                                            }
                                                }

                                                controlWrapper {
                                                    attrs.control = unplacedControl
                                                    attrs.controlProps = props.controlProps
                                                    attrs.draggableProvided = draggableProvided
                                                }
                                            }
                                        }
                                    }

                                child(droppableProvided.placeholder)
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface ControlsPaletteProps : Props {
    var controlDisplay: ControlDisplay
    var controlProps: ControlProps
    var show: OpenShow
}

fun RBuilder.controlsPalette(handler: RHandler<ControlsPaletteProps>) =
    child(ControlsPalette, handler = handler)