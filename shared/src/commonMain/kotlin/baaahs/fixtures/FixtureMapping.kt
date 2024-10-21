package baaahs.fixtures

import baaahs.controller.Controller
import baaahs.model.Model
import kotlinx.serialization.SerialName

data class FixtureMapping(
    val entity: Model.Entity?,
    @SerialName("fixtureConfig")
    val fixtureOptions: FixtureOptions,
    val transportConfig: TransportConfig? = null
) {
    private val fixtureType = fixtureOptions.fixtureType

    fun resolveFixtureOptions(controllerDefault: FixtureOptions?): FixtureOptions {
        val opts = listOfNotNull(
            fixtureType.defaultOptions,
            controllerDefault,
            entity?.defaultFixtureOptions,
            fixtureOptions
        )
        return opts
            .filter { it.fixtureType == fixtureType }
            .reduce { acc, config -> acc.plus(config) }
    }

    fun resolveTransportConfig(default: TransportConfig, controllerDefault: TransportConfig?): TransportConfig =
        default + listOfNotNull(
            controllerDefault,
            transportConfig
        ).reduceOrNull { acc, config -> acc.plus(config) }

    fun buildFixture(controller: Controller, model: Model): Fixture {
        val fixtureOptions = resolveFixtureOptions(controller.defaultFixtureOptions)
        val fixtureConfig = fixtureOptions.toConfig(entity, model, defaultComponentCount = 1)

        val name = "${entity?.name ?: "???"}@${controller.controllerId.name()}"

        val transportConfig = resolveTransportConfig(
            controller.transportType.emptyConfig, controller.defaultTransportConfig)
        val transport = controller.createTransport(entity, fixtureConfig, transportConfig)

        return Fixture(entity, fixtureConfig.componentCount, name, transport, fixtureConfig.fixtureType, fixtureConfig)
    }
}