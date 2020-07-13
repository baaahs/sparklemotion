package baaahs.ui

import baaahs.ShowResources
import baaahs.glshaders.*
import baaahs.show.*
import kotlinx.css.*
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
import materialui.components.drawer.drawer
import materialui.components.drawer.enums.DrawerAnchor
import materialui.components.drawer.enums.DrawerStyle
import materialui.components.drawer.enums.DrawerVariant
import materialui.components.formcontrol.enums.FormControlVariant
import materialui.components.iconbutton.iconButton
import materialui.components.portal.portal
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import materialui.components.textfield.textField
import materialui.icon
import materialui.styles.StylesBuilder
import materialui.styles.withStyles
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.form
import react.dom.h3
import react.dom.key
import styled.StyleSheet
import styled.getClassName
import kotlin.properties.ReadOnlyProperty

@Suppress("UNCHECKED_CAST")
fun <T> Event.targetEl(): T = target as T

val PatchyEditor = xComponent<PatchyEditorProps>("PatchSetEditor") { props ->
    val textField = ref<HTMLInputElement>()
    val showBuilder by state { ShowBuilder() }

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
        drawer(styles.drawer on DrawerStyle.paper) {
            attrs.anchor = DrawerAnchor.bottom
            attrs.variant = DrawerVariant.temporary
            attrs.elevation
            attrs.open = true
            attrs.onClose = handleDrawerClose

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
                                val allShaders = patchEditor.findShaders()
                                tableRow {
                                    attrs.key = "$index"

                                    tdCell {
                                        attrs.key = "Surfaces"
                                        +patchEditor.surfaces.name
                                    }

                                    tdCell {
                                        attrs.key = "Patches"

                                        +"Shaders:"
                                        allShaders.forEach { shader ->
                                            shaderEditor {
                                                attrs.showResources = props.showResources
                                                attrs.allShaders = allShaders
                                                attrs.patchEditor = patchEditor
                                                attrs.showBuilder = showBuilder
                                                attrs.shader = shader
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
}

private object styles : StyleSheet("UI-PatchyEditor", isStatic = true) {
    val drawer by css {
        margin(horizontal = 5.em)
    }

    val shaderCard by css {
        margin(1.em)
        padding(1.em)
    }
}

infix fun <T> RuleSet.on(clazz: T): Pair<T, String> {
    return clazz to getName()
}

external interface PatchyEditorProps : RProps {
    var showResources: ShowResources
    var editor: PatchyEditor
    var onSave: () -> Unit
    var onCancel: () -> Unit
}

fun RBuilder.patchyEditor(handler: RHandler<PatchyEditorProps>): ReactElement =
    child(PatchyEditor, handler = handler)
