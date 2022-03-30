package baaahs.scene

import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.model.ModelUnit
import baaahs.ui.IObservable
import baaahs.ui.Observable
import baaahs.ui.View
import baaahs.visualizer.ItemVisualizer

class EditingEntity<T : MutableEntity>(
    val mutableEntity: T,
    val modelUnit: ModelUnit,
    val itemVisualizer: ItemVisualizer<*>,
    private val onChange: () -> Unit
) : Observable(), EditSubject {
    override val title: String
        get() = mutableEntity.title

    val affineTransforms = Observable()

    var lastEntityData = mutableEntity.build()

    override fun onTransformationChange(
        position: Vector3F,
        rotation: EulerAngle,
        scale: Vector3F
    ) {
        if (
            position != mutableEntity.position
            || rotation != mutableEntity.rotation
            || scale != mutableEntity.scale
        ) {
            mutableEntity.position = position
            mutableEntity.rotation = rotation
            mutableEntity.scale = scale
            affineTransforms.notifyChanged()
        }
    }

    override fun onChange() {
        val newEntityData = mutableEntity.build()
        if (newEntityData != lastEntityData) {
            onChange.invoke()

            lastEntityData = newEntityData
        }
    }

    override fun getEditorPanelViews(): List<View> =
        mutableEntity.getEditorPanels().map { it.getView(this as EditingEntity<Nothing>) }
}

interface EditSubject : IObservable {
    val title: String

    fun onTransformationChange(
        position: Vector3F,
        rotation: EulerAngle,
        scale: Vector3F
    )

    fun onChange()

    fun getEditorPanelViews(): List<View>
}