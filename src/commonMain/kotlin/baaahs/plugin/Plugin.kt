package baaahs.plugin

import baaahs.fixtures.DeviceType
import baaahs.gl.patch.ContentType
import baaahs.show.AddControlMenuItem
import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
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
    val contentTypes: List<ContentType>
    val controlSerializers: List<SerializerRegistrar<out Control>>
    val dataSourceBuilders: List<DataSourceBuilder<out DataSource>>
    val dataSourceSerializers: List<SerializerRegistrar<out DataSource>>
    val deviceTypes: List<DeviceType>
        get() = emptyList()
}

class SerializerRegistrar<T : Any>(val klass: KClass<T>, val serializer: KSerializer<T>) {
    fun register(polymorphicModuleBuilder: PolymorphicModuleBuilder<T>) {
        polymorphicModuleBuilder.subclass(klass, serializer)
    }
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