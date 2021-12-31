package baaahs.visualizer

import baaahs.geom.Matrix4F
import baaahs.model.LightRing
import baaahs.model.Model
import baaahs.model.ObjGroup
import baaahs.model.PixelArray
import baaahs.sim.SimulationEnv

expect interface EntityVisualizer {
    val entity: Model.Entity
    val title: String
    var mapperIsRunning: Boolean
    var selected: Boolean
    var transformation: Matrix4F

    fun addTo(parent: VizObj)
}

interface VisualizerBuilder {
    fun createLightBarVisualizer(pixelArray: PixelArray, simulationEnv: SimulationEnv): EntityVisualizer
    fun createLightRingVisualizer(lightRing: LightRing, simulationEnv: SimulationEnv): EntityVisualizer
    fun createObjGroupVisualizer(objGroup: ObjGroup, simulationEnv: SimulationEnv): EntityVisualizer
    fun createSurfaceVisualizer(surface: Model.Surface, simulationEnv: SimulationEnv): EntityVisualizer
}

expect val visualizerBuilder: VisualizerBuilder