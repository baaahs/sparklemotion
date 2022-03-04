package baaahs.scene

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.controller.ControllerId
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.FixtureMapping
import baaahs.fixtures.TransportConfig
import baaahs.io.RemoteFsSerializer
import baaahs.model.Model
import baaahs.model.ModelData
import kotlinx.serialization.Polymorphic
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

    fun edit(): MutableControllerConfig
}

@Serializable
data class FixtureMappingData(
    val entityId: String? = null,
    val deviceConfig: FixtureConfig? = null,
    val transportConfig: TransportConfig? = null
) {
    fun edit() = MutableFixtureMapping(this)

    fun open(model: Model) =
        FixtureMapping(
            entityId?.let { model.findEntityByName(it) },
            PixelArrayDevice,
            deviceConfig,
            transportConfig,
            null
        )
}

//interface FixtureControllerConfig

//interface DeviceConfig