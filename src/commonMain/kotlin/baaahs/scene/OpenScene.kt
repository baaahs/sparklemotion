package baaahs.scene

import baaahs.device.PixelArrayDevice
import baaahs.mapper.FixtureMapping
import baaahs.model.Model
import baaahs.sm.webapi.Problem

class OpenScene(
    val model: Model,
    val controllers: Map<String, ControllerConfig> = emptyMap(),
    val fixtures: List<FixtureMapping> = emptyList()
) {
    val allProblems: List<Problem>
        get() = buildList {
            model.visit { entity -> addAll(entity.problems) }
//            controllers.values.visit { controller -> addAll(controller.problems) }
//            fixtures.visit { fixture -> addAll(fixture.problems) }
        }

    companion object {
        fun open(scene: Scene): OpenScene {
            val model = scene.model.open()

            val controllers = scene.controllers

            val fixtures = scene.fixtures.map { data ->
                val pixelArrayDeviceConfig = data.deviceConfig as? PixelArrayDevice.Config
                FixtureMapping(
                    data.entityId?.let { model.findEntity(it) },
                    pixelArrayDeviceConfig?.pixelCount, // TODO kill this?
                    null, // TODO kill this?
                    data.deviceConfig,
                    data.transportConfig
                )
            }

            return OpenScene(model, controllers, fixtures)
        }
    }
}