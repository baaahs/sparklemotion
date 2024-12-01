package baaahs.scene

import baaahs.client.document.OpenDocument
import baaahs.controller.Controller
import baaahs.controller.ControllerId
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureMapping
import baaahs.fixtures.FixtureOptions
import baaahs.fixtures.TransportConfig
import baaahs.mapping.MappingManager
import baaahs.model.Model
import baaahs.sm.webapi.Problem
import baaahs.util.Logger

class OpenScene(
    val model: Model,
    val controllers: Map<ControllerId, OpenControllerConfig<*>> = emptyMap(),
    val fixtureMappings: Map<ControllerId, List<FixtureMapping>> = emptyMap(),
    val isFallback: Boolean = false
) : OpenDocument<Scene> {
    override val title: String
        get() = model.name

    val allProblems: List<Problem>
        get() = buildList {
            model.visit { entity -> addAll(entity.problems) }
//            controllers.values.visit { controller -> addAll(controller.problems) }
//            fixtures.visit { fixture -> addAll(fixture.problems) }
        }

    fun resolveFixtures(controller: Controller, mappingManager: MappingManager): List<Fixture> {
        val fixtureMappings = relevantFixtureMappings(controller.controllerId, mappingManager, getAnonymousFixtureMappings = {
            controller.getAnonymousFixtureMappings()
        })
        val fixtureResolver = controller.createFixtureResolver()
        return fixtureMappings.map { mapping ->
            mapping.buildFixture(controller, fixtureResolver, model)
        }
    }

    fun relevantFixtureMappings(
        controllerId: ControllerId,
        mappingManager: MappingManager,
        getAnonymousFixtureMappings: () -> List<FixtureMapping>
    ): List<FixtureMapping> {
        val mappingsFromScene = fixtureMappings[controllerId] ?: emptyList()
        val mappingsFromLegacy = mappingManager.findMappings(controllerId)
        return (mappingsFromScene + mappingsFromLegacy)
            .ifEmpty { getAnonymousFixtureMappings() }
    }

    companion object {
        private val logger = Logger<OpenScene>()
    }
}

class OpenControllerConfig<T : ControllerConfig>(
    val id: ControllerId,
    val controllerConfig: T
) {
    val controllerType: String
        get() = controllerConfig.controllerType
    val title: String
        get() = controllerConfig.title
    val defaultFixtureOptions: FixtureOptions?
        get() = controllerConfig.defaultFixtureOptions
    val emptyTransportConfig: TransportConfig
        get() = controllerConfig.emptyTransportConfig
    val defaultTransportConfig: TransportConfig?
        get() = controllerConfig.defaultTransportConfig

    override fun toString(): String {
        return "OpenControllerConfig(id=$id, controllerConfig=$controllerConfig)"
    }
}