package baaahs.plugin

import baaahs.app.ui.editor.PortLinkOption
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.show.AddControlMenuItem
import baaahs.show.Control
import baaahs.show.DataSource
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

    fun resolveDataSource(inputPort: InputPort): DataSource

    fun suggestDataSources(
        inputPort: InputPort,
        suggestedContentTypes: Set<ContentType>
    ): List<PortLinkOption>

    fun resolveContentType(type: String): ContentType?

    fun suggestContentTypes(inputPort: InputPort): Collection<ContentType>

    fun findDataSource(
        resourceName: String,
        inputPort: InputPort
    ): DataSource?

    fun getAddControlMenuItems(): List<AddControlMenuItem>

    fun getControlSerializers(): List<SerializerRegistrar<out Control>>
    fun getDataSourceSerializers(): List<SerializerRegistrar<out DataSource>>
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