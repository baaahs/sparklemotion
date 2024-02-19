package baaahs.sim

import baaahs.controller.ControllersManager
import baaahs.mapper.MappingSession
import baaahs.mapper.SessionMappingResults
import baaahs.model.Model
import baaahs.net.Network
import baaahs.scene.SceneProvider
import baaahs.sm.brain.sim.BrainSimulator
import baaahs.sm.brain.sim.BrainSimulatorManager
import baaahs.util.Clock
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.Visualizer

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
    private val controllersManager: ControllersManager
) {
    val facade = Facade()

    private val brainSimulatorManager = BrainSimulatorManager(network, clock)
    private val wledsSimulator = WledsSimulator(network)

    private val simulationEnv = SimulationEnv {
        component(brainSimulatorManager)
        component(clock)
        component(dmxUniverse)
        component(pixelArranger)
        component(wledsSimulator)
    }
    private var fixtureSimulations: Map<Model.Entity, FixtureSimulation> = emptyMap()

    init {
        sceneProvider.addBeforeChangeListener { newOpenScene ->
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
                        newOpenScene.controllers.forEach { (controllerId, controllerConfig) ->
                            controllerConfig.fixtures.forEach { fixtureMappingData ->
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

            fixtureSimulations.values.forEach {
                it.start()
                visualizer.add(it.itemVisualizer)
            }
            visualizer.fitCameraToObject()
            visualizer.facade.notifyChanged()

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
        }
    }

    inner class Facade : baaahs.ui.Facade() {
        val brains: List<BrainSimulator.Facade>
            get() = this@FixturesSimulator.brainSimulatorManager.brainSimulators.map { it.facade }
    }
}