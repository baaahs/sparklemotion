package baaahs.app.ui.editor

import baaahs.app.ui.CommonIcons
import baaahs.englishize
import baaahs.gl.shader.InputPort
import baaahs.show.DataSource
import baaahs.show.ShaderChannel
import baaahs.show.mutable.*
import baaahs.ui.Icon

class ShaderInstanceEditOptions {
    fun buildLinkOptions(
        mutableShow: MutableShow
    ): List<SourcePortOption> {
        val listOf = listOf<SourcePortOption>()

        return listOf
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
    override val icon: Icon get() = CommonIcons.DataSource

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
    override val icon: Icon get() = CommonIcons.ShaderChannel

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
    override val icon: Icon get() = mutableShaderInstance.mutableShader.type.icon

    override fun matches(otherPort: MutablePort?): Boolean {
        return otherPort is MutableShaderOutPort &&
                otherPort.mutableShaderInstance == mutableShaderInstance
    }

    override fun isAppropriateFor(inputPort: InputPort): Boolean {
        return inputPort.type ==
                mutableShaderInstance.mutableShader.type.resultContentType.glslType
    }
}

object NoSourcePortOption : SourcePortOption {
    override val title: String get() = "Nothing"
    override val portEditor: MutablePort get() = error("not implemented")
    override val groupName: String? get() = null
    override val icon: Icon get() = CommonIcons.None
    override fun matches(otherPort: MutablePort?): Boolean = otherPort == null
    override fun isAppropriateFor(inputPort: InputPort): Boolean = true
}

object NewSourcePortOption : SourcePortOption {
    override val title: String get() = "Create New…"
    override val portEditor: MutablePort get() = error("not implemented")
    override val groupName: String? get() = null
    override val icon: Icon get() = CommonIcons.Add
    override fun matches(otherPort: MutablePort?): Boolean = false
    override fun isAppropriateFor(inputPort: InputPort): Boolean = true
}
