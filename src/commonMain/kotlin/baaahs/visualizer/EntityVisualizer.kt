package baaahs.visualizer

import baaahs.model.*
import baaahs.scene.EditingEntity
import baaahs.sim.SimulationEnv
import baaahs.ui.IObservable
import baaahs.ui.View

interface EntityVisualizer<T : Model.Entity> : IObservable {
    val entity: T
    val title: String
    var isEditing: Boolean
    var mapperIsRunning: Boolean
    var selected: Boolean
    val obj: VizObj

    fun notifyChanged()

    /** Returns `true` if the three model has been updated to reflect `newEntity`. */
    fun updateIfApplicable(newEntity: Model.Entity, callback: ((EntityVisualizer<*>) -> Unit)?): Boolean

    fun findById(id: Int): EntityVisualizer<*>? =
        if (entity.id == id) this else null

    fun traverse(callback: (EntityVisualizer<*>) -> Unit) {
        callback.invoke(this)
    }

    fun applyStyles()
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
    fun getGridEditorView(editingEntity: EditingEntity<out Grid>): View
    fun getLightBarEditorView(editingEntity: EditingEntity<out LightBar>): View
    fun getMovingHeadEditorView(editingEntity: EditingEntity<out MovingHead>): View
    fun getObjModelEditorView(editingEntity: EditingEntity<out ObjGroup>): View
}

expect val visualizerBuilder: VisualizerBuilder