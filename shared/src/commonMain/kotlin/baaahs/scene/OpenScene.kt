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
    val isFallback: Boolean = false
) : OpenDocument {
    override val title: String
        get() = model.name

    val allProblems: List<Problem>
        get() = buildList {
            model.visit { entity -> addAll(entity.problems) }

            controllers.forEach { (controllerId, openControllerConfig) ->
                openControllerConfig.controllerConfig.fixtures.forEach { data ->
                    val entity = data.entityId?.let { model.findEntityByNameOrNull(it) }

                    if (data.entityId != null && entity == null) {
                        add(
                            Problem(
                                "No such entity \"${data.entityId}\".",
                                "No such entity \"${data.entityId}\" found in model, " +
                                        "but there's a fixture mapping from \"${controllerId.name()}\" for it."
                            )
                        )
                    }
                }
            }
//            controllers.values.visit { controller -> addAll(controller.problems) }
//            fixtures.visit { fixture -> addAll(fixture.problems) }
        }

    fun resolveFixtures(controller: Controller, mappingManager: MappingManager): List<Fixture> {
        controller.beforeFixtureResolution()
        try {
            return relevantFixtureMappings(controller, mappingManager).map { mapping ->
                mapping.buildFixture(controller, model)
            }
        } finally {
            controller.afterFixtureResolution()
        }
    }

    fun relevantFixtureMappings(controller: Controller, mappingManager: MappingManager): List<FixtureMapping> {
        val openConfig = controllers[controller.controllerId]
        val mappingsFromScene = openConfig?.fixtureMappings
            ?: emptyList()

        val mappingsFromLegacy = mappingManager.findMappings(controller.controllerId)
        return (mappingsFromScene + mappingsFromLegacy)
            .ifEmpty { controller.getAnonymousFixtureMappings() }
    }

    companion object {
        private val logger = Logger<OpenScene>()
    }
}

class OpenControllerConfig<T : ControllerConfig>(
    val id: ControllerId,
    val controllerConfig: T,
    val fixtureMappings: List<FixtureMapping>
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
        return "OpenControllerConfig(id=$id, controllerConfig=$controllerConfig, fixtureMappings=$fixtureMappings)"
    }
}