package baaahs.app.ui.preview

import baaahs.client.ClientStageManager
import baaahs.fixtures.FixtureManagerImpl
import baaahs.gl.GlBase
import baaahs.gl.render.RenderManager
import baaahs.model.Model
import baaahs.plugin.Plugins
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimulationEnv
import baaahs.throttle
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.coroutineExceptionHandler
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.get
import web.animations.requestAnimationFrame

class ClientPreview(
    model: Model,
    private val stageManager: ClientStageManager,
    clock: Clock,
    plugins: Plugins,
    private val coroutineScope: CoroutineScope = GlobalScope
) : ClientStageManager.Listener {
    private val glContext = GlBase.jsManager.createContext()
    private val renderManager = RenderManager(glContext)
    private val fixtureManager = FixtureManagerImpl(renderManager, plugins)
    private val dmxUniverse = FakeDmxUniverse()
    private val theVisualizer = Visualizer(clock)
    private var patchSetChanged = true
    private var keepRunning = true

    // TODO: This is super janky.
    private val targetFramerate
        get() = (kotlinx.browser.document["clientPreviewTargetFramerate"] as Number?)?.toFloat() ?: 15f

    val visualizer: Visualizer.Facade get() = theVisualizer.facade
    val renderPlanMonitor get() = fixtureManager.facade.renderPlanMonitor

    init {
        val pixelArranger = SwirlyPixelArranger(0.2f, 3f)

        val simulationEnv = SimulationEnv {
            component(clock)
            component(dmxUniverse)
            component<PixelArranger>(pixelArranger)
        }
        val adapter = EntityAdapter(simulationEnv, model.units)
        theVisualizer.clear()
        theVisualizer.units = model.units
        theVisualizer.initialViewingAngle = model.initialViewingAngle

        val allFixtures = model
            .allEntities.mapNotNull { entity ->
                entity.createFixtureSimulation(adapter)?.let { simulation ->
                    theVisualizer.add(simulation.itemVisualizer)
                    simulation.previewFixture
                }
            }
//    private val mappingResults = SessionMappingResults(model, emptyList()) // TODO: use real data.

        fixtureManager.fixturesChanged(allFixtures, emptyList())

        stageManager.addListener(this@ClientPreview)

        theVisualizer.addPrerenderListener {
            // Previously we triggered a model render with every frame.
            // Now this is decoupled so we can control the model render rate directly.
//            drawAndSendFrame()
        }

        animate()
    }

    private fun animate() {
        coroutineScope.launch(coroutineExceptionHandler, start = CoroutineStart.UNDISPATCHED) {
            throttle(targetFramerate, logger) {
                drawAndSendFrame()
            }

            if (keepRunning) {
                requestAnimationFrame { animate() }
            }
        }
    }

    private suspend fun drawAndSendFrame() {
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
        keepRunning = false
    }

    override fun onPatchSetChanged() {
        patchSetChanged = true
    }

    companion object {
        private val logger = Logger<ClientPreview>()
    }
}