package baaahs.visualizer

import baaahs.app.ui.model.*
import baaahs.geom.toThreeEuler
import baaahs.model.*
import baaahs.scene.EditingEntity
import baaahs.ui.Observable
import baaahs.ui.View
import baaahs.ui.renderWrapper
import baaahs.visualizer.movers.MovingHeadVisualizer

@Suppress("LeakingThis")
abstract class BaseEntityVisualizer<T : Model.Entity>(
    override var item: T
) : Observable(), ItemVisualizer<T> {
    override val title: String
        get() = item.title

    override var isEditing: Boolean = false
    override var mapperIsRunning: Boolean = false
    override var selected: Boolean = false

    protected abstract fun applyStyle(entityStyle: EntityStyle)

    override fun applyStyles() {
        EntityStyle.applyStyles(this) { applyStyle(it) }
    }

    override fun update(newItem: T) {
        item = newItem

        obj.name = newItem.title
        obj.position.copy(newItem.position.toVector3())
        obj.rotation.copy(newItem.rotation.toThreeEuler())
        obj.scale.copy(newItem.scale.toVector3())
    }
}

actual val visualizerBuilder: VisualizerBuilder = object : VisualizerBuilder {
    override fun createLightBarVisualizer(lightBar: LightBar, adapter: EntityAdapter): ItemVisualizer<LightBar> =
        LightBarVisualizer(lightBar, adapter)

    override fun createLightRingVisualizer(lightRing: LightRing, adapter: EntityAdapter): ItemVisualizer<LightRing> =
        LightRingVisualizer(lightRing)

    override fun createMovingHeadVisualizer(movingHead: MovingHead, adapter: EntityAdapter): ItemVisualizer<MovingHead> =
        MovingHeadVisualizer(movingHead, adapter)

    override fun createObjGroupVisualizer(objGroup: ObjGroup, adapter: EntityAdapter): ItemVisualizer<ObjGroup> =
        ObjGroupVisualizer(objGroup, adapter)

    override fun createPolyLineVisualizer(polyLine: PolyLine, adapter: EntityAdapter): ItemVisualizer<PolyLine> =
        PolyLineVisualizer(polyLine)

    override fun createSurfaceVisualizer(surface: Model.Surface, adapter: EntityAdapter): ItemVisualizer<Model.Surface> =
        SurfaceVisualizer(surface, SurfaceGeometry(surface))

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

    override fun getGridEditorView(editingEntity: EditingEntity<out Grid>): View = renderWrapper {
        gridEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getLightBarEditorView(editingEntity: EditingEntity<out LightBar>): View = renderWrapper {
        lightBarEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getMovingHeadEditorView(editingEntity: EditingEntity<out MovingHead>): View = renderWrapper {
        movingHeadEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getObjModelEditorView(editingEntity: EditingEntity<out ObjGroup>): View = renderWrapper {
        objGroupEditor {
            attrs.editingEntity = editingEntity
        }
    }
}