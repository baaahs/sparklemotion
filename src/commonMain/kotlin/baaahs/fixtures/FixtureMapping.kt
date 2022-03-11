package baaahs.fixtures

import baaahs.controller.Controller
import baaahs.model.Model

data class FixtureMapping(
    val entity: Model.Entity?,
    val fixtureConfig: FixtureConfig,
    val transportConfig: TransportConfig? = null
) {
    private val fixtureType = fixtureConfig.fixtureType

    fun resolveFixtureConfig(controllerDefault: FixtureConfig?): FixtureConfig {
        return fixtureType.defaultConfig + listOfNotNull(
            controllerDefault,
            entity?.defaultFixtureConfig,
            fixtureConfig
        )
            .filter { it.fixtureType == fixtureType }
            .reduceOrNull { acc, config -> acc.plus(config) }
    }

    fun resolveTransportConfig(default: TransportConfig, controllerDefault: TransportConfig?): TransportConfig =
        default + listOfNotNull(
            controllerDefault,
            transportConfig
        ).reduceOrNull { acc, config -> acc.plus(config) }

    fun buildFixture(controller: Controller, model: Model): Fixture {
        val fixtureConfig = resolveFixtureConfig(controller.defaultFixtureConfig)

        val transportConfig = resolveTransportConfig(
            controller.transportType.emptyConfig, controller.defaultTransportConfig)

        val componentCount = fixtureConfig.componentCount ?: 1
        val bytesPerComponent = fixtureConfig.bytesPerComponent ?: error("Bytes per component unknown.")

        val name = "${entity?.name ?: "???"}@${controller.controllerId.name()}"

        val transport = controller.createTransport(
            entity, fixtureConfig, transportConfig, componentCount, bytesPerComponent
        )

        return fixtureType.createFixture(entity, componentCount, fixtureConfig, name, transport, model)
    }
}