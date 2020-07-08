package baaahs.ui.gadgets

import baaahs.OpenShow
import baaahs.ShowResources
import baaahs.ShowState
import baaahs.show.PatchyEditor
import baaahs.show.Show
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

val PatchSetList = xComponent<PatchSetListProps>("PatchSetList") { props ->
    var patchyEditor by state<PatchyEditor?> { null }

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
    var onChange: (Show, ShowState) -> Unit
}

fun RBuilder.patchSetList(handler: PatchSetListProps.() -> Unit): ReactElement =
    child(PatchSetList) { attrs { handler() } }
