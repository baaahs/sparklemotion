package baaahs.device

import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.Transport
import baaahs.getBang
import baaahs.gl.patch.ContentType
import baaahs.gl.render.RenderResults
import baaahs.gl.result.ResultStorage
import baaahs.model.Model
import baaahs.show.FeedBuilder
import baaahs.show.Shader
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

interface FixtureType {
    val id: String
    val title: String
    val feedBuilders: List<FeedBuilder<*>>
    val resultContentType: ContentType
    val likelyPipelines: List<Pair<ContentType, ContentType>>
    val errorIndicatorShader: Shader
    val emptyConfig: FixtureConfig
    val defaultConfig: FixtureConfig
    val serialModule: SerializersModule get() = SerializersModule {}

    fun createResultStorage(renderResults: RenderResults): ResultStorage
    fun createFixture(
        modelEntity: Model.Entity?,
        componentCount: Int,
        fixtureConfig: FixtureConfig,
        name: String,
        transport: Transport,
        model: Model
    ): Fixture

    class Serializer(private val knownFixtureTypes: Map<String, FixtureType>) : KSerializer<FixtureType> {
        override val descriptor: SerialDescriptor
            get() = String.serializer().descriptor

        override fun serialize(encoder: Encoder, value: FixtureType) {
            encoder.encodeString(value.id)
        }

        override fun deserialize(decoder: Decoder): FixtureType {
            return knownFixtureTypes.getBang(decoder.decodeString(), "fixture type")
        }
    }
}