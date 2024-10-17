package baaahs.scene

import baaahs.client.document.OpenDocument
import baaahs.controller.Controller
import baaahs.controller.ControllerId
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureMapping
import baaahs.mapping.MappingManager
import baaahs.model.Model
import baaahs.sm.webapi.Problem
import baaahs.util.Logger

class OpenScene(
    val model: Model,
    val controllers: Map<ControllerId, ControllerConfig> = emptyMap(),
    val isFallback: Boolean = false
) : OpenDocument {
    override val title: String
        get() = model.name

    val allProblems: List<Problem>
        get() = buildList {
            model.visit { entity -> addAll(entity.problems) }

            controllers.forEach { (controllerId, controllerConfig) ->
                controllerConfig.fixtures.forEach { data ->
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
        val mappingsFromScene = (controllers[controller.controllerId]
            ?.fixtures?.map { fixtureMappingData -> fixtureMappingData.open(model) }
            ?: emptyList())
        val mappingsFromLegacy = mappingManager.findMappings(controller.controllerId)
        return (mappingsFromScene + mappingsFromLegacy)
            .ifEmpty { controller.getAnonymousFixtureMappings() }
    }

    companion object {
        private val logger = Logger<OpenScene>()

        fun open(scene: Scene): OpenScene {
            val model = scene.model.open()
            return OpenScene(model, scene.controllers)
        }
    }
}