package baaahs.ui.gadgets

import baaahs.ShowState
import baaahs.app.ui.icon
import baaahs.show.Show
import baaahs.ui.xComponent
import external.*
import external.Direction
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import kotlinx.html.js.onClickFunction
import materialui.DragHandle
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import materialui.components.card.card
import materialui.toggleButton
import materialui.toggleButtonGroup
import org.w3c.dom.events.Event
import react.RBuilder
import react.RProps
import react.ReactElement
import react.child
import styled.css
import styled.styledDiv

fun <T> eventHandler(block: Event.(T) -> Unit): (Event) -> Unit {
    @Suppress("UNCHECKED_CAST")
    return { event: Event, item: T -> event.block(item) } as (Event) -> Unit
}

val SceneList = xComponent<SceneListProps>("SceneList") { props ->
    val scenes = props.show.scenes

    card {
        dragDropContext({
            onDragEnd = { dropResult: DropResult, responderProvided: ResponderProvided ->
                if (
                    dropResult.reason == DropReason.DROP.name
                    && dropResult.source.droppableId == dropResult.destination!!.droppableId
                ) {
                    val sourceIndex = dropResult.source.index
                    val destIndex = dropResult.destination!!.index

                    val newScenes = scenes.toMutableList().apply {
                        removeAt(sourceIndex).also { add(destIndex, it) }
                    }

                    val newPatchSetSelections = props.showState.patchSetSelections.toMutableList().apply {
                        removeAt(sourceIndex).also { add(destIndex, it) }
                    }
                    props.onChange(
                        props.show.copy(scenes = newScenes),
                        props.showState.withPatchSetSelections(newPatchSetSelections)
                    )
                }
            }
        }) {

            droppable({
                droppableId = "sceneList"
                type = "Scene"
                direction = Direction.horizontal
                isDropDisabled = !props.editMode
            }) { provided, snapshot ->
                toggleButtonGroup {
                    ref = provided.innerRef
                    copyFrom(provided.droppableProps)
                    this.childList.add(provided.placeholder)

                    attrs.variant = ButtonVariant.outlined
                    attrs["exclusive"] = true
//                    attrs["value"] = props.selected // ... but this is busted.
//                    attrs.onChangeFunction = eventHandler { value: Int -> props.onSelect(value) }

                    props.show.scenes.forEachIndexed { index, scene ->
                        draggable({
                            key = "item$index"
                            draggableId = "item$index"
                            isDragDisabled = !props.editMode
                            this.index = index
                        }) { provided, snapshot ->
//                            div {
//                                +"Handle"

                            styledDiv {
                                ref = provided.innerRef
                                css { position = Position.relative }
                                copyFrom(provided.draggableProps)

                                styledDiv {
                                    css {
                                        visibility = if (props.editMode) Visibility.visible else Visibility.hidden
                                        transition(property = "visibility", duration = 0.25.s, timing = Timing.linear)
                                        position = Position.absolute
                                        right = 2.px
                                        top = -2.px
                                        zIndex = 1
                                    }
                                    copyFrom(provided.dragHandleProps)

                                    icon(DragHandle) {}
                                }
                                toggleButton {
                                    attrs["value"] = index
                                    attrs["selected"] = index == props.showState.selectedScene
                                    attrs.onClickFunction = { props.onSelect(index) }

                                    +scene.title
                                }
                            }
                        }

//                            }
                    }

                    if (props.editMode) {
                        button {
                            +"+"
//                attrs.onClickFunction = {  }
                        }
                    }
                }
            }
        }
    }
}

external interface SceneListProps : RProps {
    var show: Show
    var showState: ShowState
    var onSelect: (Int) -> Unit
    var editMode: Boolean
    var onChange: (Show, ShowState) -> Unit
}

fun RBuilder.sceneList(handler: SceneListProps.() -> Unit): ReactElement =
    child(SceneList) { attrs { handler() } }
