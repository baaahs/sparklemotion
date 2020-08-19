package baaahs.app.ui.controls

import baaahs.ShowState
import baaahs.app.ui.Draggable
import baaahs.app.ui.DropTarget
import baaahs.app.ui.appContext
import baaahs.show.live.OpenShow
import baaahs.show.mutable.EditHandler
import baaahs.ui.*
import external.Direction
import external.copyFrom
import external.draggable
import external.droppable
import kotlinx.html.js.onClickFunction
import materialui.*
import materialui.components.button.enums.ButtonVariant
import materialui.components.card.card
import materialui.components.iconbutton.iconButton
import org.w3c.dom.events.Event
import react.dom.div
import react.key
import react.useContext

val SceneList = xComponent<SpecialControlProps>("SceneList") { props ->
    val appContext = useContext(appContext)
    val dropTarget = SceneListDropTarget(props.show, props.showState, appContext.webClient)
    val dropTargetId = appContext.dragNDrop.addDropTarget(dropTarget)
    onChange("unregister drop target") {
        withCleanup {
            appContext.dragNDrop.removeDropTarget(dropTarget)
        }
    }

    val sceneDropTargets = props.show.scenes.mapIndexed { index, _ ->
        val sceneDropTarget = SceneDropTarget(props.show, index)
        val sceneDropTargetId = appContext.dragNDrop.addDropTarget(sceneDropTarget)
        sceneDropTargetId to sceneDropTarget as DropTarget
    }
    onChange("unregister drop target") {
        withCleanup {
            sceneDropTargets.forEach { (_, sceneDropTarget) ->
                appContext.dragNDrop.removeDropTarget(sceneDropTarget)
            }
        }
    }

    val handleEditButtonClick = useCallback(props.show, props.showState) { event: Event, index: Int ->
        props.show.edit(props.showState) {
            editScene(index) { props.editPatchHolder(this) }
            event.preventDefault()
        }
    }

    card {
        droppable({
            droppableId = dropTargetId
            type = "Scene"
            direction = Direction.horizontal.name
            isDropDisabled = !props.editMode
        }) { sceneDropProvided, _ ->
            toggleButtonGroup(
                ToggleButtonGroupStyle.root to Styles.horizontalButtonList.name
            ) {
                install(sceneDropProvided)

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
                    }) { sceneDragProvided, _ ->
//                            div {
//                                +"Handle"

                        div(+Styles.controlButton) {
                            ref = sceneDragProvided.innerRef
                            copyFrom(sceneDragProvided.draggableProps)

                            div(+Styles.editButton) {
                                attrs.onClickFunction = { event -> handleEditButtonClick(event, index) }

                                icon(Edit)
                            }
                            div(+Styles.dragHandle) {
                                copyFrom(sceneDragProvided.dragHandleProps)
                                icon(DragIndicator)
                            }

                            droppable({
                                droppableId = sceneDropTargets[index].first
                                type = "Patch"
                                direction = Direction.vertical.name
                                isDropDisabled = !props.editMode
                            }) { patchDroppableProvided, _ ->
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

                insertPlaceholder(sceneDropProvided)

                if (props.editMode) {
                    iconButton {
                        icon(AddCircleOutline)
                        attrs.onClickFunction = { _: Event ->
                            props.show.edit(props.showState) {
                                addScene("Untitled Scene") { props.editPatchHolder(this) }
                            }
                        }
                    }
                }
            }
        }
    }
}

private class SceneListDropTarget(
    private val show: OpenShow,
    private val showState: ShowState,
    private val editHandler: EditHandler
) : DropTarget {
    override val type: String get() = "SceneList"
    private val myDraggable = object : Draggable {}

    override fun moveDraggable(fromIndex: Int, toIndex: Int) {
        show.edit(showState) {
            moveScene(fromIndex, toIndex)
        }.also { editor ->
            editHandler.onShowEdit(editor)
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
