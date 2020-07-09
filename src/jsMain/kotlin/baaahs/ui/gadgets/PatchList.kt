package baaahs.ui.gadgets

import baaahs.OpenShow
import baaahs.ShowResources
import baaahs.ShowState
import baaahs.app.ui.DragNDrop
import baaahs.app.ui.Draggable
import baaahs.app.ui.DropTarget
import baaahs.show.PatchyEditor
import baaahs.show.Show
import baaahs.show.ShowEditor
import baaahs.ui.patchyEditor
import baaahs.ui.useCallback
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onContextMenuFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttongroup.enums.ButtonGroupOrientation
import materialui.components.card.card
import materialui.toggleButton
import materialui.toggleButtonGroup
import org.w3c.dom.events.Event
import react.RBuilder
import react.RProps
import react.ReactElement
import react.child

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
    val dropTarget = memo {
        object : DropTarget {
            override val type: String get() = "PatchList"

            override fun moveDraggable(fromIndex: Int, toIndex: Int) {
                props.show.edit(props.showState) {
                    editScene(props.showState.selectedScene) {
                        movePatchSet(fromIndex, toIndex)
                    }
                }.also { editor ->
                    props.onChange(editor.getShow(), editor.getShowState())
                }
            }

            override fun willAccept(draggable: Draggable): Boolean {
                return draggable is DraggablePatch
            }

            override fun getDraggable(index: Int): Draggable {
                return DraggablePatch(
                    props.show.edit(props.showState),
                    props.showState.selectedScene,
                    index,
                    props.onChange
                )
            }

            override fun insertDraggable(draggable: Draggable, index: Int) {
                draggable as DraggablePatch
                draggable.addTo(props.showState.selectedScene, index)
            }

            override fun removeDraggable(draggable: Draggable) {
                draggable as DraggablePatch
                draggable.remove()
            }
        }
    }
    val dropTargetId = memo { props.dragNDrop.addDropTarget(dropTarget) }
    whenMounted {
        withCleanup {
            props.dragNDrop.removeDropTarget(dropTarget)
        }
    }

    val selectedScene = props.showState.selectedScene
    val patchSets = props.show.scenes[selectedScene].patchSets

    val onContextClick = useCallback(props.show, props.showState) { event: Event, index: Int ->
        props.show.edit(props.showState) {
            editScene(selectedScene) {
                editPatchSet(index) {
                    patchyEditor = this
                }
            }
            event.preventDefault()
        }
    }

    card {
        toggleButtonGroup {
            attrs.variant = ButtonVariant.outlined
            attrs.orientation = ButtonGroupOrientation.vertical
            attrs["exclusive"] = true
//            attrs["value"] = props.selected // ... but this is busted.
//            attrs.onChangeFunction = eventHandler { value: Int -> props.onSelect(value) }
            patchSets.forEachIndexed { index, patchSet ->
                toggleButton {
//                attrs.color = ButtonColor.primary
//                (attrs as Tag).disabled = patchSet == props.currentPatchSet
                    attrs["value"] = index
                    attrs["selected"] = index == props.showState.selectedPatchSet
                    attrs.onClickFunction = { props.onSelect(index) }
                    if (props.editMode) {
                        attrs.onContextMenuFunction = { event: Event -> onContextClick(event, index) }
                    }
                    +patchSet.title
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

    patchyEditor?.let { editor ->
        patchyEditor {
            showResources = props.showResources
            this.editor = editor
            onSave = {
                props.onChange(editor.getShow(), editor.getShowState())
                patchyEditor = null
            }
            onCancel = handler("patchyEditor.onClose") { patchyEditor = null }
        }
    }
}

external interface PatchSetListProps : RProps {
    var show: OpenShow
    var showState: ShowState
    var showResources: ShowResources
    var onSelect: (Int) -> Unit
    var editMode: Boolean
    var dragNDrop: DragNDrop
    var onChange: (Show, ShowState) -> Unit
}

fun RBuilder.patchSetList(handler: PatchSetListProps.() -> Unit): ReactElement =
    child(PatchSetList) { attrs { handler() } }
