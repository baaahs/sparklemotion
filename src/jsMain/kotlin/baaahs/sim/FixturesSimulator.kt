package baaahs.sim

import baaahs.io.Fs
import baaahs.mapper.MappingSession
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.scene.SceneProvider
import baaahs.sm.brain.sim.BrainSimulator
import baaahs.sm.brain.sim.BrainSimulatorManager
import baaahs.ui.addObserver
import baaahs.util.Clock
import baaahs.util.coroutineExceptionHandler
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.Visualizer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

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
    private val sceneProvider: SceneProvider,
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
    private lateinit var fixtureSimulations: Map<Model.Entity, FixtureSimulation>

    private val launchJob = coroutineScope.async(coroutineExceptionHandler) {
        var model = sceneProvider.openScene?.model
        if (model == null) {
            val deferrable = CompletableDeferred<Unit>()
            sceneProvider.addObserver {
                model = sceneProvider.openScene?.model
                deferrable.complete(Unit)
            }
            deferrable.await()
        }

        fixtureSimulations = buildMap {
            model!!.visit { entity ->
                entity.createFixtureSimulation(simulationEnv)?.let { put(entity, it) }
            }
        }

        model!!
    }

    suspend fun generateMappingData() {
        val model = launchJob.await()

        val mappingSession = MappingSession(
            clock.now(),
            fixtureSimulations.values.mapNotNull { it.mappingData },
            null,
            null,
            notes = "Simulated mapping session"
        )

        val mappingSessionPath = Storage(mapperFs, plugins).saveSession(mappingSession)
        val modelName = model.name
        val mappingDataPath = fs.resolve("mapping", modelName, "simulated", mappingSessionPath.name)
        mapperFs.renameFile(mappingSessionPath, mappingDataPath)
    }

    fun launchControllers() {
        fixtureSimulations.values.forEach { fixtureSimulation ->
            fixtureSimulation.launch()
        }
    }

    fun addToVisualizer() {
        fixtureSimulations.values.forEach {
            visualizer.add(it.entityVisualizer)
        }
    }

    inner class Facade : baaahs.ui.Facade() {
        val brains: List<BrainSimulator.Facade>
            get() = this@FixturesSimulator.brainSimulatorManager.brainSimulators.map { it.facade }
    }
}