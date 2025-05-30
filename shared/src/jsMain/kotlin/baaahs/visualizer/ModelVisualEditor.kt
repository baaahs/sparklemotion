package baaahs.visualizer

import baaahs.geom.toEulerAngle
import baaahs.model.EntityLocator
import baaahs.model.Model
import baaahs.scene.EditingEntity
import baaahs.scene.MutableEntity
import baaahs.scene.MutableScene
import baaahs.scene.Scene
import baaahs.util.Clock
import baaahs.util.three.addEventListener
import baaahs.visualizer.entity.ItemVisualizer
import baaahs.visualizer.entity.itemVisualizer
import three.Group
import three.Object3D
import three.examples.jsm.controls.TransformControls
import three_ext.toVector3F
import web.intersection.IntersectionObserver
import kotlin.reflect.KClass

class ModelVisualEditor(
    mutableScene: MutableScene,
    clock: Clock,
    adapter: EntityAdapter,
    elements: List<Pair<KClass<out Extension>, () -> Extension>> = emptyList(),
    private val onChange: () -> Unit
) : BaseVisualizer(
    clock,
    /** [TransformControls] must be created before [three.examples.jsm.controls.OrbitControls]. */
    elements + listOf(
        extension { TransformControlsExtension() }
    )
) {
    override val facade = Facade()

    var mutableScene: MutableScene = mutableScene

    private var sceneData: Scene = mutableScene.build()
    var model: Model = sceneData.open().model
        private set

    var selectedEntity: Model.Entity?
        get() = selectedObject?.modelEntity
        set(value) { selectedObject = value?.let { findVisualizer(it)?.obj } }

    var editingEntity: EditingEntity<*>? = null
        private set

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

    fun findByLocator(locator: EntityLocator): Object3D? {
        var entity: Object3D? = null
        scene.traverse { obj ->
            if (obj.modelEntity?.locator == locator) {
                entity = obj
            }
        }
        return entity
    }

    private fun findVisualizer(entity: Model.Entity) =
        groupVisualizer.find { (it as? Model.Entity)?.locator == entity.locator }

    override fun onObjectClick(obj: Object3D?) {
        super.onObjectClick(obj.findParentEntity())
    }

    override fun onSelectionChange(obj: Object3D?, priorObj: Object3D?) {
//        println("onSelectionChange from ${priorObj?.modelEntity?.title} to ${obj?.modelEntity?.title}")
        priorObj?.itemVisualizer?.selected = false
        transformControls.detach()

        val itemVisualizer = obj?.itemVisualizer
        val modelEntity = obj?.modelEntity
        val mutableEntity = modelEntity?.let { mutableScene.model.findByLocator(it.locator) }

        itemVisualizer?.selected = true
        selectedEntity = modelEntity

        allExtensions { this.onSelectionChange(itemVisualizer, priorObj?.itemVisualizer) }

        editingEntity = obj?.let {
            buildEditingEntity(mutableEntity, itemVisualizer)
        }

        super.onSelectionChange(obj, priorObj)
    }

    fun refresh() {
        println("refreshing!")
        val newSceneData = mutableScene.build()
        if (sceneData != newSceneData) {
            sceneData = newSceneData
            model = newSceneData.open().model
            clear()
            units = model.units
            initialViewingAngle = model.initialViewingAngle

            scene.add(groupVisualizer.groupObj)
            groupVisualizer.updateChildren(model.entities)
        }
        selectedEntity = selectedEntity?.let { previousSelectedEntity ->
            model.findEntityByLocator(previousSelectedEntity.locator)
//                .also {
//                    console.log("Previous selectedEntity:", previousSelectedEntity)
//                    console.log("New selectedEntity:", it)
//                }
        }
        // TODO: We're probably forcing too many rerenders with this, fix.
        editingEntity = selectedEntity?.let { previousEditingEntity ->
            buildEditingEntityForRefresh(previousEditingEntity)
                .also {
                    console.log("Previous editingEntity:", previousEditingEntity)
                    console.log("New editingEntity:", it)
                }
        }
    }

    private fun buildEditingEntityForRefresh(entity: Model.Entity): EditingEntity<MutableEntity> =
        EditingEntity(
            mutableScene.model.findByLocator(entity.locator)
                ?: error("No mutable entity for selection?"),
            mutableScene.model.units,
            findVisualizer(entity)
                ?: error("No visualizer for selection?"),
            onChange
        )

    private fun buildEditingEntity(mutableEntity: MutableEntity?, itemVisualizer: ItemVisualizer<*>?): EditingEntity<MutableEntity> =
        EditingEntity(
            mutableEntity
                ?: error("No mutable entity for selection?"),
            mutableScene.model.units,
            itemVisualizer
                ?: error("No visualizer for selection?"),
            onChange
        )

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
    private val title: String,
    entities: List<Model.Entity>,
    val adapter: Adapter<Model.Entity>
) {
    val groupObj: Group = Group().apply { name = title }

    private val itemVisualizers: MutableList<ItemVisualizer<Model.Entity>> =
        adapter.withinGroup(title) {
            entities.map { entity ->
                adapter.createVisualizer(entity).also {
                    it.obj.itemVisualizer = it
                    it.obj.modelEntity = entity
                    groupObj.add(it.obj)
                }
            }.toMutableList()
        }

    fun find(predicate: (Any) -> Boolean): ItemVisualizer<*>? =
        itemVisualizers.firstNotNullOfOrNull { it.find(predicate) }

    fun updateChildren(entities: List<Model.Entity>) {
        val oldChildren = ArrayList(itemVisualizers)
        itemVisualizers.clear()
        groupObj.clear()

        adapter.withinGroup(title) {
            entities.forEachIndexed { index, newChild ->
                val oldVisualizer = oldChildren.getOrNull(index)
                val visualizer = adapter.createOrUpdateVisualizer(oldVisualizer, newChild)
                    .also {
                        it.obj.itemVisualizer = it
                        it.obj.modelEntity = newChild
                        groupObj.add(it.obj)
                    }

                itemVisualizers.add(visualizer)
                groupObj.add(visualizer.obj)
            }
        }
    }

    fun traverse(callback: (ItemVisualizer<*>) -> Unit) {
        itemVisualizers.forEach { it.traverse(callback) }
    }
}

var Object3D.modelEntity: Model.Entity?
    get() = userData.asDynamic()["modelEntity"] as Model.Entity?
    set(value) { userData.asDynamic()["modelEntity"] = value }