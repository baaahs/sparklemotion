package baaahs.visualizer

import baaahs.app.ui.model.objGroupEditor
import baaahs.app.ui.model.titleAndDescriptionEditor
import baaahs.app.ui.model.transformationEditor
import baaahs.geom.toThreeEuler
import baaahs.model.*
import baaahs.scene.EditingEntity
import baaahs.sim.SimulationEnv
import baaahs.ui.IObservable
import baaahs.ui.Observable
import baaahs.ui.View
import baaahs.ui.renderWrapper
import baaahs.visualizer.movers.MovingHeadVisualizer
import three.js.Object3D

@Suppress("LeakingThis")
abstract class BaseEntityVisualizer<T : Model.Entity>(
    override var entity: T
) : Observable(), EntityVisualizer<T> {
    override val title: String
        get() = entity.title

    override var selected: Boolean = false
    override var mapperIsRunning: Boolean = false

    abstract fun isApplicable(newEntity: Model.Entity): T?

    open fun update(newEntity: T) {
        obj.name = newEntity.title
        obj.position.copy(newEntity.position.toVector3())
        obj.rotation.copy(newEntity.rotation.toThreeEuler())
        obj.scale.copy(newEntity.scale.toVector3())

        entity = newEntity
    }

    override fun updateIfApplicable(newEntity: Model.Entity): Boolean {
        if (newEntity == entity) return true

        val tEntity = isApplicable(newEntity)
        return if (tEntity != null) {
            update(tEntity)
            true
        } else false
    }
}

actual interface EntityVisualizer<T : Model.Entity> : IObservable {
    actual val entity: T
    actual val title: String
    actual var mapperIsRunning: Boolean
    actual var selected: Boolean
    val obj: Object3D

    fun notifyChanged()

    /** Returns `true` if the three model has been updated to reflect `newEntity`. */
    fun updateIfApplicable(newEntity: Model.Entity): Boolean
}

interface EntityGroupVisualizer {
    val children: Collection<EntityVisualizer<*>>
}

actual val visualizerBuilder: VisualizerBuilder = object : VisualizerBuilder {
    override fun createLightBarVisualizer(lightBar: LightBar, simulationEnv: SimulationEnv): EntityVisualizer<LightBar> =
        LightBarVisualizer(lightBar, simulationEnv)

    override fun createLightRingVisualizer(lightRing: LightRing, simulationEnv: SimulationEnv): EntityVisualizer<LightRing> =
        LightRingVisualizer(lightRing, simulationEnv)

    override fun createMovingHeadVisualizer(movingHead: MovingHead, simulationEnv: SimulationEnv): EntityVisualizer<MovingHead> =
        MovingHeadVisualizer(movingHead, simulationEnv)

    override fun createObjGroupVisualizer(objGroup: ObjGroup, simulationEnv: SimulationEnv): EntityVisualizer<ObjGroup> =
        ObjGroupVisualizer(objGroup, simulationEnv)

    override fun createPolyLineVisualizer(polyLine: PolyLine, simulationEnv: SimulationEnv): EntityVisualizer<PolyLine> =
        PolyLineVisualizer(polyLine, simulationEnv)

    override fun createSurfaceVisualizer(surface: Model.Surface, simulationEnv: SimulationEnv): EntityVisualizer<Model.Surface> =
        SurfaceVisualizer(surface, SurfaceGeometry(surface), simulationEnv)

    override fun getTitleAndDescEditorView(editingEntity: EditingEntity<out Model.Entity>): View = renderWrapper {
        titleAndDescriptionEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getTransformEditorView(editingEntity: EditingEntity<out Model.Entity>): View = renderWrapper {
        transformationEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getObjModelEditorView(editingEntity: EditingEntity<out ObjGroup>): View = renderWrapper {
        objGroupEditor {
            attrs.editingEntity = editingEntity as EditingEntity<ObjGroup>
        }
    }
}