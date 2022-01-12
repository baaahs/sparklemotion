package baaahs.visualizer

import baaahs.model.*
import baaahs.scene.EditingEntity
import baaahs.sim.SimulationEnv
import baaahs.ui.IObservable
import baaahs.ui.View

expect interface EntityVisualizer<T : Model.Entity> : IObservable {
    val entity: T
    val title: String
    var mapperIsRunning: Boolean
    var selected: Boolean

//    fun addTo(parent: VizObj)
}

interface VisualizerBuilder {
    fun createLightBarVisualizer(lightBar: LightBar, simulationEnv: SimulationEnv): EntityVisualizer<LightBar>
    fun createLightRingVisualizer(lightRing: LightRing, simulationEnv: SimulationEnv): EntityVisualizer<LightRing>
    fun createMovingHeadVisualizer(movingHead: MovingHead, simulationEnv: SimulationEnv): EntityVisualizer<MovingHead>
    fun createObjGroupVisualizer(objGroup: ObjGroup, simulationEnv: SimulationEnv): EntityVisualizer<ObjGroup>
    fun createPolyLineVisualizer(polyLine: PolyLine, simulationEnv: SimulationEnv): EntityVisualizer<PolyLine>
    fun createSurfaceVisualizer(surface: Model.Surface, simulationEnv: SimulationEnv): EntityVisualizer<Model.Surface>

    fun getTitleAndDescEditorView(editingEntity: EditingEntity<out Model.Entity>): View
    fun getTransformEditorView(editingEntity: EditingEntity<out Model.Entity>): View
    fun getObjModelEditorView(editingEntity: EditingEntity<out ObjGroup>): View
}

expect val visualizerBuilder: VisualizerBuilder