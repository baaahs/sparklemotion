package baaahs.scene

import baaahs.fixtures.DeviceType
import baaahs.fixtures.PixelArrayDevice
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
data class SceneConfig(
    val controllers: Map<String, ControllerConfig>,
    val fixtures: Map<String, FixtureConfig>,
)

@Polymorphic
interface ControllerConfig {
    val controllerType: String
    val id: String
    val title: String
}

@Serializable
data class FixtureConfig(
    val controllerId: String,
    val entityId: String? = null,
//    val controllerConfig: FixtureControllerConfig? = null,
    val deviceType: DeviceType = PixelArrayDevice,
//    val deviceConfig: DeviceConfig? = null
)

//interface FixtureControllerConfig

//interface DeviceConfig