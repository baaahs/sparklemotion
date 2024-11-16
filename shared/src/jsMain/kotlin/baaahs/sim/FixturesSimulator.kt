package baaahs.sim

import baaahs.controller.ControllersManager
import baaahs.mapper.MappingSession
import baaahs.mapper.SessionMappingResults
import baaahs.model.Model
import baaahs.net.Network
import baaahs.randomDelay
import baaahs.scene.OpenScene
import baaahs.scene.SceneProvider
import baaahs.sm.brain.sim.BrainSimulatorManager
import baaahs.util.Clock
import baaahs.util.globalLaunch
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.Visualizer
import baaahs.visualizer.sim.PixelArranger
import kotlinx.coroutines.launch

/**
 * Steps to setting up a simulator:
 *
 * 1. Create a fixture simulation for each model entity.
 * 1. Generate fixture mapping data linking controllers to model entities.
 * 1. Generate pixel mapping data.
 * 1. Register mapping data.
 * 1. Launch controller simulations for each fixture.
 * 1. Generate fixture visualizers and add them to the simulation visualizer.
 */
class FixturesSimulator(
    private val visualizer: Visualizer,
    sceneProvider: SceneProvider,
    network: Network,
    private val dmxUniverse: FakeDmxUniverse,
    private val clock: Clock,
    private val pixelArranger: PixelArranger,
    private val simMappingManager: SimMappingManager,
    private val controllersManager: ControllersManager,
    private val brainSimulatorManager: BrainSimulatorManager,
    private val wledsSimulator: WledsSimulator
) {
    val facade = Facade()


    private val simulationEnv = SimulationEnv {
        component(brainSimulatorManager)
        component(clock)
        component(dmxUniverse)
        component(pixelArranger)
        component(wledsSimulator)
    }
    private var fixtureSimulations: Map<Model.Entity, FixtureSimulation> = emptyMap()

    private var isStarted = false
    private var currentScene: OpenScene? = null

    init {
        sceneProvider.addBeforeChangeListener { newOpenScene ->
            currentScene = newOpenScene
            if (isStarted)
                handleSceneChange(newOpenScene)
        }
    }

    fun start() {
        if (isStarted)
            error("FixturesSimulator is already started.")

        isStarted = true
        handleSceneChange(currentScene)
        facade.notifyChanged()
    }

    private fun handleSceneChange(newOpenScene: OpenScene?) {
        fixtureSimulations.values.forEach { oldFixtureSimulator ->
            oldFixtureSimulator.stop()
        }
        visualizer.facade.clear()
        controllersManager.reset()

        fixtureSimulations =
            if (newOpenScene == null) {
                emptyMap()
            } else {
                // TODO: create the appropriate controller simulators for fixtures that are mapped.
                val mappedEntities = buildSet {
                    newOpenScene.controllers.forEach { (controllerId, openControllerConfig) ->
                        openControllerConfig.controllerConfig.fixtures.forEach { fixtureMappingData ->
                            add(fixtureMappingData.entityId)
                        }
                    }
                }

                val entityAdapter = EntityAdapter(simulationEnv, newOpenScene.model.units)
                visualizer.facade.units = entityAdapter.units
                visualizer.initialViewingAngle = newOpenScene.model.initialViewingAngle
                buildMap {
                    newOpenScene.model.visit { entity ->
                        entity.createFixtureSimulation(entityAdapter)
                            ?.let { put(entity, it) }
                    }
                }
            }

        simMappingManager.mappingData =
            newOpenScene?.let {
                val now = clock.now()
                SessionMappingResults(
                    newOpenScene, listOf(
                        MappingSession(
                            now,
                            fixtureSimulations.values.mapNotNull { it.mappingData },
                            null,
                            null,
                            notes = "Simulated mapping session",
                            savedAt = now
                        )
                    )
                )
            }

        globalLaunch {
            fixtureSimulations.values.map {
                launch {
                    randomDelay(2000)
                    it.start()
                    visualizer.add(it.itemVisualizer)
                }
            }.forEach { it.join() }
            visualizer.fitCameraToObject()
            visualizer.facade.notifyChanged()
        }
    }

    inner class Facade : baaahs.ui.Facade() {
        val isStarted: Boolean
            get() = this@FixturesSimulator.isStarted

        fun start() =
            this@FixturesSimulator.start()
    }
}