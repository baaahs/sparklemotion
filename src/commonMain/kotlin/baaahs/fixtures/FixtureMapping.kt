package baaahs.fixtures

import baaahs.controller.Controller
import baaahs.device.FixtureType
import baaahs.model.Model

data class FixtureMapping(
    val entity: Model.Entity?,
    val fixtureType: FixtureType,
    val fixtureConfig: FixtureConfig? = null,
    val transportConfig: TransportConfig? = null
) {
    init {
        if (fixtureConfig != null && fixtureConfig.fixtureType != fixtureType) {
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

        val typedConfigs = cascadingConfigs.filter { it.fixtureType == fixtureType }
        val fixtureConfig = typedConfigs.reduce { acc, config -> acc.plus(config) }

        val componentCount = fixtureConfig.componentCount ?: 1
        val transportConfig = transportConfig

        val name = "${entity?.name ?: "???"}@${controller.controllerId.name()}"
        val transport = controller.createTransport(entity, fixtureConfig, transportConfig, componentCount)
        return fixtureType.createFixture(entity, componentCount, fixtureConfig, name, transport, model)
    }
}