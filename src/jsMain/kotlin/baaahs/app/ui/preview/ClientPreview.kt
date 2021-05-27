package baaahs.app.ui.preview

import baaahs.client.ClientStageManager
import baaahs.fixtures.*
import baaahs.geom.Vector3F
import baaahs.gl.GlBase
import baaahs.gl.render.RenderManager
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.sim.FakeDmxUniverse
import baaahs.util.Clock
import baaahs.visualizer.SurfaceGeometry
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import baaahs.visualizer.VizPixels
import three.js.Vector3

class ClientPreview(
    model: Model,
    private val stageManager: ClientStageManager,
    clock: Clock
) : ClientStageManager.Listener {
    private val glContext = GlBase.jsManager.createContext()
    private val renderManager = RenderManager(model) { glContext }
    private val fixtureManager = FixtureManager(renderManager)
    private val theVisualizer = Visualizer(model, clock)
    private var patchSetChanged = true

    val visualizer: Visualizer.Facade get() = theVisualizer.facade

    init {
        val pixelArranger = SwirlyPixelArranger(0.2f, 3f)
        val dmxUniverse = FakeDmxUniverse()

        val allFixtures = model.allEntities.map { entity ->
            when (entity) {
                is Model.Surface -> {
                    val surfaceGeometry = SurfaceGeometry(entity)
                    // TODO: it'd be nice if actual pixel locations were used. For now we make them up.
                    val pixelPositions = pixelArranger.arrangePixels(surfaceGeometry, entity.expectedPixelCount)
                    val vizSurface = theVisualizer.addSurface(surfaceGeometry)
                    val vizPixels = VizPixels(vizSurface, pixelPositions)
                    vizSurface.vizPixels = vizPixels

                    createFixture(entity, pixelPositions, PixelArrayPreviewTransport(entity, vizPixels))
                }
                is MovingHead -> {
                    theVisualizer.addMovingHead(entity, dmxUniverse)
                    val movingHeadBuffer = entity.newBuffer(dmxUniverse)
                    createFixture(entity, emptyArray(), object : Transport {
                        override val name: String
                            get() = entity.name

                        override fun send(fixture: Fixture, resultViews: List<ResultView>) {
                            val params = MovingHeadDevice.getResults(resultViews)[0]
                            movingHeadBuffer.pan = params.pan
                            movingHeadBuffer.tilt = params.tilt
                            movingHeadBuffer.colorWheelPosition = params.colorWheel
                            movingHeadBuffer.dimmer = params.dimmer
                        }
                    })
                }
                else -> error("Unknown model entity type $entity")
            }
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

    class PixelArrayPreviewTransport(
        private val surface: Model.Surface,
        private val vizPixels: VizPixels
    ) : Transport {
        override val name: String
            get() = surface.name

        override fun send(fixture: Fixture, resultViews: List<ResultView>) {
            val resultColors =
                PixelArrayDevice.getColorResults(resultViews)
            for (i in vizPixels.indices) {
                vizPixels[i] = resultColors[i]
            }
        }
    }
}