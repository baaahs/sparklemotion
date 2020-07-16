package baaahs.show

import baaahs.getBang
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PortRef {
    infix fun linkTo(other: PortRef): Link =
        Link(this, other)

    abstract fun dereference(showEditor: ShowEditor): LinkEditor.Port
}

@Serializable @SerialName("datasource")
data class DataSourceRef(val dataSourceId: String) : PortRef() {
    override fun dereference(showEditor: ShowEditor): LinkEditor.Port =
        showEditor.dataSources.getBang(dataSourceId, "datasource")
}

interface ShaderPortRef {
    val shaderId: String
}

@Serializable @SerialName("shader-in")
data class ShaderInPortRef(override val shaderId: String, val portId: String) : PortRef(), ShaderPortRef {
    override fun dereference(showEditor: ShowEditor): LinkEditor.Port =
        showEditor.shaders.getBang(shaderId, "shader").inputPort(portId)
}

@Serializable @SerialName("shader-out")
data class ShaderOutPortRef(override val shaderId: String, val portId: String) : PortRef(), ShaderPortRef {
    fun isReturnValue() = portId == ReturnValue

    override fun dereference(showEditor: ShowEditor): LinkEditor.Port =
        showEditor.shaders.getBang(shaderId, "shader").outputPort(portId)

    companion object {
        const val ReturnValue = "_"
    }
}

@Serializable @SerialName("output")
data class OutputPortRef(val portId: String) : PortRef() {
    override fun dereference(showEditor: ShowEditor): LinkEditor.Port =
        OutputPortEditor(portId)
}
