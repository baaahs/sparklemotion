package baaahs.device

import baaahs.fixtures.FixtureConfig
import baaahs.getBang
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderResults
import baaahs.gl.result.ResultStorage
import baaahs.show.DataSourceBuilder
import baaahs.show.Shader
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

interface DeviceType {
    val id: String
    val title: String
    val dataSourceBuilders: List<DataSourceBuilder<*>>
    val resultContentType: ContentType
    val likelyPipelines: List<Pair<ContentType, ContentType>>
    val errorIndicatorShader: Shader
    val defaultConfig: FixtureConfig
    val defaultPixelCount: Int? get() = null
    val serialModule: SerializersModule get() = SerializersModule {}

    fun createResultStorage(renderResults: RenderResults): ResultStorage

    class Serializer(private val knownDeviceTypes: Map<String, DeviceType>) : KSerializer<DeviceType> {
        override val descriptor: SerialDescriptor
            get() = String.serializer().descriptor

        override fun serialize(encoder: Encoder, value: DeviceType) {
            encoder.encodeString(value.id)
        }

        override fun deserialize(decoder: Decoder): DeviceType {
            return knownDeviceTypes.getBang(decoder.decodeString(), "device type")
        }
    }
}