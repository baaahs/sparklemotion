package baaahs.app.ui.editor

import baaahs.gl.patch.ShaderInstanceOptions
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

    val lastShaderInstanceOptions = ref<ShaderInstanceOptions?>(null)
    val curShaderInstanceOptions = props.editingShader.getShaderInstanceOptions()
    val suggestionsAreCurrent = curShaderInstanceOptions != null
    val shaderInstanceOptions = curShaderInstanceOptions
        ?: lastShaderInstanceOptions.current
    lastShaderInstanceOptions.current = shaderInstanceOptions

    val handleInputPortChange = handler(
        "change to inputPort source", props.editingShader, props.editableManager
    ) { inputPort: InputPort, linkOption: LinkOption? ->
        props.editingShader.changeInputPort(inputPort, linkOption)
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
                            attrs.linkOptions = shaderInstanceOptions?.suggestions?.get(inputPort.id)
                                ?: emptyList()
                            attrs.isDisabled = !suggestionsAreCurrent
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
