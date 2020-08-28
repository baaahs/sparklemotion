package baaahs.show

import baaahs.gl.patch.PortDiagram
import baaahs.gl.shader.InputPort
import baaahs.show.mutable.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class SourcePort {
    abstract val displayName: String
    open fun finalResolve(inputPort: InputPort, resolver: PortDiagram.Resolver): SourcePort = this
    abstract fun toMutable(editContext: EditContext): MutableSourcePort
}

@Serializable @SerialName("datasource")
data class DataSourceSourcePort(val dataSource: DataSource) : SourcePort() {
    override val displayName: String
        get() = dataSource.dataSourceName

    override fun toMutable(editContext: EditContext): MutableSourcePort =
        MutableDataSourceSourcePort(dataSource)
}

@Serializable @SerialName("shader-out")
data class ShaderOutSourcePort(val shaderInstance: ShaderInstance) : SourcePort() {
    override val displayName: String
        get() = "Shader \"${shaderInstance.shader.title}\" output"

    override fun toMutable(editContext: EditContext): MutableSourcePort =
        MutableShaderOutSourcePort(editContext.getShaderInstance(shaderInstance))

    companion object {
        const val ReturnValue = "_"
    }
}

@Serializable @SerialName("shader-channel")
data class ShaderChannelSourcePort(val shaderChannel: ShaderChannel) : SourcePort() {
    override val displayName: String
        get() = "channel(${shaderChannel.id})"

    override fun finalResolve(
        inputPort: InputPort,
        resolver: PortDiagram.Resolver
    ): SourcePort {
        val contentType = inputPort.contentType
            ?: return NoOpSourcePort()
        val resolved = resolver.resolve(shaderChannel, contentType)
        return if (resolved != null)
            ShaderOutSourcePort(resolved)
        else
            NoOpSourcePort()
    }

    override fun toMutable(editContext: EditContext): MutableSourcePort =
        MutableShaderChannelSourcePort(shaderChannel)
}

@Serializable @SerialName("const")
data class ConstSourcePort(val glsl: String) : SourcePort() {
    override val displayName: String
        get() = "const($glsl)"

    override fun toMutable(editContext: EditContext): MutableSourcePort =
        MutableConstSourcePort(glsl)
}

@Serializable
data class NoOpSourcePort(@Transient val `_`: Boolean = false) : SourcePort() {
    override val displayName: String
        get() = "Nothing"

    override fun toMutable(editContext: EditContext): MutableSourcePort =
        MutableNoOpSourcePort()
}