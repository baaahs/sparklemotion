package baaahs.sim

import baaahs.ModelProvider
import baaahs.io.Fs
import baaahs.mapper.MappingSession
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.sm.brain.sim.BrainSimulator
import baaahs.sm.brain.sim.BrainSimulatorManager
import baaahs.util.Clock
import baaahs.util.coroutineExceptionHandler
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.Visualizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
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
    private val modelProvider: ModelProvider,
    network: Network,
    private val dmxUniverse: FakeDmxUniverse,
    private val fs: Fs,
    private val mapperFs: FakeFs,
    private val clock: Clock,
    private val plugins: Plugins,
    private val pixelArranger: PixelArranger,
    coroutineScope: CoroutineScope = GlobalScope
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
    private lateinit var fixtureSimulations: List<FixtureSimulation>

    private val launchJob = coroutineScope.launch(coroutineExceptionHandler) {
            val model = modelProvider.getModel()

            fixtureSimulations = model.allEntities
                .sortedBy(Model.Entity::name)
                .mapNotNull { entity -> entity.createFixtureSimulation(simulationEnv) }
        }

    suspend fun generateMappingData() {
        launchJob.join()

        val mappingSession = MappingSession(
            clock.now(),
            fixtureSimulations.mapNotNull { it.mappingData },
            null,
            null,
            notes = "Simulated mapping session"
        )

        val mappingSessionPath = Storage(mapperFs, plugins).saveSession(mappingSession)
        val modelName = modelProvider.getModel().name
        val mappingDataPath = fs.resolve("mapping", modelName, "simulated", mappingSessionPath.name)
        mapperFs.renameFile(mappingSessionPath, mappingDataPath)
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
        val brains: List<BrainSimulator.Facade>
            get() = this@FixturesSimulator.brainSimulatorManager.brainSimulators.map { it.facade }
    }
}