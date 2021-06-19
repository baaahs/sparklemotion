package baaahs.sim

import baaahs.*
import baaahs.geom.Matrix4
import baaahs.geom.Vector3F
import baaahs.io.Fs
import baaahs.mapper.MappingSession
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.model.MovingHead
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.util.Clock
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.SurfaceGeometry
import baaahs.visualizer.Visualizer
import baaahs.visualizer.VizPixels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import three.js.Vector3

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

    private val brainScope = CoroutineScope(Dispatchers.Main)
    private val brains: MutableList<Brain> = mutableListOf()
    private lateinit var simSurfaces: List<SimSurface>

    fun prepareSurfaces() {
        var totalPixels = 0

        simSurfaces = model.allSurfaces.sortedBy(Model.Surface::name).mapIndexed { index, surface ->
            //            if (panel.name != "17L") return@forEachIndexed

            val surfaceGeometry = SurfaceGeometry(surface)
            val pixelPositions = pixelArranger.arrangePixels(surfaceGeometry, surface.expectedPixelCount)
            totalPixels += pixelPositions.size
            SimSurface(surface, surfaceGeometry, pixelPositions, BrainId("brain//$index"))
        }

        doRunBlocking {
            val mappingSessionPath = Storage(mapperFs, plugins).saveSession(
                MappingSession(clock.now(), simSurfaces.map { simSurface ->
                    MappingSession.SurfaceData(
                        simSurface.brainId.uuid, simSurface.surface.name,
                        simSurface.pixelPositions.map {
                            MappingSession.SurfaceData.PixelData(
                                Vector3F(
                                    it.x.toFloat(),
                                    it.y.toFloat(),
                                    it.z.toFloat()
                                ), null, null
                            )
                        }, null, null, null
                    )
                }, Matrix4(doubleArrayOf()), null, notes = "Simulated pixels")
            )
            mapperFs.renameFile(
                mappingSessionPath,
                fs.resolve("mapping", model.name, "simulated", mappingSessionPath.name))
        }
    }

    fun launchControllers() {
        simSurfaces.forEach { simSurface ->
            val vizPanel = visualizer.addSurface(simSurface.surfaceGeometry)
            vizPanel.vizPixels = VizPixels(vizPanel, simSurface.pixelPositions)

            val brain = Brain(simSurface.brainId.uuid, network, vizPanel.vizPixels ?: NullPixels, clock)
            brains.add(brain)

            brainScope.launch { randomDelay(1000); brain.run() }
        }
    }

    fun addToVisualizer() {
        model.allEntities.forEach { entity ->
            when (entity) {
                is MovingHead -> visualizer.addMovingHead(entity, dmxUniverse)
            }
        }
    }

    inner class Facade : baaahs.ui.Facade() {
        val brains: List<Brain.Facade>
            get() = this@FixturesSimulator.brains.map { it.facade }
    }

    class SimSurface(
        val surface: Model.Surface,
        val surfaceGeometry: SurfaceGeometry,
        val pixelPositions: Array<Vector3>,
        val brainId: BrainId
    )

    object NullPixels : Pixels {
        override val size = 0

        override fun get(i: Int): Color = Color.BLACK
        override fun set(i: Int, color: Color) {}
        override fun set(colors: Array<Color>) {}
    }
}