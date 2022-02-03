package baaahs.scene

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.FixtureConfig
import baaahs.io.RemoteFsSerializer
import baaahs.mapper.TransportConfig
import baaahs.model.ModelData
import kotlinx.serialization.Contextual
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class Scene(
    val model: ModelData,
    val controllers: Map<String, ControllerConfig> = emptyMap(),
    val fixtures: List<FixtureMappingData> = emptyList(),
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
}

@Serializable
data class FixtureMappingData(
    val controllerId: String,
    val entityId: String? = null,
//    val controllerConfig: FixtureControllerConfig? = null,
    @Contextual
    val deviceType: DeviceType = PixelArrayDevice,
    val deviceConfig: FixtureConfig? = null,
    val transportConfig: TransportConfig? = null
)

//interface FixtureControllerConfig

//interface DeviceConfig