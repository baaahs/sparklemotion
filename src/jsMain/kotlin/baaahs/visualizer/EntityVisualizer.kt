package baaahs.visualizer

import baaahs.geom.Matrix4F
import baaahs.model.LightRing
import baaahs.model.Model
import baaahs.model.ObjGroup
import baaahs.model.PixelArray
import baaahs.sim.SimulationEnv
import three.js.Object3D

actual interface EntityVisualizer {
    actual val entity: Model.Entity
    actual val title: String
    actual var mapperIsRunning: Boolean
    actual var selected: Boolean
    actual var transformation: Matrix4F
    val vizObj: Object3D? get() = null

    actual fun addTo(parent: VizObj)
}

interface EntityGroupVisualizer {
    val children: Collection<EntityVisualizer>
}

actual val visualizerBuilder: VisualizerBuilder = object : VisualizerBuilder {
    override fun createLightBarVisualizer(pixelArray: PixelArray, simulationEnv: SimulationEnv): EntityVisualizer =
        LightBarVisualizer(pixelArray, simulationEnv)

    override fun createLightRingVisualizer(lightRing: LightRing, simulationEnv: SimulationEnv): EntityVisualizer =
        LightRingVisualizer(lightRing, simulationEnv)

    override fun createObjGroupVisualizer(objGroup: ObjGroup, simulationEnv: SimulationEnv): EntityVisualizer =
        ObjGroupVisualizer(objGroup, simulationEnv)

    override fun createSurfaceVisualizer(surface: Model.Surface, simulationEnv: SimulationEnv): EntityVisualizer =
        SurfaceVisualizer(surface, SurfaceGeometry(surface), simulationEnv)
}