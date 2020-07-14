package baaahs.ui.gadgets

import baaahs.OpenShow
import baaahs.ShowState
import baaahs.app.ui.DragNDrop
import baaahs.app.ui.Draggable
import baaahs.app.ui.DropTarget
import baaahs.show.PatchyEditor
import baaahs.show.Show
import baaahs.show.ShowEditor
import baaahs.ui.getName
import baaahs.ui.patchyEditor
import baaahs.ui.useCallback
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
import kotlinx.html.js.onContextMenuFunction
import materialui.*
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttongroup.enums.ButtonGroupOrientation
import materialui.components.card.card
import org.w3c.dom.events.Event
import react.*
import styled.css
import styled.styledDiv

class DraggablePatch(
    private val editor: ShowEditor,
    private val sceneIndex: Int,
    private val patchSetIndex: Int,
    private val onChange: (Show, ShowState) -> Unit
) : Draggable {
    lateinit var patchSetEditor: ShowEditor.SceneEditor.PatchSetEditor

    init {
        editor.editScene(sceneIndex) {
            editPatchSet(patchSetIndex) {
                patchSetEditor = this
            }
        }
    }

    fun remove() {
        editor.editScene(sceneIndex) {
            removePatchSet(patchSetIndex)
        }
    }

    fun addTo(sceneIndex: Int, index: Int) {
        editor.editScene(sceneIndex) {
            insertPatchSet(patchSetEditor, index)
        }
    }

    override fun onMove() {
        onChange(editor.getShow(), editor.getShowState())
    }
}

val PatchSetList = xComponent<PatchSetListProps>("PatchSetList") { props ->
    var patchyEditor by state<PatchyEditor?> { null }
    val dropTarget = PatchSetListDropTarget(props.show, props.showState, props.onChange)
    val dropTargetId = props.dragNDrop.addDropTarget(dropTarget)
    onChange("unregister drop target") {
        withCleanup {
            props.dragNDrop.removeDropTarget(dropTarget)
        }
    }

    val selectedScene = props.showState.selectedScene
    val patchSets = props.show.scenes[selectedScene].patchSets

    val handleContextClick = useCallback(props.show, props.showState) { event: Event, index: Int ->
        props.show.edit(props.showState) {
            editScene(selectedScene) {
                editPatchSet(index) {
                    patchyEditor = this
                }
            }
            event.preventDefault()
        }
    }

    val handleOnClose = handler("patchyEditor.onClose") { patchyEditor = null }

    card {
        droppable({
            droppableId = dropTargetId
            type = "Patch"
            direction = Direction.vertical.name
            isDropDisabled = !props.editMode
        }) { droppableProvided, snapshot ->
            toggleButtonGroup(
                ToggleButtonGroupStyle.root to Styles.verticalButtonList.getName()
            ) {
                ref = droppableProvided.innerRef
                copyFrom(droppableProvided.droppableProps)
                this.childList.add(droppableProvided.placeholder)

                attrs.variant = ButtonVariant.outlined
                attrs.orientation = ButtonGroupOrientation.vertical
                attrs["exclusive"] = true
//            attrs["value"] = props.selected // ... but this is busted.
//            attrs.onChangeFunction = eventHandler { value: Int -> props.onSelect(value) }
                patchSets.forEachIndexed { index, patchSet ->
                    draggable({
                        key = patchSet.id
                        draggableId = patchSet.id
                        isDragDisabled = !props.editMode
                        this.index = index
                    }) { draggableProvided, snapshot ->
                        styledDiv {
                            ref = draggableProvided.innerRef
                            css { position = Position.relative }
                            copyFrom(draggableProvided.draggableProps)

                            styledDiv {
                                css {
                                    visibility = if (props.editMode) Visibility.visible else Visibility.hidden
                                    transition(property = "visibility", duration = 0.25.s, timing = Timing.linear)
                                    position = Position.absolute
                                    right = 2.px
                                    top = -2.px
                                    zIndex = 1
                                }
                                copyFrom(draggableProvided.dragHandleProps)

                                icon(DragHandle)
                            }

                            toggleButton {
//                attrs.color = ButtonColor.primary
//                (attrs as Tag).disabled = patchSet == props.currentPatchSet
                                attrs["value"] = index
                                attrs["selected"] = index == props.showState.selectedPatchSet
                                attrs.onClickFunction = { props.onSelect(index) }
                                if (props.editMode) {
                                    attrs.onContextMenuFunction = { event: Event -> handleContextClick(event, index) }
                                }
                                +patchSet.title
                            }
                        }
                    }
                }

                if (props.editMode) {
                    button {
                        +"+"
                        attrs.onClickFunction = { _: Event ->
                            props.show.edit(props.showState) {
                                editScene(selectedScene) {
                                    addPatchSet("Untitled Patch") {
                                        patchyEditor = this
                                    }
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
                props.onChange(editor.getShow(), editor.getShowState())
                patchyEditor = null
            }
            attrs.onCancel = handleOnClose
        }
    }
}

private class PatchSetListDropTarget(
    private val show: OpenShow,
    private val showState: ShowState,
    private val onChange: (Show, ShowState) -> Unit
) : DropTarget {
    override val type: String get() = "PatchList"

    override fun moveDraggable(fromIndex: Int, toIndex: Int) {
        show.edit(showState) {
            editScene(showState.selectedScene) {
                movePatchSet(fromIndex, toIndex)
            }
        }.also { editor ->
            onChange(editor.getShow(), editor.getShowState())
        }
    }

    override fun willAccept(draggable: Draggable): Boolean {
        return draggable is DraggablePatch
    }

    override fun getDraggable(index: Int): Draggable {
        return DraggablePatch(
            show.edit(showState),
            showState.selectedScene,
            index,
            onChange
        )
    }

    override fun insertDraggable(draggable: Draggable, index: Int) {
        draggable as DraggablePatch
        draggable.addTo(showState.selectedScene, index)
    }

    override fun removeDraggable(draggable: Draggable) {
        draggable as DraggablePatch
        draggable.remove()
    }
}

external interface PatchSetListProps : RProps {
    var show: OpenShow
    var showState: ShowState
    var onSelect: (Int) -> Unit
    var editMode: Boolean
    var dragNDrop: DragNDrop
    var onChange: (Show, ShowState) -> Unit
}

fun RBuilder.patchSetList(handler: RHandler<PatchSetListProps>): ReactElement =
    child(PatchSetList, handler = handler)
