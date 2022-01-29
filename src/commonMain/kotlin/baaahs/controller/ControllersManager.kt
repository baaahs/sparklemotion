package baaahs.controller

import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureListener
import baaahs.geom.Vector3F
import baaahs.getBang
import baaahs.mapping.MappingManager
import baaahs.model.Model
import baaahs.scene.SceneProvider
import baaahs.sm.server.FrameListener
import baaahs.ui.addObserver
import baaahs.util.Logger

class ControllersManager(
    private val controllerManagers: List<ControllerManager>,
    private val mappingManager: MappingManager,
    private val sceneProvider: SceneProvider,
    private val fixtureListeners: List<FixtureListener>
) : FrameListener {
    private val byType = controllerManagers.associateBy { it.controllerType }
    private var deferFixtureRefresh = false
    private val controllers = mutableMapOf<ControllerId, LiveController>()
    private var model: Model? = sceneProvider.openScene?.model

    init {
        controllerManagers.forEach {
            it.addListener(object : ControllerListener {
                override fun onAdd(controller: Controller) {
                    logger.debug { "onAdd($controller)" }
                    if (controllers.containsKey(controller.controllerId))
                        error("Already know about ${controller.controllerId}")

                    val liveController = LiveController(controller)
                    controllers[controller.controllerId] = liveController

                    if (!deferFixtureRefresh)
                        refreshControllerFixtures(listOf(liveController))
                }

                override fun onRemove(controller: Controller) {
                    logger.debug { "onRemove($controller)" }
                    val liveController = controllers.remove(controller.controllerId)
                        ?: error("Don't know about ${controller.controllerId}")

                    fixturesChanged(removed = liveController.fixtures)
                }

                override fun onError(controller: Controller) {
                    onRemove(controller)
                }
            })
        }
    }

    fun start() {
        mappingManager.addObserver {
            refreshControllerFixtures()
        }

        sceneProvider.addObserver {
            onSceneChange()
            refreshControllerFixtures()
        }

        controllerManagers.forEach { it.start() }
        onSceneChange()
        refreshControllerFixtures()
    }

    private fun refreshControllerFixtures(
        controllers: Collection<LiveController> = this.controllers.values
    ) {
        val addFixtures = arrayListOf<Fixture>()
        val removeFixtures = arrayListOf<Fixture>()
        val model = this.model
        controllers.forEach { liveController ->
            val controller = liveController.controller
            removeFixtures.addAll(liveController.fixtures)
            liveController.fixtures.clear()

            if (model != null && mappingManager.dataHasLoaded) {
                val newFixtures = resolveFixtures(controller, model)
                liveController.fixtures.addAll(newFixtures)
                addFixtures.addAll(newFixtures)
            }
        }

        fixturesChanged(addFixtures, removeFixtures)
    }

    private fun resolveFixtures(
        controller: Controller,
        model: Model
    ): ArrayList<Fixture> {
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
                "${modelEntity?.name ?: "???"}@${controller.controllerId.name()}",
                controller.createTransport(modelEntity, fixtureConfig, transportConfig, pixelCount)
            )

            newFixtures.add(fixture)
        }
        return newFixtures
    }

    override fun beforeFrame() {
        controllers.values.forEach { it.controller.beforeFrame() }
    }

    override fun afterFrame() {
        controllers.values.forEach { it.controller.afterFrame() }
    }

    private fun onSceneChange() {
        val scene = sceneProvider.openScene
            ?: return

        this.model = scene.model

        val managerConfig = scene.controllers.entries
            .groupByTo(hashMapOf()) { (_, v) -> v.controllerType }
            .mapKeys { (k, _) -> byType.getBang(k, "controller manager") }
            .mapValues { (_, v) -> v.associate { (k, v) -> k to v } }

        try {
            deferFixtureRefresh = true
            controllerManagers.forEach { controllerManager ->
                controllerManager.onConfigChange(managerConfig[controllerManager] ?: emptyMap())
            }
        } finally {
            deferFixtureRefresh = false
        }
    }

    fun logStatus() {
        val controllerCounts = controllers.keys
            .groupBy { it.controllerType }
            .mapValues { (_, v) -> v.size }
            .entries.sortedBy { (k, _) -> k }

        val total = controllerCounts.sumOf { (_, count) -> count }
        logger.info {
            "$total controllers online (${
                controllerCounts.joinToString(", ") { (type, count) -> "$type=$count" }
            })."
        }
    }

    private fun fixturesChanged(added: List<Fixture> = emptyList(), removed: List<Fixture> = emptyList()) {
        if (added.isNotEmpty() || removed.isNotEmpty())
            fixtureListeners.forEach { it.fixturesChanged(added, removed) }
    }

    private class LiveController(
        val controller: Controller,
        val fixtures: MutableList<Fixture> = arrayListOf()
    )

    companion object {
        private val logger = Logger<ControllersManager>()
    }
}