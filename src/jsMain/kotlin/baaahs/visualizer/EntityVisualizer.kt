package baaahs.visualizer

import baaahs.app.ui.model.*
import baaahs.geom.toThreeEuler
import baaahs.model.Model
import baaahs.scene.*
import baaahs.ui.Observable
import baaahs.ui.View
import baaahs.ui.renderWrapper

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
    override fun getTitleAndDescEditorView(editingEntity: EditingEntity<out MutableEntity>): View = renderWrapper {
        titleAndDescriptionEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getTransformEditorView(editingEntity: EditingEntity<out MutableEntity>): View = renderWrapper {
        transformationEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getGridEditorView(editingEntity: EditingEntity<out MutableGridData>): View = renderWrapper {
        gridEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getLightBarEditorView(editingEntity: EditingEntity<out MutableLightBarData>): View = renderWrapper {
        lightBarEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getLightRingEditorView(editingEntity: EditingEntity<out MutableLightRingData>): View = renderWrapper {
        lightRingEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getMovingHeadEditorView(editingEntity: EditingEntity<out MutableMovingHeadData>): View = renderWrapper {
        movingHeadEditor {
            attrs.editingEntity = editingEntity
        }
    }

    override fun getImportedEntityEditorView(editingEntity: EditingEntity<out MutableImportedEntityGroup>): View = renderWrapper {
        objGroupEditor {
            attrs.editingEntity = editingEntity
        }
    }
}