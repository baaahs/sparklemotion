package baaahs.ui

import baaahs.app.ui.appContext
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
import react.*
import react.dom.b
import react.dom.code
import react.dom.h3

val OldPatchEditor = xComponent<OldPatchEditorProps>("OldPatchEditor") { props ->
    val appContext = useContext(appContext)

    val shaderChannelOptions = props.shaderChannels
        .sortedBy { it.id }
        .map { shaderChannel -> ShaderChannelOption(shaderChannel) }

    val shaderOptions = props.patchEditor.shaderInstances
        .minus(props.shaderInstance)
        .sortedBy { it.shader.title }
        .mapNotNull { editor ->
            val openShader = appContext.showPlayer.openShaderOrNull(editor.shader.shader)
            openShader?.let { ShaderOption(editor, it.outputPort) }
        }

    val dataSourceOptions = appContext.showPlayer.dataSources.sortedBy { it.dataSourceName }.mapIndexed { index, dataSource ->
        DataSourceOption(dataSource)
    }

    val sourcePortOptions = shaderChannelOptions + shaderOptions + dataSourceOptions

    val shaderInstance = props.shaderInstance
    val shader = shaderInstance.shader
    val openShader = appContext.showPlayer.openShaderOrNull(shader.shader)
    val inputPorts = openShader?.inputPorts?.sortedBy { it.title }
    val incomingLinks = props.shaderInstance.incomingLinks

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
                    attrs.patch = previewPatch.resolve().openForPreview()
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
                        inputPorts?.forEach { inputPort ->
                            val currentSourcePort = shaderInstance.incomingLinks[inputPort.id]

                            tableRow {
                                tdCell {
                                    formControl {
                                        inputLabel { +"Source" }

                                        select {
                                            attrs.onChangeFunction = { event ->
                                                val value = (event.target.asDynamic().value as String).split(":")
                                                when(value[0]) {
                                                    "__new__" -> {} // TODO
                                                    "__none__" -> incomingLinks.remove(inputPort.id)
                                                    "shaderOut" -> incomingLinks[inputPort.id] = sourcePortOptions[value[1].toInt()].portEditor
//                "dataSource" -> shaderInstance.incomingLinks[inputPort.id] = dataSourceS[value[1].toInt()]
                                                }
                                                this@xComponent.forceRender()
                                            }

                                            var dividerGroup = sourcePortOptions.firstOrNull()?.groupName
                                            sourcePortOptions.forEachIndexed { index, option ->
                                                if (dividerGroup != option.groupName) {
                                                    divider {}
                                                    dividerGroup = option.groupName
                                                }

                                                if (currentSourcePort != null && option.matches(currentSourcePort)) {
                                                    attrs.value(index.toString())
                                                }

                                                if (option.isAppropriateFor(inputPort)) {
                                                    menuItem {
                                                        attrs["value"] = index.toString()
                                                        +option.title
                                                    }
                                                }
                                            }

                                            if (dividerGroup != null) { divider {} }

                                            menuItem {
                                                attrs["value"] = "__new__"
                                                +"Create New…"
                                            }
                                        }
                                    }
                                }

                                tdCell {
                                    b { +inputPort.title }
                                    code { +" (${inputPort.dataType})" }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

interface SourcePortOption {
    val title: String
    val portEditor: LinkEditor.Port
    val groupName: String
    fun matches(otherPort: LinkEditor.Port): Boolean
    fun isAppropriateFor(inputPort: InputPort): Boolean
}

class DataSourceOption(val dataSource: DataSource): SourcePortOption {
    override val title: String get() = dataSource.dataSourceName
    override val portEditor: LinkEditor.Port get() = DataSourceEditor(dataSource)
    override val groupName: String get() = "dataSource"

    override fun matches(otherPort: LinkEditor.Port): Boolean {
        return otherPort is DataSourceEditor && otherPort.dataSource == dataSource
    }

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return dataSource.getType() == inputPort.dataType
    }
}

class ShaderChannelOption(val shaderChannel: ShaderChannel): SourcePortOption {
    override val title: String get() = "${shaderChannel.id} shader channel"
    override val portEditor: LinkEditor.Port get() = ShaderChannelEditor(shaderChannel)
    override val groupName: String get() = "shaderChannel"

    override fun matches(otherPort: LinkEditor.Port): Boolean {
        return otherPort is ShaderChannelEditor && otherPort.shaderChannel == shaderChannel
    }

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return true // We don't have any type info for channel links.
    }
}

class ShaderOption(val editor: ShaderInstanceEditor, val outputPort: OutputPort): SourcePortOption {
    override val title: String get() = "${editor.shader.title} output"
    override val portEditor: LinkEditor.Port get() = ShaderOutPortEditor(editor, outputPort.id)
    override val groupName: String get() = "shaderPort"

    override fun matches(otherPort: LinkEditor.Port): Boolean {
        return otherPort is ShaderOutPortEditor &&
                otherPort.shaderInstance == editor &&
                otherPort.portId == outputPort.id
    }

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return true // TODO port.dataType == inputPort.dataType
    }
}


external interface OldPatchEditorProps : RProps {
    var patchEditor: PatchEditor
    var showBuilder: ShowBuilder
    var shaderInstance: ShaderInstanceEditor
    var shaderChannels: Set<ShaderChannel>
}

fun RBuilder.oldPatchEditor(handler: RHandler<OldPatchEditorProps>): ReactElement =
    child(OldPatchEditor, handler = handler)
