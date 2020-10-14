package baaahs.app.ui.editor

import baaahs.gl.shader.InputPort
import baaahs.show.mutable.EditingShader
import baaahs.ui.xComponent
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import react.*
import react.dom.b
import react.dom.br
import react.dom.code
import kotlin.collections.component1
import kotlin.collections.component2

val LinksEditor = xComponent<LinksEditorProps>("LinksEditor") { props ->
    observe(props.editingShader)
    val sourcePortOptions = props.editingShader.suggestSourcePortOptions()
    val openShader = props.editingShader.openShader
    val handleInputPortChange = handler(
        "change to inputPort source", props.editingShader, props.editableManager
    ) { inputPort: InputPort, sourcePortOption: SourcePortOption? ->
        props.editingShader.changeInputPort(inputPort, sourcePortOption)
        props.editableManager.onChange()
    }

    table {
        attrs["size"] = "small"

        tableHead {
            tableRow {
                thCell { +"Port" }
                thCell { +"Source" }
            }
        }

        tableBody {
            props.editingShader.inputPorts.forEach { (inputPort, currentSourcePort) ->
                tableRow {
                    tdCell {
                        b { +inputPort.title }
                        br {}
                        code { +" (${inputPort.type.glslLiteral})" }
                    }

                    tdCell {
                        linkSourceEditor {
                            attrs.inputPort = inputPort
                            attrs.currentSourcePort = currentSourcePort
                            attrs.sourcePortOptions = sourcePortOptions
                            attrs.onChange = handleInputPortChange
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
