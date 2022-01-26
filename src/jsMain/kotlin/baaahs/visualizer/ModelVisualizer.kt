package baaahs.visualizer

import baaahs.model.EntityId
import baaahs.model.Model
import baaahs.sim.SimulationEnv
import baaahs.util.Clock
import external.IntersectionObserver
import three.js.Group
import three.js.Object3D
import three_ext.OrbitControls
import three_ext.TransformControls
import three_ext.clear

class ModelVisualizer(
    model: Model,
    clock: Clock,
    simulationEnv: SimulationEnv,
    private val isEditing: Boolean
) : BaseVisualizer(clock) {
    override val facade = Facade()

    var model: Model = model
        set(value) {
            field = value
            update(value)
        }

    var selectedEntity: Model.Entity? = null
        set(value) {
            field?.let { groupVisualizer.findById(it.id)?.selected = false }
            value?.let { groupVisualizer.findById(it.id)?.selected = true }
            transformControls.enabled = value != null
            field = value
        }

    /** [TransformControls] must be created by [OrbitControls]. */
    override val extensions get() = listOf(
        extension { TransformControlsExtension() }
    ) + super.extensions

    inner class TransformControlsExtension : Extension(TransformControlsExtension::class) {
        val transformControls by lazy {
            TransformControls(camera, canvas).also {
                it.space = "local"
                it.enabled = false
                realScene.add(it)
            }
        }

        override fun attach() {
            transformControls
        }
    }

    private val transformControls = findExtension(TransformControlsExtension::class).transformControls

    private val groupVisualizer = GroupVisualizer("Model: ${model.name}", model.entities, simulationEnv)
        .also { scene.add(it.groupObj) }

    private val intersectionObserver = IntersectionObserver(callback = { entries ->
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

            if (!isDragging) {
                selectedObject?.dispatchEvent(EventType.Transform)
            }
        }
        transformControls.addEventListener("change") {
            val entityVisualizer = transformControls.`object`?.entityVisualizer
            entityVisualizer?.notifyChanged()
            println("object = ${transformControls.`object`}")
        }

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

    override fun onObjectClick(obj: Object3D?) {
        super.onObjectClick(findParentEntity(obj))
    }

    override fun onSelectionChange(obj: Object3D?) {
        if (selectedEntity != null) {
            transformControls.detach()
        }

        val newSelectedEntity = obj?.modelEntity
        newSelectedEntity?.let { transformControls.attach(obj) }
        selectedEntity = newSelectedEntity

        super.onSelectionChange(obj)
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

    override fun release() {
        transformControls.dispose()
        intersectionObserver.disconnect()
        super.release()
    }

    private fun update(newModel: Model) {
        val entities = newModel.entities
        groupVisualizer.updateChildren(entities) {
            it.isEditing = isEditing
        }
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
    val simulationEnv: SimulationEnv
) {
    val groupObj: Group = Group().apply { name = title }

    private val entityVisualizers: MutableList<EntityVisualizer<*>> =
        entities.map { entity ->
            entity.createVisualizer(simulationEnv).also { visualizer ->
                val obj = visualizer.obj
                obj.modelEntity = entity
                groupObj.add(obj)
            }
        }.toMutableList()

    fun findById(id: EntityId): EntityVisualizer<*>? =
        entityVisualizers.firstNotNullOfOrNull { it.findById(id) }

    fun updateChildren(
        entities: List<Model.Entity>,
        callback: ((EntityVisualizer<*>) -> Unit)? = null
    ) {
        val oldChildren = entityVisualizers.associateBy { it.entity.id }
        entityVisualizers.clear()
        groupObj.clear()

        entities.forEach { newChild ->
            val oldVisualizer = oldChildren[newChild.id]
            val visualizer =
                if (oldVisualizer != null && oldVisualizer.updateIfApplicable(newChild, callback)) {
                    oldVisualizer
                } else {
                    newChild.createVisualizer(simulationEnv)
                }

            entityVisualizers.add(visualizer)
            callback?.invoke(visualizer)
            val obj = visualizer.obj
            obj.modelEntity = newChild
            groupObj.add(obj)
        }
    }

    fun traverse(callback: (EntityVisualizer<*>) -> Unit) {
        entityVisualizers.forEach { it.traverse(callback) }
    }
}

var Object3D.modelEntity: Model.Entity?
    get() = userData["modelEntity"] as Model.Entity?
    set(value) { userData["modelEntity"] = value }