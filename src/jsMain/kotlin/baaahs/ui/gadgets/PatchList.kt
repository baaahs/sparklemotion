package baaahs.ui.gadgets

import baaahs.PubSub
import baaahs.ShowResources
import baaahs.replacing
import baaahs.show.PatchSet
import baaahs.ui.*
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onContextMenuFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonVariant
import materialui.components.buttongroup.buttonGroup
import materialui.components.buttongroup.enums.ButtonGroupOrientation
import materialui.components.card.card
import org.w3c.dom.events.Event
import react.*

val PatchSetList = xComponent<PatchSetListProps>("PatchSetList") { props ->
    var editingPatchSet by state<EditSpec<PatchSet>?> { null }

    card {
        buttonGroup {
            attrs.variant = ButtonVariant.outlined
            attrs.orientation = ButtonGroupOrientation.vertical
            println("Rendering patchSets: ${props.patchSets.map { it.title }}")
            props.patchSets.forEachIndexed { index, patchSet ->
                button {
                    +patchSet.title
//                attrs.color = ButtonColor.primary
//                (attrs as Tag).disabled = patchSet == props.currentPatchSet
                    attrs["disabled"] = index == props.selected
                    attrs.onClickFunction = { props.onSelect(index) }
                    if (props.editMode) {
                        attrs.onContextMenuFunction = { event: Event ->
                            editingPatchSet = EditSpec(
                                model = patchSet,
                                onSave = { newPatchSet ->
                                    props.onChange(props.patchSets.replacing(index, newPatchSet))
                                    editingPatchSet = null
                                },
                                onCancel = { editingPatchSet = null })
                            event.preventDefault()
                        }
                    }
                }
            }

            if (props.editMode) {
                button {
                    +"+"
                    attrs.onClickFunction = { _: Event ->
                        editingPatchSet = EditSpec(
                            model = PatchSet("Untitled"),
                            onSave = { newPatchSet ->
                                props.onChange(props.patchSets + newPatchSet)
                                editingPatchSet = null
                            },
                            onCancel = { editingPatchSet = null }
                        )
                    }
                }
            }
        }
    }

    editingPatchSet?.let { editSpec ->
        patchSetEditor {
            showResources = props.showResources
            patchSet = editSpec.model
            onSave = editSpec.onSave
            onClose = handler("patchSetEditor.onClose") { editingPatchSet = null }
        }
    }
}

external interface PatchSetListProps : RProps {
    var pubSub: PubSub.Client
    var showResources: ShowResources
    var patchSets: List<PatchSet>
    var selected: Int
    var onSelect: (Int) -> Unit
    var editMode: Boolean
    var onChange: (List<PatchSet>) -> Unit
}

fun RBuilder.patchSetList(handler: PatchSetListProps.() -> Unit): ReactElement =
    child(PatchSetList) { attrs { handler() } }
