package baaahs.show

import baaahs.getBang
import baaahs.show.mutable.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PortRef {
    abstract fun dereference(mutableShow: MutableShow): MutableLink.Port
}

@Serializable @SerialName("datasource")
data class DataSourceRef(val dataSourceId: String) : PortRef() {
    override fun dereference(mutableShow: MutableShow): MutableLink.Port =
        mutableShow.dataSources.getBang(dataSourceId, "datasource")
}

interface ShaderPortRef {
    val shaderInstanceId: String
}

@Serializable @SerialName("shader-out")
data class ShaderOutPortRef(val shaderInstanceId: String) : PortRef() {

    override fun dereference(mutableShow: MutableShow): MutableLink.Port =
        MutableShaderOutPort(
            mutableShow.shaderInstances.getBang(shaderInstanceId, "shader instance"))

    companion object {
        const val ReturnValue = "_"
    }
}

@Serializable @SerialName("shader-channel")
data class ShaderChannelRef(val shaderChannel: ShaderChannel) : PortRef() {
    override fun dereference(mutableShow: MutableShow): MutableLink.Port =
        MutableShaderChannel(shaderChannel)
}

@Serializable @SerialName("output")
data class OutputPortRef(val portId: String) : PortRef() {
    override fun dereference(mutableShow: MutableShow): MutableLink.Port =
        MutableOutputPort(portId)
}

@Serializable @SerialName("const")
data class ConstPortRef(val glsl: String) : PortRef() {
    override fun dereference(mutableShow: MutableShow): MutableLink.Port =
        MutableConstPort(glsl)
}
