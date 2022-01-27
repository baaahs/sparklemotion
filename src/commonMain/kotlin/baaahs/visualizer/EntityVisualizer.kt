package baaahs.visualizer

import baaahs.model.*
import baaahs.scene.EditingEntity
import baaahs.ui.IObservable
import baaahs.ui.View

interface EntityVisualizer<T : Model.Entity /*TODO undo*/> : IObservable {
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
    fun createLightBarVisualizer(lightBar: LightBar, adapter: EntityAdapter): EntityVisualizer<LightBar>
    fun createLightRingVisualizer(lightRing: LightRing, adapter: EntityAdapter): EntityVisualizer<LightRing>
    fun createMovingHeadVisualizer(movingHead: MovingHead, adapter: EntityAdapter): EntityVisualizer<MovingHead>
    fun createObjGroupVisualizer(objGroup: ObjGroup, adapter: EntityAdapter): EntityVisualizer<ObjGroup>
    fun createPolyLineVisualizer(polyLine: PolyLine, adapter: EntityAdapter): EntityVisualizer<PolyLine>
    fun createSurfaceVisualizer(surface: Model.Surface, adapter: EntityAdapter): EntityVisualizer<Model.Surface>

    fun getTitleAndDescEditorView(editingEntity: EditingEntity<out Model.Entity>): View
    fun getTransformEditorView(editingEntity: EditingEntity<out Model.Entity>): View
    fun getGridEditorView(editingEntity: EditingEntity<out Grid>): View
    fun getLightBarEditorView(editingEntity: EditingEntity<out LightBar>): View
    fun getMovingHeadEditorView(editingEntity: EditingEntity<out MovingHead>): View
    fun getObjModelEditorView(editingEntity: EditingEntity<out ObjGroup>): View
}

expect val visualizerBuilder: VisualizerBuilder