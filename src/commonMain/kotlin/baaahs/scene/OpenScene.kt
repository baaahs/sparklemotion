package baaahs.scene

import baaahs.controller.ControllerId
import baaahs.model.Model
import baaahs.sm.webapi.Problem
import baaahs.util.Logger

class OpenScene(
    val model: Model,
    val controllers: Map<ControllerId, ControllerConfig> = emptyMap()
) {
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

    companion object {
        private val logger = Logger<OpenScene>()

        fun open(scene: Scene): OpenScene {
            val model = scene.model.open()
            return OpenScene(model, scene.controllers)
        }
    }
}