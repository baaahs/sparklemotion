package baaahs.controller

import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureListener
import baaahs.geom.Vector3F
import baaahs.getBang
import baaahs.mapper.ControllerId
import baaahs.mapping.MappingManager
import baaahs.model.Model
import baaahs.scene.SceneConfig
import baaahs.ui.addObserver

class ControllersManager(
    private val controllerManagers: List<ControllerManager>,
    private val mappingManager: MappingManager,
    private val model: Model,
    private val fixtureListener: FixtureListener
) {
    private val byType = controllerManagers.associateBy { it.controllerType }
    private var managersStarted = false
    private val controllers = mutableMapOf<ControllerId, LiveController>()

    private val controllerListener = object : ControllerListener {
        override fun onAdd(controller: Controller) {
            if (controllers.containsKey(controller.controllerId))
                error("Already know about ${controller.controllerId}")

            val mappings = mappingManager.findMappings(controller.controllerId)
                .ifEmpty { controller.getAnonymousFixtureMappings() }
            val newFixtures = arrayListOf<Fixture>()

            mappings.forEach { mapping ->
                val defaultMapping = controller.fixtureMapping

                val modelEntity = mapping.entity

                val fixtureConfig = mapping.fixtureConfig
                    ?: modelEntity?.deviceType?.defaultConfig
                    ?: defaultMapping?.fixtureConfig
                    ?: error("huh? no device config")

                // TODO: These really only apply to PixelArrayDevices.
                val pixelCount = mapping.pixelCount
                    ?: defaultMapping?.pixelCount
                    ?: 0

                val pixelLocations = mapping.pixelLocations
                    ?.map { it ?: Vector3F(0f, 0f, 0f) }
                    ?: fixtureConfig.generatePixelLocations(pixelCount, modelEntity, model)
                    ?: emptyList()

                val transportConfig = mapping.transportConfig
                    ?: defaultMapping?.transportConfig

                val fixture = Fixture(
                    modelEntity, pixelCount, pixelLocations,
                    fixtureConfig,
                    "${modelEntity?.name ?: "???"}@${controller.controllerId.shortName()}",
                    controller.createTransport(modelEntity, fixtureConfig, transportConfig, pixelCount)
                )

                newFixtures.add(fixture)
            }

            controllers[controller.controllerId] = LiveController(controller, newFixtures)
            if (newFixtures.isNotEmpty()) {
                fixtureListener.fixturesChanged(newFixtures, emptyList())
            }
        }

        override fun onRemove(controller: Controller) {
            val liveController = controllers.remove(controller.controllerId)
                ?: error("Don't know about ${controller.controllerId}")

            fixtureListener.fixturesChanged(emptyList(), liveController.fixtures)
        }

        override fun onError(controller: Controller) {
            onRemove(controller)
        }
    }

    fun start() {
        mappingManager.addObserver { onMappingChange() }

        maybeStartManagers()
    }

    fun beforeFrame() {
        controllers.values.forEach { it.controller.beforeFrame() }
    }

    fun afterFrame() {
        controllers.values.forEach { it.controller.afterFrame() }
    }

    fun onSceneChange(sceneConfig: SceneConfig?) {
        if (sceneConfig == null) return

        sceneConfig.controllers.entries
            .groupByTo(hashMapOf()) { (_, v) -> v.controllerType }
            .map { (controllerType, controllers) ->
                val controllerManager = byType.getBang(controllerType, "controller manager")
                controllerManager.onConfigChange(controllers.map { (_, v) -> v })
            }
    }

    fun logStatus() {
        controllerManagers.forEach { it.logStatus() }
    }

    private fun maybeStartManagers() {
        if (mappingManager.dataHasLoaded && !managersStarted) {
            controllerManagers.forEach { it.start(controllerListener) }
            managersStarted = true
        }
    }

    private fun onMappingChange() {
        maybeStartManagers()
    }

    private class LiveController(
        val controller: Controller,
        val fixtures: List<Fixture>
    )
}