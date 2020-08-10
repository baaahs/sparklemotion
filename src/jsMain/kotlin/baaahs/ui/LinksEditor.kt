package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.glshaders.InputPort
import baaahs.show.DataSource
import baaahs.show.ShaderChannel
import baaahs.show.mutable.*
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import react.*
import react.dom.b
import react.dom.code
import kotlin.collections.List
import kotlin.collections.Set
import kotlin.collections.associateWith
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.map
import kotlin.collections.minus
import kotlin.collections.plus
import kotlin.collections.set
import kotlin.collections.sortedBy

val LinksEditor = xComponent<LinksEditorProps>("LinksEditor") { props ->
    val appContext = useContext(appContext)

    val shaderChannelOptions = props.shaderChannels
        .sortedBy { it.id }
        .map { shaderChannel -> ShaderChannelOption(shaderChannel) }

    val shaderOptions =
        props.siblingMutableShaderInstances
            .minus(props.mutableShaderInstance)
            .sortedBy { it.mutableShader.title }
            .map { instance -> ShaderOption(instance) }

    val dataSourceOptions =
        appContext.showPlayer.dataSources
            .sortedBy { it.dataSourceName }.map { dataSource -> DataSourceOption(dataSource) }

    val sourcePortOptions =
        memo(props.siblingMutableShaderInstances, props.mutableShaderInstance, appContext.showPlayer.dataSources) {
            shaderChannelOptions + shaderOptions + dataSourceOptions
        }

    val shaderInstance = props.mutableShaderInstance
    val shader = shaderInstance.mutableShader
    val openShader = appContext.showPlayer.openShaderOrNull(shader.build())
    val inputPorts = openShader?.inputPorts
        ?.sortedBy { it.title }
        ?.associateWith { inputPort ->
            handler(
                "change to ${inputPort.id}", props.mutableShaderInstance, props.onChange
            ) { sourcePortOption: SourcePortOption? ->
                val incomingLinks = props.mutableShaderInstance.incomingLinks
                if (sourcePortOption == null) {
                    incomingLinks.remove(inputPort.id)
                } else {
                    incomingLinks[inputPort.id] = sourcePortOption.portEditor
                }
                props.onChange()
                this@xComponent.forceRender()
            }
        }

    table {
        attrs["size"] = "small"

        tableHead {
            tableRow {
                thCell { +"From" }
                thCell { +"To Port" }
            }
        }

        tableBody {
            inputPorts?.forEach { (inputPort, handleSourceChange) ->
                val currentSourcePort = shaderInstance.incomingLinks[inputPort.id]

                tableRow {
                    tdCell {
                        linkSourceEditor {
                            attrs.inputPort = inputPort
                            attrs.currentSourcePort = currentSourcePort
                            attrs.sourcePortOptions = sourcePortOptions
                            attrs.onChange = handleSourceChange
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


interface SourcePortOption {
    val title: String
    val portEditor: MutableLink.Port
    val groupName: String
    fun matches(otherPort: MutableLink.Port): Boolean
    fun isAppropriateFor(inputPort: InputPort): Boolean
}

data class DataSourceOption(val dataSource: DataSource): SourcePortOption {
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

data class ShaderChannelOption(val shaderChannel: ShaderChannel): SourcePortOption {
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

data class ShaderOption(val mutableShaderInstance: MutableShaderInstance): SourcePortOption {
    override val title: String get() = "${mutableShaderInstance.mutableShader.title} output"
    override val portEditor: MutableLink.Port get() = MutableShaderOutPort(mutableShaderInstance)
    override val groupName: String get() = "Shader Ports"

    override fun matches(otherPort: MutableLink.Port): Boolean {
        return otherPort is MutableShaderOutPort &&
                otherPort.mutableShaderInstance == mutableShaderInstance
    }

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return true // TODO port.dataType == inputPort.dataType
    }
}


external interface LinksEditorProps : RProps {
    var showBuilder: ShowBuilder
    var mutableShaderInstance: MutableShaderInstance
    var siblingMutableShaderInstances: List<MutableShaderInstance>
    var shaderChannels: Set<ShaderChannel>
    var onChange: () -> Unit
}

fun RBuilder.linksEditor(handler: RHandler<LinksEditorProps>): ReactElement =
    child(LinksEditor, handler = handler)
