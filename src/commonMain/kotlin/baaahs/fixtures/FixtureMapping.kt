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
        val cascadingFixtureConfigs = listOfNotNull(
            fixtureType.defaultConfig,
            controller.defaultFixtureConfig,
            entity?.defaultFixtureConfig,
            fixtureConfig
        )

        val fixtureConfig = cascadingFixtureConfigs
            .filter { it.fixtureType == fixtureType }
            .reduce { acc, config -> acc.plus(config) }

        val cascadingTransportConfigs = listOfNotNull(
            controller.defaultTransportConfig,
            transportConfig
        )
        val transportConfig = cascadingTransportConfigs
            .reduceOrNull { acc, config -> acc.plus(config) }

        val componentCount = fixtureConfig.componentCount ?: 1

        val name = "${entity?.name ?: "???"}@${controller.controllerId.name()}"
        val transport = controller.createTransport(entity, fixtureConfig, transportConfig, componentCount)
        return fixtureType.createFixture(entity, componentCount, fixtureConfig, name, transport, model)
    }
}