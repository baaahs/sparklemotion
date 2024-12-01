package baaahs.fixtures

import baaahs.controller.Controller
import baaahs.controller.FixtureResolver
import baaahs.model.Model
import baaahs.scene.ControllerConfig
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

    fun resolveTransportConfig(controller: ControllerConfig): TransportConfig {
        val transportDefault = controller.emptyTransportConfig
        val controllerDefault = controller.defaultTransportConfig
        return reduce(transportDefault, controllerDefault, this@FixtureMapping.transportConfig)
    }

    fun resolveTransportConfig(controller: Controller): TransportConfig {
        val transportDefault = controller.transportType.emptyConfig
        val controllerDefault = controller.defaultTransportConfig
        return reduce(transportDefault, controllerDefault, transportConfig)
    }

    private fun reduce(vararg transportConfigs: TransportConfig?): TransportConfig {
        return transportConfigs.toList().filterNotNull()
            .reduce { acc, config -> acc + config }
    }

    fun buildFixture(controller: Controller, fixtureResolver: FixtureResolver, model: Model): Fixture {
        val fixtureOptions = resolveFixtureOptions(controller.defaultFixtureOptions)
        val fixtureConfig = fixtureOptions.toConfig(entity, model, defaultComponentCount = 1)

        val name = "${entity?.name ?: "???"}@${controller.controllerId.name()}"

        val transportConfig = resolveTransportConfig(controller)
        val transport = fixtureResolver.createTransport(entity, fixtureConfig, transportConfig)

        return Fixture(entity, fixtureConfig.componentCount, name, transport, fixtureConfig.fixtureType, fixtureConfig)
    }
}