package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.glshaders.InputPort
import baaahs.glshaders.OutputPort
import baaahs.show.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.AddCircleOutline
import materialui.components.container.container
import materialui.components.divider.divider
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
import materialui.components.typography.typography
import materialui.icon
import org.w3c.dom.events.Event
import react.*
import react.dom.b
import react.dom.code

val LinksEditor = xComponent<LinksEditorProps>("OldPatchEditor") { props ->
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
                                    val value = event.target.asDynamic().value as String
                                    when(value) {
                                        "__new__" -> {} // TODO
                                        "__none__" -> incomingLinks.remove(inputPort.id)
                                        else -> incomingLinks[inputPort.id] = sourcePortOptions[value[1].toInt()].portEditor
                                    }
                                    props.onChange()
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

            tableRow {
                tdCell {
                    attrs.colSpan = "2"

                    container {
                        iconButton {
                            icon(AddCircleOutline)
                            typography { +"New Link…" }

                            attrs.onClickFunction = { _: Event ->
//                        shaderInstanceEditor.incomingLinks.put(LinkEditor(null, null))
                                props.onChange()
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


external interface LinksEditorProps : RProps {
    var patchEditor: PatchEditor
    var showBuilder: ShowBuilder
    var shaderInstance: ShaderInstanceEditor
    var shaderChannels: Set<ShaderChannel>
    var onChange: () -> Unit
}

fun RBuilder.linksEditor(handler: RHandler<LinksEditorProps>): ReactElement =
    child(LinksEditor, handler = handler)
