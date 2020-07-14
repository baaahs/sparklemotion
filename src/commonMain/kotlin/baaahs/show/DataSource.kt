package baaahs.show

import baaahs.ShowResources
import baaahs.camelize
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.InputPort
import baaahs.glshaders.Plugins
import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObjectSerializer


interface DataSourceBuilder<T : DataSource> {
    val resourceName: String
    fun suggestDataSources(inputPort: InputPort): List<T> {
        return if (looksValid(inputPort)) {
            listOf(build(inputPort))
        } else emptyList()
    }
    fun looksValid(inputPort: InputPort): Boolean = false
    fun build(inputPort: InputPort): T
}

interface DataSource : Control {
    val dataSourceName: String
    fun isImplicit(): Boolean = false
    fun getType(): String
    fun getVarName(id: String): String = "in_$id"

    fun getRenderType(): String? = null
    fun createFeed(showResources: ShowResources, id: String): GlslProgram.DataFeed
    fun suggestId(): String = dataSourceName.camelize()

    override fun toControlRef(showBuilder: ShowBuilder): ControlRef {
        return ControlRef(ControlRef.Type.DataSource, showBuilder.idFor(this))
    }
}

class DataSourceSerializer(val plugins: Plugins): KSerializer<DataSource> {
    override val descriptor: SerialDescriptor = SerialDescriptor("DataSource", StructureKind.MAP) {
            element("pluginId", String.serializer().descriptor)
            element("pluginData", MapSerializer(JsonElement.serializer(), JsonElement.serializer()).descriptor)
        }

    override fun deserialize(decoder: Decoder): DataSource {
        val map = decoder.beginStructure(descriptor)
        val pluginId = map.decodeStringElement(descriptor, 0)
        val pluginData = map.decodeSerializableElement(descriptor, 1, JsonObjectSerializer)
        val dataSource = plugins.decodeDataSource(pluginId, pluginData)
        map.endStructure(descriptor)
        return dataSource
    }

    override fun serialize(encoder: Encoder, value: DataSource) {
        val map = encoder.beginStructure(descriptor)

//        val pluginId = plugins.dataSourceType(value)
//        val pluginData = plugins.serialize(value)
//        map.encodeStringElement(descriptor, 0, pluginId)
//        map.encodeSerializableElement(descriptor, 1, JsonObjectSerializer, pluginData)
        map.endStructure(descriptor)
    }
}

