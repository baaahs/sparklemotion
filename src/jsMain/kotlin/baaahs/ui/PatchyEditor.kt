package baaahs.ui

import baaahs.ShowResources
import baaahs.app.ui.icon
import baaahs.glshaders.*
import baaahs.show.PatchEditor
import baaahs.show.PatchyEditor
import baaahs.show.ShaderPortEditor
import kotlinx.css.px
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import materialui.Edit
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.card.card
import materialui.components.dialog.dialog
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.iconbutton.iconButton
import materialui.components.list.list
import materialui.components.listitem.listItem
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
import react.dom.h3
import react.dom.key

@Suppress("UNCHECKED_CAST")
fun <T> Event.targetEl(): T = target as T

val PatchyEditor = xComponent<PatchyEditorProps>("PatchSetEditor") { props ->
    val textField = ref<HTMLInputElement>()

    val changed = props.editor.isChanged()

    val handleTitleChange = useCallback(props.editor) { event: Event ->
        props.editor.title = event.targetEl<HTMLInputElement>().value
        forceRender()
    }

    val x = this
    dialog {
        attrs.open = true
        attrs.onClose = x.handler("onClose", props.onCancel) { _: Event, _: String -> props.onCancel() }

        dialogTitle { +"${props.editor.displayType} Editor" }

        dialogContent {
            form {
                attrs.onSubmitFunction = x.handler("onSubmit", changed, props.onSave) { event: Event ->
                    if (changed) {
                        props.onSave()
                    }
                    event.preventDefault()
                }

                textField {
                    ref = textField
                    attrs.autoFocus = true
                    attrs.variant = FormControlVariant.outlined
                    attrs.label = "Title".asTextNode()
                    attrs.value = props.editor.title
                    attrs.onChangeFunction = handleTitleChange
                }

                table {
                    attrs["stickyHeader"] = true

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
                        props.editor.patchMappings.forEachIndexed { index: Int, patchEditor: PatchEditor ->
                            tableRow {
                                attrs.key = "$index"

                                tdCell {
                                    attrs.key = "Surfaces"
                                    +patchEditor.surfaces.name
                                }

                                tdCell {
                                    attrs.key = "Patches"

                                    +"Shaders:"
                                    patchEditor.findShaders().forEach { shader ->
                                        card {
                                            attrs.raised = true

                                            val openShader = GlslAnalyzer().asShader(shader)
                                            if (openShader is ColorShader) {
                                                iconButton {
                                                    icon(Edit) { }

                                                    attrs.onClickFunction = {}
                                                }
                                                val previewPatch =
                                                    AutoWirer(Plugins.findAll()).autoWire(openShader as OpenShader)
                                                patchPreview {
                                                    this.patch = previewPatch.resolve().open()
                                                    width = 120.px
                                                    height = 75.px
                                                    onSuccess = {}
                                                    onGadgetsChange = {}
                                                    onError = {}
                                                }
                                            }
                                            h3 { +shader.title }


                                            +"Links:"
                                            table {
                                                tableHead {
                                                    tableRow {
                                                        thCell { +"From" }
                                                        thCell { +"To" }
                                                    }
                                                }

                                                tableBody {
                                                    patchEditor.links.filter { (_, to) ->
                                                        to is ShaderPortEditor && to.shader == shader
                                                    }.forEach { (from, to) ->
                                                        tableRow {
                                                            tdCell { +from.displayName() }
                                                            tdCell { +to.displayName() }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
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
                attrs.onClickFunction = x.eventHandler(props.onCancel)
            }

            button {
                +"Save"
                attrs["disabled"] = !changed
                attrs.color = ButtonColor.primary
                attrs.onClickFunction = x.eventHandler(props.onSave)
            }
        }
    }
}

external interface PatchyEditorProps : RProps {
    var showResources: ShowResources
    var editor: PatchyEditor
    var onSave: () -> Unit
    var onCancel: () -> Unit
}

fun RBuilder.patchyEditor(handler: PatchyEditorProps.() -> Unit): ReactElement =
    child(PatchyEditor) { attrs { handler() } }
