package baaahs.visualizer

import baaahs.geom.toEulerAngle
import baaahs.model.EntityId
import baaahs.model.Model
import baaahs.model.ModelData
import baaahs.scene.EditingEntity
import baaahs.scene.MutableModel
import baaahs.util.Clock
import three.js.Group
import three.js.Object3D
import three_ext.OrbitControls
import three_ext.TransformControls
import three_ext.clear
import three_ext.toVector3F
import web.dom.observers.IntersectionObserver

class ModelVisualEditor(
    var mutableModel: MutableModel,
    clock: Clock,
    adapter: EntityAdapter,
    private val onChange: () -> Unit
) : BaseVisualizer(clock) {
    override val facade = Facade()

    init {
        realScene.autoUpdate = true
    }

    private var modelData: ModelData = mutableModel.build()
    var model: Model = modelData.open()
        private set

    var selectedEntity: Model.Entity?
        get() = selectedObject?.modelEntity
        set(value) { selectedObject = value?.let { findVisualizer(it)?.obj } }

    var editingEntity: EditingEntity<*>? = null
        private set

    /** [TransformControls] must be created by [OrbitControls]. */
    override val extensions get() = listOf(
        extension { TransformControlsExtension() }
    ) + super.extensions

    inner class TransformControlsExtension : Extension(TransformControlsExtension::class) {
        val transformControls by lazy {
            TransformControls(camera, canvas).also {
                it.space = "world"
                it.enabled = false
                realScene.add(it)
            }
        }

        override fun attach() {
            transformControls
        }
    }

    private val transformControls = findExtension(TransformControlsExtension::class).transformControls

    init {
        clear()
        units = model.units
        initialViewingAngle = model.initialViewingAngle
    }

    private val groupVisualizer =
        GroupVisualizer("Model: ${model.name}", model.entities, adapter).also {
            scene.add(it.groupObj)
            sceneNeedsUpdate = true
        }

    private val intersectionObserver = IntersectionObserver(callback = { entries, _ ->
        val isVisible = entries.any { it.isIntersecting }
        if (isVisible) startRendering() else stopRendering()
    }).apply { observe(canvas) }

    init {
        addPrerenderListener {
            groupVisualizer.traverse { it.applyStyles() }
        }

        val orbitControls = findExtension(OrbitControlsExtension::class).orbitControls
        transformControls.addEventListener("dragging-changed") {
            val isDragging = transformControls.dragging

            orbitControls.enabled = !isDragging

            println("${editingEntity?.mutableEntity?.title}: dragging-changed, dragging=${transformControls.dragging}; obj=${transformControls.`object`?.modelEntity?.title}")
            if (!isDragging) {
                pushTransformationChange()
                println("Push to undo stack!!!!!!!!!!!!!!!!!!!!!!")
                editingEntity?.onChange()
            }
        }
        transformControls.addEventListener("objectChange") {
            println("${editingEntity?.mutableEntity?.title}: pushTransformationChange(${
                if (!transformControls.dragging) "withUndo" else ""
            }) because 'objectChange'; dragging = ${transformControls.dragging}")
            pushTransformationChange()

//            if (!transformControls.dragging) {
//                println("Push to undo stack!!!!!!!!!!!!!!!!!!!!!!")
//                editingEntity?.onChange()
//            }
        }
    }

    private fun pushTransformationChange() {
        selectedObject?.let { selectedObj ->
            editingEntity?.onTransformationChange(
                selectedObj.position.toVector3F(),
                selectedObj.rotation.toEulerAngle(),
                selectedObj.scale.toVector3F()
            )
        }
        selectedObject?.dispatchEvent(EventType.Transform)
    }

    fun findById(id: EntityId): Object3D? {
        var entity: Object3D? = null
        scene.traverse { obj ->
            if (obj.modelEntity?.id == id) {
                entity = obj
            }
        }
        return entity
    }

    private fun findVisualizer(entity: Model.Entity) =
        groupVisualizer.find { (it as? Model.Entity)?.id == entity.id }

    override fun onObjectClick(obj: Object3D?) {
        super.onObjectClick(findParentEntity(obj))
    }

    override fun onSelectionChange(obj: Object3D?, priorObj: Object3D?) {
//        println("onSelectionChange from ${priorObj?.modelEntity?.title} to ${obj?.modelEntity?.title}")
        priorObj?.itemVisualizer?.selected = false
        transformControls.detach()

        val itemVisualizer = obj?.itemVisualizer
        val modelEntity = obj?.modelEntity
        val mutableEntity = modelEntity?.let { mutableModel.findById(it.id) }

        itemVisualizer?.selected = true
        selectedEntity = modelEntity

        if (obj == null) {
            transformControls.detach()
            transformControls.enabled = false
        } else {
            transformControls.attach(obj)
            transformControls.enabled = true
        }

        editingEntity = obj?.let {
            EditingEntity(
                mutableEntity ?: error("No mutable entity for selection?"),
                mutableModel.units,
                itemVisualizer ?: error("No visualizer for selection?"),
                onChange
            )
        }

        super.onSelectionChange(obj, priorObj)
    }

    override fun inUserInteraction(): Boolean =
        super.inUserInteraction() || transformControls.dragging

    private fun findParentEntity(obj: Object3D?): Object3D? {
        var curObj = obj
        while (curObj != null && curObj.modelEntity == null) {
            curObj = curObj.parent
        }
        return curObj
    }

    fun refresh() {
        val newData = mutableModel.build()
        if (modelData != newData) {
            modelData = newData
            model = newData.open()
            clear()
            units = model.units
            initialViewingAngle = model.initialViewingAngle

            scene.add(groupVisualizer.groupObj)
            groupVisualizer.updateChildren(model.entities)
        }
    }

    override fun release() {
        transformControls.dispose()
        intersectionObserver.disconnect()
        super.release()
    }

    inner class Facade : BaseVisualizer.Facade() {
        var moveSnap: Double?
            get() = transformControls.translationSnap
            set(value) {
                transformControls.translationSnap = value
            }

        var rotateSnap: Double?
            get() = transformControls.rotationSnap
            set(value) {
                transformControls.rotationSnap = value
            }

        var scaleSnap: Double?
            get() = transformControls.scaleSnap
            set(value) {
                transformControls.scaleSnap = value
            }

        var transformMode: TransformMode
            get() = TransformMode.find(transformControls.mode)
            set(value) {
                transformControls.mode = value.modeName
            }

        var transformInLocalSpace: Boolean
            get() = transformControls.space == "local"
            set(value) {
                transformControls.space = if (value) "local" else "world"
            }
    }
}

class GroupVisualizer(
    title: String,
    entities: List<Model.Entity>,
    val adapter: Adapter<Model.Entity>
) {
    val groupObj: Group = Group().apply { name = title }

    private val itemVisualizers: MutableList<ItemVisualizer<*>> =
        entities.map { entity ->
            adapter.createVisualizer(entity).also {
                it.obj.itemVisualizer = it
                it.obj.modelEntity = entity
                groupObj.add(it.obj)
            }
        }.toMutableList()

    fun find(predicate: (Any) -> Boolean): ItemVisualizer<*>? =
        itemVisualizers.firstNotNullOfOrNull { it.find(predicate) }

    fun updateChildren(entities: List<Model.Entity>) {
        val oldChildren = ArrayList(itemVisualizers)
        itemVisualizers.clear()
        groupObj.clear()

        entities.forEachIndexed { index, newChild ->
            val oldVisualizer = oldChildren.getOrNull(index)
            val visualizer =
                if (oldVisualizer != null && oldVisualizer.updateIfApplicable(newChild)) {
                    oldVisualizer
                } else {
                    adapter.createVisualizer(newChild).also {
                        it.obj.itemVisualizer = it
                        it.obj.modelEntity = newChild
                    }
                }

            itemVisualizers.add(visualizer)
            groupObj.add(visualizer.obj)
        }
    }

    fun traverse(callback: (ItemVisualizer<*>) -> Unit) {
        itemVisualizers.forEach { it.traverse(callback) }
    }
}

var Object3D.modelEntity: Model.Entity?
    get() = userData["modelEntity"] as Model.Entity?
    set(value) { userData["modelEntity"] = value }