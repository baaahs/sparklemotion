package baaahs.scene

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.controller.ControllerId
import baaahs.controller.sim.ControllerSimulator
import baaahs.fixtures.*
import baaahs.io.RemoteFsSerializer
import baaahs.model.Model
import baaahs.model.ModelData
import baaahs.sim.SimulationEnv
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
    val defaultFixtureConfig: FixtureConfig?
    val emptyTransportConfig: TransportConfig
    val defaultTransportConfig: TransportConfig?

    fun edit(): MutableControllerConfig

    fun buildFixturePreviews(tempModel: Model): List<FixturePreview> {
        return fixtures.map { fixtureMappingData ->
            try {
                val fixtureMapping = fixtureMappingData.open(tempModel)
                val fixtureConfig = fixtureMapping.resolveFixtureConfig(defaultFixtureConfig)
                val transportConfig = fixtureMapping.resolveTransportConfig(emptyTransportConfig, defaultTransportConfig)
                createFixturePreview(fixtureConfig, transportConfig)
            } catch (e: Exception) {
                FixturePreviewError(e)
            }
        }
    }

    fun createFixturePreview(fixtureConfig: FixtureConfig, transportConfig: TransportConfig): FixturePreview
    fun createSimulator(controllerId: ControllerId, simulationEnv: SimulationEnv): ControllerSimulator
}

@Serializable
data class FixtureMappingData(
    val entityId: String? = null,
    val fixtureConfig: FixtureConfig,
    val transportConfig: TransportConfig? = null
) {
    fun edit() = MutableFixtureMapping(this)

    fun open(model: Model) =
        FixtureMapping(
            entityId?.let { model.findEntityByName(it) },
            fixtureConfig,
            transportConfig
        )
}

//interface FixtureControllerConfig

//interface DeviceConfig