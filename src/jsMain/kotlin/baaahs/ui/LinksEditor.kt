package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.glshaders.InputPort
import baaahs.glshaders.OutputPort
import baaahs.show.DataSource
import baaahs.show.ShaderChannel
import baaahs.show.mutable.*
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.AddCircleOutline
import materialui.components.container.container
import materialui.components.divider.divider
import materialui.components.formcontrol.formControl
import materialui.components.iconbutton.iconButton
import materialui.components.inputlabel.inputLabel
import materialui.components.listsubheader.listSubheader
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

val LinksEditor = xComponent<LinksEditorProps>("LinksEditor") { props ->
    val appContext = useContext(appContext)

    val shaderChannelOptions = props.shaderChannels
        .sortedBy { it.id }
        .map { shaderChannel -> ShaderChannelOption(shaderChannel) }

    val shaderOptions = props.mutablePatch.mutableShaderInstances
        .minus(props.mutableShaderInstance)
        .sortedBy { it.mutableShader.title }
        .mapNotNull { editor ->
            val openShader = appContext.showPlayer.openShaderOrNull(editor.mutableShader.build())
            openShader?.let { ShaderOption(editor, it.outputPort) }
        }

    val dataSourceOptions = appContext.showPlayer.dataSources.sortedBy { it.dataSourceName }.mapIndexed { index, dataSource ->
        DataSourceOption(dataSource)
    }

    val sourcePortOptions = shaderChannelOptions + shaderOptions + dataSourceOptions

    val shaderInstance = props.mutableShaderInstance
    val shader = shaderInstance.mutableShader
    val openShader = appContext.showPlayer.openShaderOrNull(shader.build())
    val inputPorts = openShader?.inputPorts?.sortedBy { it.title }
    val incomingLinks = props.mutableShaderInstance.incomingLinks

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
                                        listSubheader { +option.groupName }
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
    val portEditor: MutableLink.Port
    val groupName: String
    fun matches(otherPort: MutableLink.Port): Boolean
    fun isAppropriateFor(inputPort: InputPort): Boolean
}

class DataSourceOption(val dataSource: DataSource): SourcePortOption {
    override val title: String get() = dataSource.dataSourceName
    override val portEditor: MutableLink.Port get() = MutableDataSource(
        dataSource
    )
    override val groupName: String get() = "Data Sources"

    override fun matches(otherPort: MutableLink.Port): Boolean {
        return otherPort is MutableDataSource && otherPort.dataSource == dataSource
    }

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return dataSource.getType() == inputPort.dataType
    }
}

class ShaderChannelOption(val shaderChannel: ShaderChannel): SourcePortOption {
    override val title: String get() = "${shaderChannel.id} shader channel"
    override val portEditor: MutableLink.Port get() = MutableShaderChannel(
        shaderChannel
    )
    override val groupName: String get() = "Shader Channels"

    override fun matches(otherPort: MutableLink.Port): Boolean {
        return otherPort is MutableShaderChannel && otherPort.shaderChannel == shaderChannel
    }

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return true // We don't have any type info for channel links.
    }
}

class ShaderOption(val mutableShaderInstance: MutableShaderInstance, val outputPort: OutputPort): SourcePortOption {
    override val title: String get() = "${mutableShaderInstance.mutableShader.title} output"
    override val portEditor: MutableLink.Port get() = MutableShaderOutPort(
        mutableShaderInstance,
        outputPort.id
    )
    override val groupName: String get() = "Shader Ports"

    override fun matches(otherPort: MutableLink.Port): Boolean {
        return otherPort is MutableShaderOutPort &&
                otherPort.mutableShaderInstance == mutableShaderInstance &&
                otherPort.portId == outputPort.id
    }

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return true // TODO port.dataType == inputPort.dataType
    }
}


external interface LinksEditorProps : RProps {
    var mutablePatch: MutablePatch
    var showBuilder: ShowBuilder
    var mutableShaderInstance: MutableShaderInstance
    var shaderChannels: Set<ShaderChannel>
    var onChange: () -> Unit
}

fun RBuilder.linksEditor(handler: RHandler<LinksEditorProps>): ReactElement =
    child(LinksEditor, handler = handler)
