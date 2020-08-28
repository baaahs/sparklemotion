package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.englishize
import baaahs.gl.shader.InputPort
import baaahs.show.*
import baaahs.show.mutable.*
import materialui.Icon
import materialui.Input
import materialui.PowerInput
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import react.*
import react.dom.b
import react.dom.code
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

val LinksEditor = xComponent<LinksEditorProps>("LinksEditor") { props ->
    val appContext = useContext(appContext)

    val shaderChannelOptions = props.shaderChannels
        .sortedBy { it.id }
        .map { shaderChannel -> ShaderChannelOption(shaderChannel) }

    val shaderOptions =
        props.siblingShaderInstances
            .filterNot { it.id == props.mutableShaderInstance.id }
            .sortedWith(ShaderInstance.defaultOrder)
            .map { instance -> ShaderOption(instance) }

    val dataSourceOptions =
        appContext.showPlayer.dataSources
            .sortedBy { it.dataSourceName }.map { dataSource -> DataSourceOption(dataSource) }

    val sourcePortOptions =
        memo(props.siblingShaderInstances, props.mutableShaderInstance, appContext.showPlayer.dataSources) {
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
                    incomingLinks[inputPort.id] = MutableSourcePort(sourcePortOption.sourcePort)
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
                            attrs.currentSourceSourcePort = currentSourcePort
                            attrs.sourcePortOptions = sourcePortOptions
                            attrs.onChange = handleSourceChange
                        }
                    }

                    tdCell {
                        b { +inputPort.title }
                        code { +" (${inputPort.type.glslLiteral})" }
                    }
                }
            }
        }
    }
}


interface SourcePortOption {
    val title: String
    val sourcePort: SourcePort
    val groupName: String?
    val icon: Icon
    fun isAppropriateFor(inputPort: InputPort): Boolean
}

data class DataSourceOption(val dataSource: DataSource) : SourcePortOption {
    override val title: String get() = dataSource.dataSourceName
    override val sourcePort: SourcePort get() = DataSourceSourcePort(dataSource)
    override val groupName: String? get() = "Data Source:"
    override val icon: Icon get() = Input

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return dataSource.getType() == inputPort.type
    }
}

data class ShaderChannelOption(val shaderChannel: ShaderChannel) : SourcePortOption {
    override val title: String get() = shaderChannel.id.englishize()
    override val sourcePort: SourcePort get() = ShaderChannelSourcePort(shaderChannel)
    override val groupName: String? get() = "Channel:"
    override val icon: Icon get() = PowerInput

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return true // We don't have any type info for channel links.
    }
}

data class ShaderOption(val shaderInstance: ShaderInstance) : SourcePortOption {
    override val title: String get() = shaderInstance.shader.title
    override val sourcePort: SourcePort get() = ShaderOutSourcePort(shaderInstance)
    override val groupName: String? get() = "Shader output from:"
    override val icon: Icon get() = Icons.forShader(shaderInstance.shader.type)

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return inputPort.type ==
                shaderInstance.shader.type.resultContentType.glslType
    }
}


external interface LinksEditorProps : RProps {
    var mutableShaderInstance: MutableShaderInstance
    var siblingShaderInstances: List<ShaderInstance>
    var shaderChannels: Set<ShaderChannel>
    var onChange: () -> Unit
}

fun RBuilder.linksEditor(handler: RHandler<LinksEditorProps>): ReactElement =
    child(LinksEditor, handler = handler)
