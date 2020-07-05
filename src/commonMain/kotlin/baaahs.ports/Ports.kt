package baaahs.ports

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.modules.SerializersModule

//val portRefModule = SerializersModule {
//    polymorphic(PortRef::class) {
//        DataSourceRef::class with DataSourceRef.serializer()
//        ShaderInPortRef::class with ShaderInPortRef.serializer()
//        ShaderOutPortRef::class with ShaderOutPortRef.serializer()
//        OutputPortRef::class with OutputPortRef.serializer()
//    }
//}



//interface ShaderPortRef: PortRef {
//    val shaderId: String
//}

//@Serializable
//data class ShaderInPortRef(override val shaderId: String, val portName: String) :
//    ShaderPortRef
//
//@Serializable
//data class ShaderOutPortRef(override val shaderId: String) : ShaderPortRef
//
//@Serializable
//data class OutputPortRef(
//    val dataType: String,
//    val name: String,
//    val pluginId: String? = null,
//    val pluginConfig: JsonElement = JsonNull
//) : PortRef
//
//@Serializable
//data class Link(val from: PortRef, val to: PortRef)
