package baaahs.app.ui.preview

import baaahs.client.ClientStageManager
import baaahs.document
import baaahs.fixtures.FixtureManager
import baaahs.gl.render.RenderManager
import baaahs.mapper.SessionMappingResults
import baaahs.model.Model
import baaahs.sim.DirectSurfaceSimulation
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FixtureSimulationBuilder
import baaahs.sim.SimulationEnv
import baaahs.throttle
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import baaahs.window
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.get

class ClientPreview(
    model: Model,
    private val stageManager: ClientStageManager,
    clock: Clock
) : ClientStageManager.Listener {
    private val mappingResults = SessionMappingResults(model, emptyList()) // TODO: use real data.
    private val dmxUniverse = FakeDmxUniverse()
    private val theVisualizer = Visualizer(model, clock)
    private val glContext = theVisualizer.getGlContext()
    private val renderManager = RenderManager(model, direct = true) { glContext }
    private val fixtureManager = FixtureManager(renderManager, model, mappingResults)
    private var patchSetChanged = true

    // TODO: This is super janky.
    private val targetFramerate
        get() = (document["clientPreviewTargetFramerate"] as Number?)?.toFloat() ?: 15f

    val visualizer: Visualizer.Facade get() = theVisualizer.facade

    init {
        val pixelArranger = SwirlyPixelArranger(0.2f, 3f)
        val fixtureSimulationBuilder = FixtureSimulationBuilder { entity, simulationEnv ->
            when (entity) {
                is Model.Surface -> {
                    DirectSurfaceSimulation(entity, simulationEnv)
                }
                else -> error("huh?")
            }
        }

        val simulationEnv = SimulationEnv {
            component(clock)
            component(dmxUniverse)
            component<PixelArranger>(pixelArranger)
            component(visualizer)
            component(fixtureSimulationBuilder)
        }

        val allFixtures = model.allEntities.map { entity ->
            val simulation = entity.createFixtureSimulation(simulationEnv)
            theVisualizer.addEntityVisualizer(simulation.entityVisualizer)
            simulation.previewFixture
        }

        fixtureManager.fixturesChanged(allFixtures, emptyList())

        stageManager.addListener(this)

        theVisualizer.addPrerenderListener {
            // Previously we triggered a model render with every frame.
            // Now this is decoupled so we can control the model render rate directly.
//            drawAndSendFrame()
        }

        animate()
    }

    private fun animate() {
        GlobalScope.launch(start = CoroutineStart.UNDISPATCHED) {
            throttle(targetFramerate, logger) {
                drawAndSendFrame()
            }
            window.requestAnimationFrame { animate() }
        }
    }

    private fun drawAndSendFrame() {
        checkForPatchSetChange()
        renderManager.draw()
        fixtureManager.sendFrame()
        dmxUniverse.sendFrame()
    }

    private fun checkForPatchSetChange() {
        if (patchSetChanged) {
            patchSetChanged = false

            fixtureManager.activePatchSetChanged(stageManager.activePatchSet)
            fixtureManager.maybeUpdateRenderPlans()
        }
    }

    fun detach() {
        stageManager.removeListener(this)
    }

    override fun onPatchSetChanged() {
        patchSetChanged = true
    }

    companion object {
        private val logger = Logger<ClientPreview>()
    }
}