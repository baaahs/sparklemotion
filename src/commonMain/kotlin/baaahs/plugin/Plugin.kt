package baaahs.plugin

import baaahs.device.DeviceType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.dialect.ShaderDialect
import baaahs.gl.shader.type.ShaderType
import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.ui.Icon
import baaahs.util.Clock
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlin.reflect.KClass

interface Plugin {
    val packageName: String
    val title: String

    val addControlMenuItems: List<AddControlMenuItem>
        get() = emptyList()

    val contentTypes: List<ContentType>
        get() = emptyList()

    val controlSerializers: List<SerializerRegistrar<out Control>>
        get() = emptyList()

    val dataSourceBuilders: List<DataSourceBuilder<out DataSource>>
        get() = emptyList()

    val deviceTypes: List<DeviceType>
        get() = emptyList()

    val shaderDialects: List<ShaderDialect>
        get() = emptyList()

    val shaderTypes: List<ShaderType>
        get() = emptyList()
}

class SerializerRegistrar<T : Any>(val klass: KClass<T>, val serializer: KSerializer<T>) {
    fun register(polymorphicModuleBuilder: PolymorphicModuleBuilder<T>) {
        polymorphicModuleBuilder.subclass(klass, serializer)
    }

    override fun toString(): String = "SerializerRegistrar[${klass.simpleName} -> $serializer]"
}

@OptIn(InternalSerializationApi::class)
class ObjectSerializer<T : Any>(serialName: String, private val objectInstance: T) : KSerializer<T> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor(serialName, StructureKind.OBJECT)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.beginStructure(descriptor).endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): T {
        decoder.beginStructure(descriptor).endStructure(descriptor)
        return objectInstance
    }

    override fun toString(): String =
        "[ObjectSerializer ${descriptor.serialName} -> ${objectInstance::class.simpleName}]"
}

inline fun <reified T : Any> classSerializer(serializer: KSerializer<T>) =
    SerializerRegistrar(T::class, serializer)

inline fun <reified T : Any> objectSerializer(serialName: String, t: T) =
    classSerializer(ObjectSerializer(serialName, t))

interface PluginBuilder {
    val id: String
    fun build(pluginContext: PluginContext): Plugin
}

class PluginContext(
    val clock: Clock
)

data class AddControlMenuItem(
    val label: String,
    val icon: Icon,
    val createControlFn: (mutableShow: MutableShow) -> MutableControl
)