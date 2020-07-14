package baaahs.ui.gadgets

import baaahs.OpenShow
import baaahs.ShowState
import baaahs.app.ui.Draggable
import baaahs.app.ui.DropTarget
import baaahs.show.PatchyEditor
import baaahs.show.Show
import baaahs.ui.getName
import baaahs.ui.patchyEditor
import baaahs.ui.xComponent
import external.Direction
import external.copyFrom
import external.draggable
import external.droppable
import kotlinx.css.*
import kotlinx.css.properties.Timing
import kotlinx.css.properties.s
import kotlinx.css.properties.transition
import kotlinx.html.js.onClickFunction
import materialui.*
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import materialui.components.card.card
import org.w3c.dom.events.Event
import react.key
import styled.css
import styled.styledDiv

val SceneList = xComponent<SpecialControlProps>("SceneList") { props ->
    var patchyEditor by state<PatchyEditor?> { null }
    val dropTarget = SceneListDropTarget(props.show, props.showState, props.onEdit)
    val dropTargetId = props.dragNDrop.addDropTarget(dropTarget)
    onChange("unregister drop target") {
        withCleanup {
            props.dragNDrop.removeDropTarget(dropTarget)
        }
    }

    val sceneDropTargets = props.show.scenes.mapIndexed { index, _ ->
        val sceneDropTarget = SceneDropTarget(props.show, index)
        val sceneDropTargetId = props.dragNDrop.addDropTarget(sceneDropTarget)
        sceneDropTargetId to sceneDropTarget as DropTarget
    }
    onChange("unregister drop target") {
        withCleanup {
            sceneDropTargets.forEach { (_, sceneDropTarget) ->
                props.dragNDrop.removeDropTarget(sceneDropTarget)
            }
        }
    }

    val handleClose = handler("patchyEditor.onClose") { patchyEditor = null }

    card {
        droppable({
            droppableId = dropTargetId
            type = "Scene"
            direction = Direction.horizontal.name
            isDropDisabled = !props.editMode
        }) { sceneDropProvided, snapshot ->
            toggleButtonGroup(
                ToggleButtonGroupStyle.root to Styles.horizontalButtonList.getName()
            ) {
                ref = sceneDropProvided.innerRef
                copyFrom(sceneDropProvided.droppableProps)
                this.childList.add(sceneDropProvided.placeholder)

                attrs.variant = ButtonVariant.outlined
                attrs["exclusive"] = true
//                    attrs["value"] = props.selected // ... but this is busted.
//                    attrs.onChangeFunction = eventHandler { value: Int -> props.onSelect(value) }

                props.show.scenes.forEachIndexed { index, scene ->
                    draggable({
                        key = scene.id
                        draggableId = scene.id
                        isDragDisabled = !props.editMode
                        this.index = index
                    }) { sceneDragProvided, snapshot ->
//                            div {
//                                +"Handle"

                        styledDiv {
                            ref = sceneDragProvided.innerRef
                            css { position = Position.relative }
                            copyFrom(sceneDragProvided.draggableProps)

                            styledDiv {
                                css {
                                    visibility = if (props.editMode) Visibility.visible else Visibility.hidden
                                    transition(property = "visibility", duration = 0.25.s, timing = Timing.linear)
                                    position = Position.absolute
                                    right = 2.px
                                    top = -2.px
                                    zIndex = 1
                                }
                                copyFrom(sceneDragProvided.dragHandleProps)

                                icon(DragHandle)
                            }
                            droppable({
                                droppableId = sceneDropTargets[index].first
                                type = "Patch"
                                direction = Direction.vertical.name
                                isDropDisabled = !props.editMode
                            }) { patchDroppableProvided, snapshot ->
                                toggleButton {
                                    ref = patchDroppableProvided.innerRef
                                    copyFrom(patchDroppableProvided.droppableProps)

                                    attrs["value"] = index
                                    attrs["selected"] = index == props.showState.selectedScene
                                    attrs.onClickFunction = {
                                        val newState = props.showState.selectScene(index)
                                        props.onShowStateChange(newState)
                                    }

                                    +scene.title
                                }
                            }
                        }
                    }

//                            }
                }

                if (props.editMode) {
                    button {
                        +"+"
                        attrs.onClickFunction = { _: Event ->
                            props.show.edit(props.showState) {
                                addScene("Untitled Scene") {
                                    patchyEditor = this
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    patchyEditor?.let { editor ->
        patchyEditor {
            attrs.editor = editor
            attrs.onSave = {
                props.onEdit(editor.getShow(), editor.getShowState())
                patchyEditor = null
            }
            attrs.onCancel = handleClose
        }
    }
}

private class SceneListDropTarget(
    private val show: OpenShow,
    private val showState: ShowState,
    private val onChange: (Show, ShowState) -> Unit
) : DropTarget {
    override val type: String get() = "SceneList"
    private val myDraggable = object : Draggable {}

    override fun moveDraggable(fromIndex: Int, toIndex: Int) {
        show.edit(showState) {
            moveScene(fromIndex, toIndex)
        }.also { editor ->
            onChange(editor.getShow(), editor.getShowState())
        }
    }

    override fun willAccept(draggable: Draggable): Boolean {
        return draggable == myDraggable
    }

    // Scenes can only be moved within a single SceneList.
    override fun getDraggable(index: Int): Draggable = error("not implemented")

    // Scenes can only be moved within a single SceneList.
    override fun insertDraggable(draggable: Draggable, index: Int): Unit = error("not implemented")

    // Scenes can only be moved within a single SceneList.
    override fun removeDraggable(draggable: Draggable): Unit = error("not implemented")

}

private class SceneDropTarget(
    private val show: OpenShow,
    private val sceneIndex: Int
) : DropTarget {
    override val type: String get() = "Scene"

    override fun moveDraggable(fromIndex: Int, toIndex: Int): Unit = error("not implemented")

    override fun willAccept(draggable: Draggable): Boolean {
        return draggable is DraggablePatch
    }

    override fun getDraggable(index: Int): Draggable = error("not implemented")

    override fun insertDraggable(draggable: Draggable, index: Int) {
        draggable as DraggablePatch
        val scene = show.scenes[sceneIndex]
        draggable.addTo(sceneIndex, scene.patchSets.size)
    }

    override fun removeDraggable(draggable: Draggable): Unit = error("not implemented")
}
