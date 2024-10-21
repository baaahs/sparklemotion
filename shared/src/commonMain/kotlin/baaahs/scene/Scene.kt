package baaahs.scene

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.controller.ControllerId
import baaahs.fixtures.*
import baaahs.io.RemoteFsSerializer
import baaahs.model.GridData
import baaahs.model.Model
import baaahs.model.ModelData
import baaahs.model.ModelUnit
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class Scene(
    val model: ModelData,
    val controllers: Map<ControllerId, ControllerConfig> = emptyMap()
) {
    val title get() = model.title

    fun edit(): MutableScene = MutableScene(this)
    fun open(): OpenScene = OpenScene.open(this)

    companion object {
        val Empty: Scene = Scene(ModelData("Untitled", emptyList()))

        val Fallback = Scene(
            ModelData("Fallback Scene", listOf(
                GridData("Grid", columns = 320, rows = 240, columnGap = 1.25f, rowGap = 1.25f, zigZag = true)
            ), ModelUnit.Centimeters),
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

    fun edit(): MutableControllerConfig

    fun buildFixturePreviews(tempModel: Model): List<FixturePreview> {
        return fixtures.map { fixtureMappingData ->
            try {
                val fixtureMapping = fixtureMappingData.open(tempModel)
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
    val fixtureOptions: FixtureOptions,
    val transportConfig: TransportConfig? = null
) {
    fun edit() = MutableFixtureMapping(this)

    fun open(model: Model) =
        FixtureMapping(
            entityId?.let { model.findEntityByName(it) },
            fixtureOptions,
            transportConfig
        )
}

//interface FixtureControllerConfig

//interface DeviceConfig