package baaahs.ui

import baaahs.show.mutable.PatchEditor
import baaahs.show.mutable.PatchHolderEditor
import kotlinx.css.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onSubmitFunction
import materialui.AddCircleOutline
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerStyle
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.iconbutton.iconButton
import materialui.components.portal.portal
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.enums.TableCellStyle
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import materialui.components.textfield.textField
import materialui.components.typography.typography
import materialui.icon
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.form
import react.dom.key
import styled.StyleSheet

@Suppress("UNCHECKED_CAST")
fun <T> Event.targetEl(): T = target as T

val PatchHolderEditor = xComponent<PatchHolderEditorProps>("PatchHolderEditor") { props ->
    val textField = ref<HTMLInputElement>()

    val changed = props.editor.isChanged()

    val handleTitleChange = useCallback(props.editor) { event: Event ->
        props.editor.title = event.targetEl<HTMLInputElement>().value
        forceRender()
    }

    val handleDrawerClose = eventHandler("handleDrawerClose", props.onCancel) {
        props.onCancel()
    }

    val x = this
    portal {
        drawer(PatchHolderStyles.drawer on DrawerStyle.paper) {
            attrs.anchor = DrawerAnchor.bottom
            attrs.variant = DrawerVariant.temporary
            attrs.elevation
            attrs.open = true
            attrs.onClose = handleDrawerClose

            dialogTitle { +"${props.editor.displayType} Editor" }

            dialogContent {
                form {
                    attrs.onSubmitFunction = x.handler("onSubmit", changed, props.onApply) { event: Event ->
                        if (changed) {
                            props.onApply()
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

                    table(PatchHolderStyles.patchTable.name) {
                        attrs["stickyHeader"] = true

                        tableHead {
                            tableRow {
                                thCell(PatchHolderStyles.patchTableSurfacesColumn on TableCellStyle.root) {
                                    attrs.key = "Surfaces"
                                    +"Surfaces"
                                }

                                thCell(PatchHolderStyles.patchTableShadersColumn on TableCellStyle.root) {
                                    attrs.key = "Shaders"
                                    +"Shaders"
                                }
                            }
                        }
                        tableBody {
                            props.editor.patchMappings.forEachIndexed { index: Int, patchEditor: PatchEditor ->
                                patchEditor {
                                    attrs.patchEditor = patchEditor
                                    attrs.patchHolderEditor = props.editor
                                    attrs.onChange = { this@xComponent.forceRender() }
                                }
                            }

                            tableRow {
                                attrs.key = "__new__"

                                tdCell {
                                    attrs.colSpan = "2"

                                    iconButton {
                                        icon(AddCircleOutline)
                                        typography { +"New Patch…" }

                                        attrs.onClickFunction = {
                                            props.editor.patchMappings.add(PatchEditor())
                                            this@xComponent.forceRender()
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
                    +"Revert"
                    attrs.color = ButtonColor.secondary
                    attrs.onClickFunction = x.eventHandler(props.onCancel)
                }

                button {
                    +"Apply"
                    attrs.disabled = !changed
                    attrs.color = ButtonColor.primary
                    attrs.onClickFunction = x.eventHandler(props.onApply)
                }
            }
        }
    }
}

object PatchHolderStyles : StyleSheet("ui-PatchHolderEditor", isStatic = true) {
    val drawer by css {
        margin(horizontal = 5.em)
        minHeight = 85.vh
    }

    val patchTable by css {}
    val patchTableSurfacesColumn by css {
        width = 15.pct
    }
    val patchTableShadersColumn by css {
        width = 85.pct
    }

    val shaderCard by css {
        margin(1.em)
        padding(1.em)
    }
}

external interface PatchHolderEditorProps : RProps {
    var editor: PatchHolderEditor
    var onApply: () -> Unit
    var onCancel: () -> Unit
}

fun RBuilder.patchHolderEditor(handler: RHandler<PatchHolderEditorProps>): ReactElement =
    child(PatchHolderEditor, handler = handler)
