package baaahs.scene

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.io.RemoteFsSerializer
import baaahs.model.ModelData
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.modules.SerializersModule

@Serializable
data class Scene(
    val model: ModelData,
    val controllers: Map<String, ControllerConfig> = emptyMap(),
    val fixtures: Map<String, FixtureConfigNew> = emptyMap(),
) {
    val title get() = model.title

    fun open(): OpenScene = OpenScene(this)

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
data class FixtureConfigNew(
    val controllerId: String,
    val entityId: String? = null,
//    val controllerConfig: FixtureControllerConfig? = null,
    val deviceType: DeviceType = PixelArrayDevice,
//    val deviceConfig: DeviceConfig? = null
)

//interface FixtureControllerConfig

//interface DeviceConfig