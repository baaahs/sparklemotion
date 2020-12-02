package baaahs.show

import baaahs.getBang
import baaahs.gl.glsl.GlslType
import baaahs.show.mutable.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PortRef {
    abstract fun dereference(mutableShow: MutableShow): MutablePort
}

@Serializable @SerialName("datasource")
data class DataSourceRef(val dataSourceId: String) : PortRef() {
    override fun dereference(mutableShow: MutableShow): MutablePort =
        mutableShow.dataSources.getBang(dataSourceId, "datasource")
}

interface ShaderPortRef {
    val shaderInstanceId: String
}

@Serializable @SerialName("shader-out")
data class ShaderOutPortRef(val shaderInstanceId: String) : PortRef() {

    override fun dereference(mutableShow: MutableShow): MutablePort =
        MutableShaderOutPort(mutableShow.findShaderInstance(shaderInstanceId))

    companion object {
        const val ReturnValue = "_"
    }
}

@Serializable @SerialName("shader-channel")
data class ShaderChannelRef(val shaderChannel: ShaderChannel) : PortRef() {
    override fun dereference(mutableShow: MutableShow): MutablePort =
        MutableShaderChannel(shaderChannel.id)
}

@Serializable @SerialName("output")
data class OutputPortRef(val portId: String) : PortRef() {
    override fun dereference(mutableShow: MutableShow): MutablePort =
        MutableOutputPort(portId)
}

@Serializable @SerialName("const")
data class ConstPortRef(val glsl: String, val type: String) : PortRef() {
    override fun dereference(mutableShow: MutableShow): MutablePort =
        MutableConstPort(glsl, GlslType.from(type))
}
