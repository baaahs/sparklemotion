package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.getBang
import baaahs.glshaders.*
import baaahs.show.*
import kotlinx.css.px
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.Edit
import materialui.ExpandMore
import materialui.components.card.card
import materialui.components.container.container
import materialui.components.divider.divider
import materialui.components.expansionpanel.expansionPanel
import materialui.components.expansionpaneldetails.expansionPanelDetails
import materialui.components.expansionpanelsummary.expansionPanelSummary
import materialui.components.formcontrol.formControl
import materialui.components.iconbutton.iconButton
import materialui.components.inputlabel.inputLabel
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import materialui.icon
import org.w3c.dom.events.Event
import react.*
import react.dom.b
import react.dom.code
import react.dom.h3

val OldPatchEditor = xComponent<OldPatchEditorProps>("OldPatchEditor") { props ->
    val appContext = useContext(appContext)
    val shader = props.shader
    val openShader = appContext.showResources.openShader(shader)

    val otherShaderOutputPorts = props.allShaders
        .minus(shader).sortedBy { it.title }
        .map { it to appContext.showResources.openShader(it) }
        .flatMap { (otherShader, otherOpenShader) ->
            otherOpenShader.outputPorts.sortedBy { it.name }.map { otherShader to it }
        }

    val linkEditorsByPortId = props.patchEditor.linksTo(shader)
    fun idFor(dataSource: DataSource): String = props.showBuilder.idFor(dataSource)

    fun idFor(port: LinkEditor.Port): String {
        return when (port) {
            is ShaderPortEditor -> "${props.showBuilder.idFor(port.shader)}:${port.portId}"
            is DataSourceEditor -> idFor(port.dataSource)
            else -> error("huh? $port")
        }
    }

    fun portFor(id: String): LinkEditor.Port {
        if (id.contains(':')) {
            val (shaderId, portId) = id.split(':')
            val otherShader = props.showBuilder.getShaders().getBang(shaderId, "shader")
            return ShaderEditor.ShaderOutPortEditor(otherShader, portId)
        }

        return DataSourceEditor(props.showBuilder.getDataSources().getBang(id, "data source"))
    }

    val inputPorts = openShader.inputPorts.sortedBy { it.title }
    val dataSources = appContext.showResources.dataSources.sortedBy { it.dataSourceName }

    val handleLinkChange =
        handler("link change") { event: Event, inputPort: InputPort ->
            val linkEditor = linkEditorsByPortId[inputPort.id]
            val value = event.target.asDynamic().value as String
            if (value == "__new__") return@handler

            val port = portFor(value)
            if (linkEditor == null) {
                props.patchEditor.addLink(
                    port,
                    baaahs.show.ShaderEditor.ShaderInPortEditor(shader, inputPort.id)
                )
            } else {
                linkEditor.from = port
            }
            forceRender()
        }

    container {
        card {
            attrs.raised = true

            if (openShader is ColorShader) {
                iconButton {
                    icon(Edit)

                    attrs.onClickFunction = {}
                }
                val previewPatch =
                    AutoWirer(Plugins.findAll()).autoWire(openShader as OpenShader)
                patchPreview {
                    attrs.patch = previewPatch.resolve().open()
                    attrs.width = 120.px
                    attrs.height = 75.px
                    attrs.onSuccess = {}
                    attrs.onGadgetsChange = {}
                    attrs.onError = {}
                }
            }
            h3 { +shader.title }
        }

        expansionPanel {
            expansionPanelSummary {
                attrs.expandIcon { icon(ExpandMore) }
                +"Links"
            }

            expansionPanelDetails {
                table {
                    tableHead {
                        tableRow {
                            thCell { +"From" }
                            thCell { +"To Port" }
                        }
                    }

                    tableBody {
                        inputPorts.forEach { shaderInputPort ->
                            val sourcePort = linkEditorsByPortId[shaderInputPort.id]?.from

                            tableRow {
                                tdCell {
                                    formControl {
                                        inputLabel { +"Source" }

                                        select {
                                            sourcePort?.let { attrs.value(idFor(it)) }
                                            attrs.onChangeFunction = { event -> handleLinkChange(event, shaderInputPort) }

                                            var needDivider = false
                                            otherShaderOutputPorts.forEach { (otherShader, outputPort) ->
//                                                if (outputPort.dataType == shaderInputPort.dataType) {
                                                    menuItem {
                                                        attrs["value"] = idFor(ShaderEditor(otherShader).outputPort(ShaderOutPortRef.ReturnValue))
                                                        +"${otherShader.title} output"
                                                    }
                                                    needDivider = true
//                                                }
                                            }

                                            if (needDivider) { divider {}; needDivider = false }

                                            dataSources.forEach { dataSource ->
                                                if (dataSource.getType() == shaderInputPort.dataType) {
                                                    menuItem {
                                                        attrs["value"] = idFor(dataSource)
                                                        +dataSource.dataSourceName
                                                    }
                                                    needDivider = true
                                                }
                                            }

                                            if (needDivider) { divider {}; needDivider = false }

                                            menuItem {
                                                attrs["value"] = "__new__"
                                                +"Create New…"
                                            }
                                        }
                                    }
                                }

                                tdCell {
                                    b { +shaderInputPort.title }
                                    code { +" (${shaderInputPort.dataType})" }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

external interface OldPatchEditorProps : RProps {
    var allShaders: Set<Shader>
    var patchEditor: PatchEditor
    var showBuilder: ShowBuilder
    var shader: Shader
}

fun RBuilder.oldPatchEditor(handler: RHandler<OldPatchEditorProps>): ReactElement =
    child(OldPatchEditor, handler = handler)
