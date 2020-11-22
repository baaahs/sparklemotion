package baaahs.app.ui.editor

import baaahs.gl.shader.InputPort
import baaahs.show.mutable.EditingShader
import baaahs.ui.xComponent
import materialui.components.circularprogress.circularProgress
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import materialui.components.typography.typographyH6
import react.*
import react.dom.b
import react.dom.br
import react.dom.code

val LinksEditor = xComponent<LinksEditorProps>("LinksEditor") { props ->
    observe(props.editingShader)

    val lastShaderInputPorts = ref<List<InputPort>?>(null)
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
                thCell { +"Port" }
                thCell { +"Source" }
            }
        }

        tableBody {
            shaderInputPorts.forEach { inputPort ->
                tableRow {
                    tdCell {
                        b { +inputPort.title }
                        br {}
                        code { +"(${inputPort.contentType?.title ?: inputPort.type.glslLiteral})" }
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
        }
    }
}

external interface LinksEditorProps : RProps {
    var editableManager: EditableManager
    var editingShader: EditingShader
}

fun RBuilder.linksEditor(handler: RHandler<LinksEditorProps>): ReactElement =
    child(LinksEditor, handler = handler)
