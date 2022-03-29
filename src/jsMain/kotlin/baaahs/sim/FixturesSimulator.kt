package baaahs.sim

import baaahs.controller.ControllerId
import baaahs.controller.SacnControllerConfig
import baaahs.controller.SacnManager
import baaahs.controller.sim.ControllerSimulator
import baaahs.mapper.MappingSession
import baaahs.mapper.SessionMappingResults
import baaahs.model.Model
import baaahs.net.Network
import baaahs.scene.FixtureMappingData
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
    private val simMappingManager: SimMappingManager
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
        component(visualizer)
    }
    private val entityAdapter = EntityAdapter(simulationEnv)
    private var simulation: Simulation? = null

    init {
        sceneProvider.addBeforeChangeListener { newOpenScene ->
            simulation?.stop()
            visualizer.facade.clear()

            simulation = if (newOpenScene != null) {
                val entityToControllerId = mutableMapOf<String, ControllerId>()

                // Create additional controller configs for any unmapped entities.

                newOpenScene.controllers.forEach { (controllerId, controllerConfig) ->
                    controllerConfig.fixtures.forEach { fixtureMappingData ->
                        if (fixtureMappingData.entityId != null) {
                            entityToControllerId[fixtureMappingData.entityId] = controllerId
                        }
                    }
                }

                val generatedControllerConfigs = buildMap {
                    newOpenScene.model.visit { entity ->
                        if (!entityToControllerId.contains(entity.name)) {
                            val controllerId = SacnManager.idFor("sim_${entity.name}")
                            put(
                                controllerId,
                                SacnControllerConfig(
                                    "Sim Controller for ${entity.name}", "fake-address", 5,
                                    listOf(FixtureMappingData(entity.name, entity.fixtureType.emptyConfig))
                                )
                            )
                            entityToControllerId[entity.name] = controllerId
                        }
                    }
                }

                val controllerSimulators =
                    (newOpenScene.controllers + generatedControllerConfigs)
                        .mapValues { (controllerId, controllerConfig) ->
                            controllerConfig.createSimulator(controllerId, simulationEnv)
                        }

                val mappingDatas = mutableListOf<MappingSession.SurfaceData>()
                val fixturePreviews = buildMap {
                    newOpenScene.model.visit { entity ->
                        val controllerId = entityToControllerId[entity.name]
                        val controllerSimulation = controllerSimulators[controllerId]!!

                        entity.createFixtureVisualizer(simulationEnv, entityAdapter, controllerSimulation)
                            ?.let {
                                visualizer.add(it.itemVisualizer)
                                it.mappingData?.let { mappingDatas.add(it) }

                                put(entity, it)
                            }
                    }
                }

                simMappingManager.mappingData =
                    SessionMappingResults(
                        newOpenScene, listOf(
                            MappingSession(
                                clock.now(),
                                mappingDatas,
                                null,
                                null,
                                notes = "Simulated mapping session"
                            )
                        )
                    )

                controllerSimulators.values.forEach { it.start() }

                Simulation(controllerSimulators, fixturePreviews)
            } else {
                simMappingManager.mappingData = null
                null
            }
        }
    }

    inner class Simulation(
        private val controllerSimulators: Map<ControllerId, ControllerSimulator>,
        private val fixturePreviews: Map<Model.Entity, FixtureSimulation>
    ) {
        fun stop() {
            controllerSimulators.values.forEach { it.stop() }
        }
    }

    inner class Facade : baaahs.ui.Facade() {
        val brains: List<BrainSimulator.Facade>
            get() = this@FixturesSimulator.brainSimulatorManager.brainSimulators.map { it.facade }
    }
}