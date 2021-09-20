package baaahs.app.ui.editor

import baaahs.gl.shader.InputPort
import baaahs.show.mutable.EditingShader
import baaahs.ui.typographyBody1
import baaahs.ui.typographyBody2
import baaahs.ui.typographySubtitle2
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import materialui.components.circularprogress.circularProgress
import materialui.components.iconbutton.iconButton
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import materialui.components.typography.enums.TypographyColor
import materialui.components.typography.typography
import materialui.components.typography.typographyH6
import materialui.icon
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.b
import react.dom.code

val LinksEditor = xComponent<LinksEditorProps>("LinksEditor") { props ->
    observe(props.editingShader)

    val lastShaderInputPorts = ref<List<InputPort>>()
    val shaderInputPorts = props.editingShader.inputPorts
        ?.also { lastShaderInputPorts.current = it }
        ?: lastShaderInputPorts.current

    if (shaderInputPorts == null) {
        circularProgress {}
        typographyH6 { +"Analyzing Shader…" }
    } else table {
        attrs["size"] = "small"

        tableHead {
            tableRow {
                thCell { typographySubtitle2 { +"Port" } }
                thCell { typographySubtitle2 { +"Source" } }
            }
        }

        tableBody {
            shaderInputPorts.forEach { inputPort ->
                tableRow {
                    tdCell {
                        typographyBody1 { b { +inputPort.title } }
                        typographyBody2 {
                            if (inputPort.contentType.isUnknown()) {
                                attrs.color = TypographyColor.error
                            }

                            +"(${inputPort.contentType.title})"
                        }
                    }

                    tdCell {
                        linkSourceEditor {
                            key = inputPort.id
                            attrs.editableManager = props.editableManager
                            attrs.editingShader = props.editingShader
                            attrs.inputPort = inputPort
                        }
                    }
                }
            }

            val extraLinks = props.editingShader.extraLinks
            if (extraLinks.isNotEmpty()) {
                tableRow {
                    thCell { typographySubtitle2 { +"Unknown Port" } }
                    thCell { typographySubtitle2 { +"Old Source" } }
                }

                extraLinks.forEach { (portId, link) ->
                    tableRow {
                        tdCell {
                            typography {
                                attrs.color = TypographyColor.error

                                code { +portId }
                            }
                        }

                        tdCell {
                            typography { +link.title }

                            iconButton {
                                attrs.onClickFunction = { _ ->
                                    props.editingShader.changeInputPortLink(portId, null)
                                    props.editableManager.onChange()
                                }
                                icon(materialui.icons.Delete)
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface LinksEditorProps : Props {
    var editableManager: EditableManager
    var editingShader: EditingShader
}

fun RBuilder.linksEditor(handler: RHandler<LinksEditorProps>) =
    child(LinksEditor, handler = handler)
