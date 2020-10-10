package baaahs.app.ui.editor

import baaahs.app.ui.appContext
import baaahs.englishize
import baaahs.gl.shader.InputPort
import baaahs.show.DataSource
import baaahs.show.ShaderChannel
import baaahs.show.mutable.*
import baaahs.ui.xComponent
import materialui.Icon
import materialui.components.table.table
import materialui.components.tablebody.tableBody
import materialui.components.tablecell.tdCell
import materialui.components.tablecell.thCell
import materialui.components.tablehead.tableHead
import materialui.components.tablerow.tableRow
import materialui.icons.Icons
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
        props.siblingMutableShaderInstances
            .minus(props.mutableShaderInstance)
            .sortedWith(MutableShaderInstance.defaultOrder)
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
                "change to ${inputPort.id}", props.mutableShaderInstance, props.editableManager
            ) { sourcePortOption: SourcePortOption? ->
                val incomingLinks = props.mutableShaderInstance.incomingLinks
                if (sourcePortOption == null) {
                    incomingLinks.remove(inputPort.id)
                } else {
                    incomingLinks[inputPort.id] = sourcePortOption.portEditor
                }
                props.editableManager.onChange()
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
                        code { +" (${inputPort.type.glslLiteral})" }
                    }
                }
            }
        }
    }
}


interface SourcePortOption {
    val title: String
    val portEditor: MutablePort
    val groupName: String?
    val icon: Icon
    fun matches(otherPort: MutablePort?): Boolean
    fun isAppropriateFor(inputPort: InputPort): Boolean
}

data class DataSourceOption(val dataSource: DataSource) : SourcePortOption {
    override val title: String get() = dataSource.dataSourceName
    override val portEditor: MutablePort get() = MutableDataSource(dataSource)
    override val groupName: String? get() = "Data Source:"
    override val icon: Icon get() = Icons.Input

    override fun matches(otherPort: MutablePort?): Boolean {
        return otherPort is MutableDataSource && otherPort.dataSource == dataSource
    }

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return dataSource.getType() == inputPort.type
    }
}

data class ShaderChannelOption(val shaderChannel: ShaderChannel) : SourcePortOption {
    override val title: String get() = shaderChannel.id.englishize()
    override val portEditor: MutablePort get() = MutableShaderChannel(shaderChannel)
    override val groupName: String? get() = "Channel:"
    override val icon: Icon get() = Icons.PowerInput

    override fun matches(otherPort: MutablePort?): Boolean {
        return otherPort is MutableShaderChannel && otherPort.shaderChannel == shaderChannel
    }

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return true // We don't have any type info for channel links.
    }
}

data class ShaderOption(val mutableShaderInstance: MutableShaderInstance) : SourcePortOption {
    override val title: String get() = mutableShaderInstance.mutableShader.title
    override val portEditor: MutablePort get() = MutableShaderOutPort(mutableShaderInstance)
    override val groupName: String? get() = "Shader output from:"
    override val icon: Icon get() = mutableShaderInstance.mutableShader.type.icon.getReactIcon()

    override fun matches(otherPort: MutablePort?): Boolean {
        return otherPort is MutableShaderOutPort &&
                otherPort.mutableShaderInstance == mutableShaderInstance
    }

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return inputPort.type ==
                mutableShaderInstance.mutableShader.type.resultContentType.glslType
    }
}


external interface LinksEditorProps : RProps {
    var editableManager: EditableManager
    var mutableShaderInstance: MutableShaderInstance
    var siblingMutableShaderInstances: List<MutableShaderInstance>
    var shaderChannels: Set<ShaderChannel>
}

fun RBuilder.linksEditor(handler: RHandler<LinksEditorProps>): ReactElement =
    child(LinksEditor, handler = handler)
