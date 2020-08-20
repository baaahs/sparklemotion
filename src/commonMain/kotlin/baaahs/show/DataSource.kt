package baaahs.show

import baaahs.ShowPlayer
import baaahs.camelize
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.shader.InputPort
import baaahs.plugin.BeatLinkPlugin
import baaahs.plugin.CorePlugin
import baaahs.plugin.Plugin
import baaahs.plugin.Plugins
import baaahs.show.mutable.MutableGadgetControl
import kotlinx.serialization.*
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObjectSerializer
import kotlinx.serialization.modules.SerializersModule


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

interface DataSource {
    val pluginPackage: String
    val dataSourceName: String
    fun isImplicit(): Boolean = false
    fun getType(): GlslType
    fun getVarName(id: String): String = "in_$id"

    fun getRenderType(): String? = null
    fun createFeed(showPlayer: ShowPlayer, plugin: Plugin, id: String): GlslProgram.DataFeed
    fun suggestId(): String = dataSourceName.camelize()

    fun buildControl(): MutableGadgetControl? = null

    companion object {
        val serialModule = SerializersModule {
            polymorphic(DataSource::class) {
//        CorePlugin.NoOp::class with CorePlugin.NoOp.serializer()
                CorePlugin.ResolutionDataSource::class with CorePlugin.ResolutionDataSource.serializer()
                CorePlugin.PreviewResolutionDataSource::class with CorePlugin.PreviewResolutionDataSource.serializer()
                CorePlugin.TimeDataSource::class with CorePlugin.TimeDataSource.serializer()
                CorePlugin.PixelCoordsTextureDataSource::class with CorePlugin.PixelCoordsTextureDataSource.serializer()
                CorePlugin.ModelInfoDataSource::class with CorePlugin.ModelInfoDataSource.serializer()
                CorePlugin.SliderDataSource::class with CorePlugin.SliderDataSource.serializer()
                CorePlugin.ColorPickerDataSource::class with CorePlugin.ColorPickerDataSource.serializer()
                CorePlugin.ColorPickerDataSource::class with CorePlugin.ColorPickerDataSource.serializer()
                CorePlugin.RadioButtonStripDataSource::class with CorePlugin.RadioButtonStripDataSource.serializer()
                CorePlugin.XyPadDataSource::class with CorePlugin.XyPadDataSource.serializer()

                BeatLinkPlugin.BeatLinkDataSource::class with BeatLinkPlugin.BeatLinkDataSource.serializer()
            }

//    polymorphic(ControlRef::class) {
//        SpecialControlRef::class with SpecialControlRef.serializer()
//        DataSourceRef::class with DataSourceRef.serializer()
//    }
        }
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

