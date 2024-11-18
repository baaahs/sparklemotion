package baaahs.scene

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.controller.ControllerId
import baaahs.fixtures.FixtureOptions
import baaahs.fixtures.FixturePreview
import baaahs.fixtures.FixturePreviewError
import baaahs.fixtures.TransportConfig
import baaahs.io.RemoteFsSerializer
import baaahs.model.*
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class Scene(
    val model: ModelData,
    val entities: Map<EntityId, EntityData> = emptyMap(),
    val controllers: Map<ControllerId, ControllerConfig> = emptyMap()
) {
    val title get() = model.title

    fun edit(): MutableScene = MutableSceneBuilder(this).build()
    fun open(): OpenScene = SceneOpener(this).open()

    companion object {
        val Empty: Scene = Scene(ModelData("Untitled"))

        val Fallback = Scene(
            ModelData("Fallback Scene", listOf("grid"), ModelUnit.Centimeters),
            mapOf(
                "grid" to GridData("Grid", columns = 320, rows = 240, columnGap = 1.25f, rowGap = 1.25f, zigZag = true)
            ),
            emptyMap()
        )

        fun createTopic(
            serializersModule: SerializersModule,
            fsSerializer: RemoteFsSerializer
        ): PubSub.Topic<DocumentState<Scene, Unit>?> {
            return PubSub.Topic(
                "sceneEditState",
                DocumentState.serializer(serializer(), Unit.serializer()).nullable,
                SerializersModule {
                    include(serializersModule)
                    include(fsSerializer.serialModule)
                }
            )
        }
    }
}

@Polymorphic
interface ControllerConfig {
    val controllerType: String
    val title: String
    val fixtures: List<FixtureMappingData>
    val defaultFixtureOptions: FixtureOptions?
    val emptyTransportConfig: TransportConfig
    val defaultTransportConfig: TransportConfig?

    fun edit(fixtureMappings: MutableList<MutableFixtureMapping>): MutableControllerConfig

    fun buildFixturePreviews(sceneOpener: SceneOpener): List<FixturePreview> {
        return fixtures.map { fixtureMappingData ->
            try {
                val fixtureMapping = with (sceneOpener) { fixtureMappingData.open() }
                val fixtureOptions = fixtureMapping.resolveFixtureOptions(defaultFixtureOptions)
                val transportConfig = fixtureMapping.resolveTransportConfig(emptyTransportConfig, defaultTransportConfig)
                createFixturePreview(fixtureOptions, transportConfig)
            } catch (e: Exception) {
                FixturePreviewError(e)
            }
        }
    }

    fun createFixturePreview(fixtureOptions: FixtureOptions, transportConfig: TransportConfig): FixturePreview
}

@Serializable
data class FixtureMappingData(
    val entityId: String? = null,
    @SerialName("fixtureConfig")
    val fixtureOptions: FixtureOptions?,
    val transportConfig: TransportConfig? = null
)

//interface FixtureControllerConfig

//interface DeviceConfig