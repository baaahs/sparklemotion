package baaahs.sim

import baaahs.Brain
import baaahs.doRunBlocking
import baaahs.fixtures.FixtureManager
import baaahs.getValue
import baaahs.io.Fs
import baaahs.mapper.MappingSession
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.util.Clock
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
    private val model: Model,
    private val network: Network,
    private val dmxUniverse: FakeDmxUniverse,
    private val fs: Fs,
    private val mapperFs: FakeFs,
    private val clock: Clock,
    private val plugins: Plugins,
    private val pixelArranger: PixelArranger
) {
    val facade = Facade()
    var fixtureManager: FixtureManager? = null

    private val brainsSimulator = BrainsSimulator(network, clock)
    private val wledsSimulator = WledsSimulator(network, clock)

    private val fixtureSimulations by lazy {
        val fixtureSimulationBuilder = FixtureSimulationBuilder { entity, simulationEnv ->
            when (entity) {
                is Model.Surface -> {
                    DirectSurfaceSimulation(entity, simulationEnv)
                }
                else -> error("huh?")
            }
        }

        val simulationEnv = SimulationEnv {
            component(brainsSimulator)
            component(clock)
            component(dmxUniverse)
            component(pixelArranger)
            component(wledsSimulator)
            component(visualizer)
            component(fixtureSimulationBuilder)
            component(fixtureManager!!)
        }

        model.allEntities.sortedBy(Model.Entity::name).map { entity ->
            entity.createFixtureSimulation(simulationEnv)
        }
    }

    fun generateMappingData() {
        val mappingSession = MappingSession(
            clock.now(),
            fixtureSimulations.mapNotNull { it.mappingData },
            null,
            null,
            notes = "Simulated mapping session"
        )

        doRunBlocking {
            val mappingSessionPath = Storage(mapperFs, plugins).saveSession(mappingSession)
            val mappingDataPath = fs.resolve("mapping", model.name, "simulated", mappingSessionPath.name)
            mapperFs.renameFile(mappingSessionPath, mappingDataPath)
        }
    }

    fun launchControllers() {
        fixtureSimulations.forEach { fixtureSimulation ->
            fixtureSimulation.launch()
        }
    }

    fun addToVisualizer() {
        fixtureSimulations.forEach {
            visualizer.addEntityVisualizer(it.entityVisualizer)
        }
    }

    inner class Facade : baaahs.ui.Facade() {
        val brains: List<Brain.Facade>
            get() = this@FixturesSimulator.brainsSimulator.brains.map { it.facade }
    }
}