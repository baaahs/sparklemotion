package baaahs.show

import baaahs.getBang
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PortRef {
    abstract fun dereference(showEditor: ShowEditor): LinkEditor.Port
}

@Serializable @SerialName("datasource")
data class DataSourceRef(val dataSourceId: String) : PortRef() {
    override fun dereference(showEditor: ShowEditor): LinkEditor.Port =
        showEditor.dataSources.getBang(dataSourceId, "datasource")
}

interface ShaderPortRef {
    val shaderInstanceId: String
}

@Serializable @SerialName("shader-out")
data class ShaderOutPortRef(val shaderInstanceId: String, val portId: String = ReturnValue) : PortRef() {
    fun isReturnValue() = portId == ReturnValue

    override fun dereference(showEditor: ShowEditor): LinkEditor.Port =
        ShaderOutPortEditor(showEditor.shaderInstances.getBang(shaderInstanceId, "shader instance"), portId)

    companion object {
        const val ReturnValue = "_"
    }
}

@Serializable @SerialName("shader-channel")
data class ShaderChannelRef(val shaderChannel: ShaderChannel) : PortRef() {
    override fun dereference(showEditor: ShowEditor): LinkEditor.Port =
        ShaderChannelEditor(shaderChannel)
}

@Serializable @SerialName("output")
data class OutputPortRef(val portId: String) : PortRef() {
    override fun dereference(showEditor: ShowEditor): LinkEditor.Port =
        OutputPortEditor(portId)
}
