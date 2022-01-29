package baaahs.scene

import baaahs.controller.ControllerId
import baaahs.device.PixelArrayDevice
import baaahs.mapper.FixtureMapping
import baaahs.model.Model
import baaahs.sm.webapi.Problem

class OpenScene(
    val model: Model,
    val controllers: Map<String, ControllerConfig> = emptyMap(),
    val fixtures: Map<ControllerId, List<FixtureMapping>> = emptyMap()
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

            val fixtures = buildMap<ControllerId, MutableList<FixtureMapping>> {
                scene.fixtures.forEach { data ->
                    val pixelArrayDeviceConfig = data.deviceConfig as? PixelArrayDevice.Config
                    val fixtureMapping = FixtureMapping(
                        data.entityId?.let { model.findEntity(it) },
                        pixelArrayDeviceConfig?.pixelCount, // TODO kill this?
                        null, // TODO kill this?
                        data.deviceConfig,
                        data.transportConfig
                    )

                    val controllerId = ControllerId.fromName(data.controllerId)
                    getOrPut(controllerId) { arrayListOf() }
                        .add(fixtureMapping)
                }
            }

            return OpenScene(model, controllers, fixtures)
        }
    }
}