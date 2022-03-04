package baaahs.fixtures

import baaahs.controller.Controller
import baaahs.device.DeviceType
import baaahs.geom.Vector3F
import baaahs.model.Model

data class FixtureMapping(
    val entity: Model.Entity?,
    val fixtureType: DeviceType,
    val fixtureConfig: FixtureConfig? = null,
    val transportConfig: TransportConfig? = null,

    /** Pixel's estimated position within the model. */
    @Deprecated("Use fixtureConfig for pixelLocations instead.")
    val pixelLocations: List<Vector3F?>? = null
) {
    init {
        if (fixtureConfig != null && fixtureConfig.deviceType != fixtureType) {
            error("Fixture type mismatch for mapping (entity=${entity?.name})")
        }
    }

    fun buildFixture(controller: Controller, model: Model): Fixture {
        val cascadingConfigs = listOfNotNull(
            fixtureType.defaultConfig,
            controller.defaultFixtureConfig,
            entity?.defaultFixtureConfig,
            fixtureConfig
        )

        val typedConfigs = cascadingConfigs.filter { it.deviceType == fixtureType }
        val fixtureConfig = typedConfigs.reduce { acc, config -> acc.plus(config) }

        val componentCount = fixtureConfig.componentCount ?: 1
        val pixelLocations = pixelLocations
            ?.map { it ?: Vector3F(0f, 0f, 0f) }
            ?: fixtureConfig.generatePixelLocations(componentCount, entity, model)
            ?: emptyList()

        val transportConfig = transportConfig

        val name = "${entity?.name ?: "???"}@${controller.controllerId.name()}"
        val transport = controller.createTransport(entity, fixtureConfig, transportConfig, componentCount)
        return fixtureType.createFixture(entity, componentCount, fixtureConfig, name, transport, pixelLocations)
    }
}