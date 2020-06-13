package baaahs.ui

import baaahs.ShowResources
import baaahs.show.PatchMapping
import baaahs.show.PatchSet
import baaahs.unknown
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.dialog.dialog
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import materialui.components.textfield.textField
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.RBuilder
import react.RProps
import react.ReactElement
import react.child
import react.dom.form
import react.dom.key

fun <T> Event.targetEl(): T = target as T

val PatchSetEditor = xComponent<PatchSetEditorProps>("PatchSetEditor") { props ->
    var currentPatchSet by state { props.patchSet }
    val textField = ref<HTMLInputElement>()

    val changed = currentPatchSet != props.patchSet

    val handleTitleChange = useCallback(currentPatchSet) { event: Event ->
        currentPatchSet = currentPatchSet.copy(title = event.targetEl<HTMLInputElement>().value)
    }

    val x = this
    dialog {
        attrs.open = true
        attrs.onClose = x.handler("onClose", props.onClose) { _: Event, _: String -> props.onClose() }

        dialogTitle { +"Patchset Editor" }

        dialogContent {
            form {
                attrs.onSubmitFunction = x.handler("onSubmit", changed, props.onSave, currentPatchSet) { event: Event ->
                    if (changed) { props.onSave(currentPatchSet) }
                    event.preventDefault()
                }

                textField {
                    ref = textField
                    attrs.variant = FormControlVariant.outlined
                    attrs.label = "Title".asTextNode()
                    attrs.value = currentPatchSet.title
                    attrs.onChangeFunction = handleTitleChange
                }

                table {
                    attrs["stickyHeader"] = "stickyHeader"

                    tableHead {
                        tableRow {
                            thCell {
                                attrs.key = "Surfaces"
                                +"Surfaces"
                            }

                            thCell {
                                attrs.key = "Patches"
                                +"Patches"
                            }
                        }
                    }
                    tableBody {
                        currentPatchSet.patchMappings.forEachIndexed { index: Int, patchMapping: PatchMapping ->
                            tableRow {
                                attrs.key = "$index"

                                tdCell {
                                    attrs.key = "Surfaces"
                                    +patchMapping.surfaces.name
                                }

                                tdCell {
                                    attrs.key = "Patches"
                                    val shaders = patchMapping.getShaderIds().map {
                                        props.showResources.shaders[it]
                                            ?: error(unknown("shader", it, props.showResources.shaders.keys))
                                    }
                                    +"Shaders: ${shaders.joinToString(", ") { it.title }}"
                                }
                            }
                        }
                    }
                }
            }
        }

        dialogActions {
            button {
                +"Cancel"
                attrs.color = ButtonColor.secondary
                attrs.onClickFunction = x.handler("onCancelClick", props.onClose) { event: Event -> props.onClose() }
            }

            button {
                +"Save"
                attrs["disabled"] = !changed
                attrs.color = ButtonColor.primary
                attrs.onClickFunction = x.handler("onSaveClick", props.onSave, currentPatchSet) { event: Event ->
                    props.onSave(currentPatchSet)
                }
            }
        }
    }
}

external interface PatchSetEditorProps : RProps {
    var showResources: ShowResources
    var patchSet: PatchSet
    var onSave: (PatchSet) -> Unit
    var onClose: () -> Unit
}

class EditSpec<T>(
    val model: T,
    val onSave: (newPatchSet: T) -> Unit,
    val onCancel: () -> Unit
)

fun RBuilder.patchSetEditor(handler: PatchSetEditorProps.() -> Unit): ReactElement =
    child(PatchSetEditor) { attrs { handler() } }
