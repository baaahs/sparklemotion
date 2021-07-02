package baaahs.app.ui.preview

import baaahs.client.ClientStageManager
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureManager
import baaahs.fixtures.Transport
import baaahs.geom.Vector3F
import baaahs.gl.GlBase
import baaahs.gl.render.RenderManager
import baaahs.mapper.SessionMappingResults
import baaahs.model.Model
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimulationEnv
import baaahs.util.Clock
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import three.js.Vector3

class ClientPreview(
    model: Model,
    private val stageManager: ClientStageManager,
    clock: Clock
) : ClientStageManager.Listener {
    private val glContext = GlBase.jsManager.createContext()
    private val renderManager = RenderManager(model) { glContext }
    private val mappingResults = SessionMappingResults(model, emptyList()) // TODO: use real data.
    private val fixtureManager = FixtureManager(renderManager, model, mappingResults)
    private val theVisualizer = Visualizer(model, clock)
    private var patchSetChanged = true

    val visualizer: Visualizer.Facade get() = theVisualizer.facade

    init {
        val pixelArranger = SwirlyPixelArranger(0.2f, 3f)
        val dmxUniverse = FakeDmxUniverse()

        val simulationEnv = SimulationEnv {
            component(clock)
            component(dmxUniverse)
            component<PixelArranger>(pixelArranger)
            component(visualizer)
        }

        val allFixtures = model.allEntities.map { entity ->
            val simulation = entity.createFixtureSimulation(simulationEnv)
            theVisualizer.addEntityVisualizer(simulation.entityVisualizer)
            simulation.previewFixture
        }

        fixtureManager.fixturesChanged(allFixtures, emptyList())

        stageManager.addListener(this)

        theVisualizer.addPrerenderListener {
            checkForPatchSetChange()
            renderManager.draw()
            fixtureManager.sendFrame()
            dmxUniverse.sendFrame()
        }
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

    private fun createFixture(entity: Model.Entity, pixelPositions: Array<Vector3>, transport: Transport) =
        Fixture(
            entity,
            maxOf(1, pixelPositions.size), // One pixel, even if no pixels, e.g. for moving heads.
            pixelPositions.map { Vector3F(it.x.toFloat(), it.y.toFloat(), it.z.toFloat()) },
            entity.deviceType,
            entity.name,
            transport
        )
}