package baaahs.ports

import baaahs.glshaders.InputPort
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.modules.SerializersModule

val portRefModule = SerializersModule {
    polymorphic(PortRef::class) {
        InputPortRef::class with InputPortRef.serializer()
        ShaderInPortRef::class with ShaderInPortRef.serializer()
        ShaderOutPortRef::class with ShaderOutPortRef.serializer()
        OutputPortRef::class with OutputPortRef.serializer()
    }
}

@Polymorphic
interface PortRef {
    infix fun linkTo(other: PortRef): Link =
        Link(this, other)
}

fun inputPortRef(inputPort: InputPort): InputPortRef =
    inputPortRef(
        inputPort.id,
        inputPort.type,
        inputPort.title,
        inputPort.pluginId,
        inputPort.pluginConfig
    )

// Workaround for https://github.com/Kotlin/kotlinx.serialization/issues/133
fun inputPortRef(
    id: String,
    type: String,
    title: String,
    pluginId: String? = null,
    pluginConfig: Map<String, String> = emptyMap(),
    varName: String = "in_$id",
    isImplicit: Boolean = false
) = InputPortRef(id, type, title, pluginId, pluginConfig, varName, isImplicit)

@Serializable
data class InputPortRef(
    val id: String,
    val type: String,
    val title: String,
    val pluginId: String? = null,
    val pluginConfig: Map<String, String> = emptyMap(),
    val varName: String,
    val isImplicit: Boolean = false
) : PortRef

interface ShaderPortRef: PortRef {
    val shaderId: String
}

@Serializable
data class ShaderInPortRef(override val shaderId: String, val portName: String) :
    ShaderPortRef

@Serializable
data class ShaderOutPortRef(override val shaderId: String) : ShaderPortRef

@Serializable
data class OutputPortRef(
    val type: String,
    val name: String,
    val pluginId: String? = null,
    val pluginConfig: JsonElement = JsonNull
) : PortRef

@Serializable
data class Link(val from: PortRef, val to: PortRef)
