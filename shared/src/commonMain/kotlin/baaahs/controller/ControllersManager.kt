package baaahs.controller

import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureListener
import baaahs.getBang
import baaahs.mapping.MappingManager
import baaahs.scene.OpenScene
import baaahs.scene.SceneProvider
import baaahs.sm.server.FrameListener
import baaahs.ui.addObserver
import baaahs.util.Logger

class ControllersManager(
    private val controllerManagers: List<ControllerManager>,
    private val mappingManager: MappingManager,
    private val sceneProvider: SceneProvider,
    private val fixtureListeners: List<FixtureListener>,
    private val controllerListeners: List<ControllerListener> = emptyList()
) : FrameListener {
    private val byType = controllerManagers.associateBy { it.controllerType }
    private var deferFixtureRefresh = false
    private val controllers = mutableMapOf<ControllerId, LiveController>()
    private var controllersChanged = true
    private var scene: OpenScene? = sceneProvider.openScene

    init {
        controllerManagers.forEach { controllerManager ->
            controllerManager.addListener(object : ControllerListener {
                override fun onAdd(controller: Controller) {
                    logger.debug { "onAdd(${controller.controllerId})" }
                    if (controllers.containsKey(controller.controllerId))
                        error("Already know about ${controller.controllerId}")

                    val liveController = LiveController(controller)
                    controllers[controller.controllerId] = liveController
                    controllersChanged = true

                    if (!deferFixtureRefresh)
                        refreshControllerFixtures(listOf(liveController))
                    controllerListeners.forEach { it.onAdd(controller) }
                }

                override fun onRemove(controller: Controller) {
                    logger.debug { "onRemove(${controller.controllerId})" }
                    val liveController = controllers.remove(controller.controllerId)
                        .also { if (it != null) controllersChanged = true }
                        ?: error("Don't know about ${controller.controllerId}")

                    fixturesChanged(removed = liveController.fixtures)
                    controllerListeners.forEach { it.onRemove(controller) }
                }

                override fun onError(controller: Controller) {
                    controllerListeners.forEach { it.onError(controller) }
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
        if (!controllersChanged) return

        val addFixtures = arrayListOf<Fixture>()
        val removeFixtures = arrayListOf<Fixture>()
        controllers.forEach { liveController ->
            val controller = liveController.controller
            removeFixtures.addAll(liveController.fixtures)
            liveController.fixtures.clear()

            val scene = this.scene
            if (scene != null && mappingManager.dataHasLoaded) {
                val newFixtures = scene.resolveFixtures(controller, mappingManager)
                liveController.fixtures.addAll(newFixtures)
                addFixtures.addAll(newFixtures)
            }
        }

        fixturesChanged(addFixtures, removeFixtures)
        controllersChanged = false
    }

    override fun beforeFrame() {
        controllers.values.forEach { it.controller.beforeFrame() }
    }

    override fun afterFrame() {
        controllers.values.forEach { it.controller.afterFrame() }
    }

    private fun onSceneChange() {
        this.scene = sceneProvider.openScene

        val managerConfig = scene?.let {
            it.controllers.entries
                .groupByTo(hashMapOf()) { (_, v) -> v.controllerType }
                .mapKeys { (k, _) -> byType.getBang(k, "controller manager") }
                .mapValues { (_, v) -> v.associate { (k, v) -> k to v } }
        } ?: emptyMap()

        try {
            deferFixtureRefresh = true
            controllerManagers.forEach { controllerManager ->
                controllerManager.onConfigChange(managerConfig[controllerManager] ?: emptyMap())
            }
        } finally {
            deferFixtureRefresh = false
        }
    }

    fun reset() {
        controllerManagers.forEach {
            it.reset()
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
    ) {
        override fun toString(): String = "LiveController(controller=$controller, fixtures=$fixtures)"
    }

    companion object {
        private val logger = Logger<ControllersManager>()
    }
}