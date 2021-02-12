package baaahs.app.ui.preview

import baaahs.client.ClientStageManager
import baaahs.fixtures.*
import baaahs.geom.Vector3F
import baaahs.gl.GlBase
import baaahs.gl.render.RenderManager
import baaahs.model.Model
import baaahs.util.Clock
import baaahs.visualizer.SurfaceGeometry
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import baaahs.visualizer.VizPixels

class ClientPreview(
    model: Model,
    private val stageManager: ClientStageManager,
    clock: Clock
) : ClientStageManager.Listener {
    private val glContext = GlBase.jsManager.createContext()
    private val renderManager = RenderManager(model) { glContext }
    private val fixtureManager = FixtureManager(renderManager)
    private val realVisualizer = Visualizer(model, clock)
    private var patchSetChanged = true

    val visualizer: Visualizer.Facade get() = realVisualizer.facade

    init {
        val pixelArranger = SwirlyPixelArranger(0.2f, 3f)
        val allFixtures = model.allSurfaces.map { surface ->
            val surfaceGeometry = SurfaceGeometry(surface)
            val pixelPositions = pixelArranger.arrangePixels(surfaceGeometry)
            val vizSurface = realVisualizer.addSurface(surfaceGeometry)
            val vizPixels = VizPixels(vizSurface, pixelPositions)
            vizSurface.vizPixels = vizPixels

            Fixture(
                surface,
                pixelPositions.size,
                pixelPositions.map { Vector3F(it.x.toFloat(), it.y.toFloat(), it.z.toFloat()) },
                surface.deviceType,
                surface.name,
                object : Transport {
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
            )
        }

        fixtureManager.fixturesChanged(allFixtures, emptyList())

        stageManager.addListener(this)

        realVisualizer.addPrerenderListener {
            checkForPatchSetChange()
            renderManager.draw()
            fixtureManager.sendFrame()
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
}