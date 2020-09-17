package baaahs.app.ui.controls

import baaahs.app.ui.Draggable
import baaahs.show.mutable.MutableShow
import baaahs.ui.xComponent

//class DraggablePatch(
//    private val editor: MutableShow,
//    private val sceneIndex: Int,
//    private val patchSetIndex: Int,
//    private val onEdit: () -> Unit
//) : Draggable {
//    lateinit var mutablePatchSet: MutableShow.MutableScene.MutablePatchSet
//
//    init {
//        editor.editScene(sceneIndex) {
//            editPatchSet(patchSetIndex) {
//                mutablePatchSet = this
//            }
//        }
//    }
//
//    fun remove() {
//        editor.editScene(sceneIndex) {
//            removePatchSet(patchSetIndex)
//        }
//    }
//
//    fun addTo(sceneIndex: Int, index: Int) {
//        editor.editScene(sceneIndex) {
//            insertPatchSet(mutablePatchSet, index)
//        }
//    }
//
//    override fun onMove() {
//        onEdit()
//    }
//}

val PatchSetList = xComponent<SpecialControlProps>("PatchSetList") { props ->
//    val appContext = useContext(appContext)
//    val dropTarget =
//        PatchSetListDropTarget(props.show, props.showState, appContext.webClient)
//
//    val dropTargetId = appContext.dragNDrop.addDropTarget(dropTarget)
//    onChange("unregister drop target") {
//        withCleanup {
//            appContext.dragNDrop.removeDropTarget(dropTarget)
//        }
//    }
//
//    val currentScene = props.showState.findScene(props.show)
//
//    val handleEditButtonClick = useCallback(props.show, props.showState) { event: Event, index: Int ->
//        props.show.edit {
//            val mutableShow = this
//            props.showState.findMutableScene(this)?.editPatchSet(index) {
//                props.editPatchHolder(PatchHolderEditContext(mutableShow, this))
//            }
//            event.preventDefault()
//        }
//    }
//
//    card {
//        droppable({
//            droppableId = dropTargetId
//            type = "Patch"
//            direction = Direction.vertical.name
//            isDropDisabled = !props.editMode
//        }) { droppableProvided, _ ->
//            toggleButtonGroup(
//                ToggleButtonGroupStyle.root to Styles.verticalButtonList.name
//            ) {
//                install(droppableProvided)
//
//                attrs.variant = ButtonVariant.outlined
//                attrs.orientation = ButtonGroupOrientation.vertical
//                attrs["exclusive"] = true
////            attrs["value"] = props.selected // ... but this is busted.
////            attrs.onChangeFunction = eventHandler { value: Int -> props.onSelect(value) }
//                currentScene?.patchSets?.forEachIndexed { index, patchSet ->
//                    draggable({
//                        key = patchSet.id
//                        draggableId = patchSet.id
//                        isDragDisabled = !props.editMode
//                        this.index = index
//                    }) { draggableProvided, _ ->
//                        div(+Styles.controlButton) {
//                            ref = draggableProvided.innerRef
//                            copyFrom(draggableProvided.draggableProps)
//
//                            div(+Styles.editButton) {
//                                attrs.onClickFunction = { event -> handleEditButtonClick(event, index) }
//
//                                icon(Edit)
//                            }
//                            div(+Styles.dragHandle) {
//                                copyFrom(draggableProvided.dragHandleProps)
//                                icon(DragIndicator)
//                            }
//
//                            toggleButton {
////                attrs.color = ButtonColor.primary
////                (attrs as Tag).disabled = patchSet == props.currentPatchSet
//                                attrs["value"] = index
//                                attrs["selected"] = index == props.showState.selectedPatchSet
//                                attrs.onClickFunction = {
//                                    val newState = props.showState.selectPatchSet(index)
//                                    props.onShowStateChange(newState)
//                                }
//                                +patchSet.title
//                            }
//                        }
//                    }
//                }
//
//                insertPlaceholder(droppableProvided)
//
//                if (props.editMode) {
//                    iconButton {
//                        icon(AddCircleOutline)
//                        attrs.onClickFunction = { _: Event ->
//                            props.show.edit() {
//                                val mutableShow = this
//                                props.showState.findMutableScene(this)?.apply {
//                                    addPatchSet("Untitled Patch") {
//                                        props.editPatchHolder(PatchHolderEditContext(mutableShow, this))
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}

//private class PatchSetListDropTarget(
//    private val show: OpenShow,
//    private val showState: ShowState,
//    private val editHandler: EditHandler
//) : DropTarget {
//    override val type: String get() = "PatchList"
//
//    override fun moveDraggable(fromIndex: Int, toIndex: Int) {
//        show.edit() {
//            editScene(showState.selectedScene) {
//                movePatchSet(fromIndex, toIndex)
//            }
//        }.also { editor ->
//            editHandler.onShowEdit(editor)
//        }
//    }
//
//    override fun willAccept(draggable: Draggable): Boolean {
//        return draggable is DraggablePatch
//    }
//
//    override fun getDraggable(index: Int): Draggable {
//        val editor = show.edit()
//        return DraggablePatch(editor, showState.selectedScene, index) {
//            editHandler.onShowEdit(editor)
//        }
//    }
//
//    override fun insertDraggable(draggable: Draggable, index: Int) {
//        draggable as DraggablePatch
//        draggable.addTo(showState.selectedScene, index)
//    }
//
//    override fun removeDraggable(draggable: Draggable) {
//        draggable as DraggablePatch
//        draggable.remove()
//    }
//}
