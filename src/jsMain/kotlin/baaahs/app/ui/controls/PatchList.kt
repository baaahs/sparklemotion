package baaahs.app.ui.controls

import baaahs.ShowState
import baaahs.app.ui.Draggable
import baaahs.app.ui.DropTarget
import baaahs.app.ui.appContext
import baaahs.show.Show
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShow
import baaahs.ui.*
import external.Direction
import external.copyFrom
import external.draggable
import external.droppable
import kotlinx.html.js.onClickFunction
import materialui.*
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttongroup.enums.ButtonGroupOrientation
import materialui.components.card.card
import org.w3c.dom.events.Event
import react.dom.div
import react.key
import react.useContext

class DraggablePatch(
    private val editor: MutableShow,
    private val sceneIndex: Int,
    private val patchSetIndex: Int,
    private val onChange: (Show, ShowState) -> Unit
) : Draggable {
    lateinit var mutablePatchSet: MutableShow.MutableScene.MutablePatchSet

    init {
        editor.editScene(sceneIndex) {
            editPatchSet(patchSetIndex) {
                mutablePatchSet = this
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
            insertPatchSet(mutablePatchSet, index)
        }
    }

    override fun onMove() {
        onChange(editor.getShow(), editor.getShowState())
    }
}

val PatchSetList = xComponent<SpecialControlProps>("PatchSetList") { props ->
    val appContext = useContext(appContext)
    var mutablePatchHolder by state<MutablePatchHolder?> { null }
    val dropTarget =
        PatchSetListDropTarget(props.show, props.showState, props.onEdit)
    val dropTargetId = appContext.dragNDrop.addDropTarget(dropTarget)
    onChange("unregister drop target") {
        withCleanup {
            appContext.dragNDrop.removeDropTarget(dropTarget)
        }
    }

    val currentScene = props.showState.findScene(props.show)

    val handleEditButtonClick = useCallback(props.show, props.showState) { event: Event, index: Int ->
        props.show.edit(props.showState) {
            props.showState.findSceneEditor(this)?.editPatchSet(index) {
                mutablePatchHolder = this
            }
            event.preventDefault()
        }
    }

    val handleClose = handler("patchHolderEditor.onClose") { mutablePatchHolder = null }

    card {
        droppable({
            droppableId = dropTargetId
            type = "Patch"
            direction = Direction.vertical.name
            isDropDisabled = !props.editMode
        }) { droppableProvided, snapshot ->
            toggleButtonGroup(
                ToggleButtonGroupStyle.root to Styles.verticalButtonList.name
            ) {
                install(droppableProvided)

                attrs.variant = ButtonVariant.outlined
                attrs.orientation = ButtonGroupOrientation.vertical
                attrs["exclusive"] = true
//            attrs["value"] = props.selected // ... but this is busted.
//            attrs.onChangeFunction = eventHandler { value: Int -> props.onSelect(value) }
                currentScene?.patchSets?.forEachIndexed { index, patchSet ->
                    draggable({
                        key = patchSet.id
                        draggableId = patchSet.id
                        isDragDisabled = !props.editMode
                        this.index = index
                    }) { draggableProvided, snapshot ->
                        div(+Styles.controlButton) {
                            ref = draggableProvided.innerRef
                            copyFrom(draggableProvided.draggableProps)

                            div(+Styles.editButton) {
                                attrs.onClickFunction = { event -> handleEditButtonClick(event, index) }

                                icon(Edit)
                            }
                            div(+Styles.dragHandle) {
                                copyFrom(draggableProvided.dragHandleProps)
                                icon(DragIndicator)
                            }

                            toggleButton {
//                attrs.color = ButtonColor.primary
//                (attrs as Tag).disabled = patchSet == props.currentPatchSet
                                attrs["value"] = index
                                attrs["selected"] = index == props.showState.selectedPatchSet
                                attrs.onClickFunction = {
                                    val newState = props.showState.selectPatchSet(index)
                                    props.onShowStateChange(newState)
                                }
                                +patchSet.title
                            }
                        }
                    }
                }

                insertPlaceholder(droppableProvided)

                if (props.editMode) {
                    button {
                        +"+"
                        attrs.onClickFunction = { _: Event ->
                            props.show.edit(props.showState) {
                                props.showState.findSceneEditor(this)?.apply {
                                    addPatchSet("Untitled Patch") {
                                        mutablePatchHolder = this
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    mutablePatchHolder?.let { editor ->
        patchHolderEditor {
            attrs.mutablePatchHolder = editor
            attrs.onApply = {
                props.onEdit(editor.getShow(), editor.getShowState())
                mutablePatchHolder = null
            }
            attrs.onCancel = handleClose
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
