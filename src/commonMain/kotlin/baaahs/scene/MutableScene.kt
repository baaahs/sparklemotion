package baaahs.scene

import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.MutableEditable
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.model.*
import baaahs.show.mutable.MutableDocument
import baaahs.sm.webapi.Problem
import baaahs.ui.Observable
import baaahs.visualizer.ItemVisualizer

class MutableScene(
    baseScene: Scene
) : MutableDocument<Scene> {
    override var title = baseScene.title
    val model = MutableModel(baseScene.model)
    val controllers = baseScene.controllers
        .mapValues { (_, v) -> MutableControllerConfig(v) }.toMutableMap()
    val fixtures = baseScene.fixtures
        .map { data -> MutableFixture(data) }.toMutableList()

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> {
        TODO("not implemented")
    }

    override fun isChanged(originalDocument: Scene): Boolean {
        TODO("not implemented")
    }

    override fun build(): Scene {
        return Scene(
            model.build(),
            controllers.mapValues { (_, v) -> v.controllerConfig },
            fixtures.map { it.fixture }
        )
    }
}

class MutableControllerConfig(var controllerConfig: ControllerConfig)

class MutableFixture(var fixture: FixtureMappingData)

class MutableModel(baseModel: ModelData) {
    var title = baseModel.title
    val entities = baseModel.entities.map { it.edit() }.toMutableList()
    var units = baseModel.units

    fun build(): ModelData {
        return ModelData(title, entities.map { it.build() }, units)
    }

    fun findById(id: EntityId): MutableEntity<*>? =
        entities.firstNotNullOfOrNull { it.findById(id) }

}

abstract class MutableEntity<T : Model.Entity>(
    override var title: String,
    var description: String?,
    var position: Vector3F,
    var rotation: EulerAngle,
    var scale: Vector3F,
    val id: EntityId
) : MutableEditable<T> {
    constructor(baseEntity: EntityData) : this(
        baseEntity.title, baseEntity.description,
        baseEntity.position, baseEntity.rotation, baseEntity.scale,
        baseEntity.id
    )

    abstract fun build(): EntityData

    open fun findById(id: EntityId): MutableEntity<*>? =
        if (this.id == id) this else null

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> =
        emptyList()
    abstract fun getEditorPanels(): List<EntityEditorPanel<in T>>
}

class EditingEntity<T : Model.Entity>(
    val mutableEntity: MutableEntity<T>,
    val modelUnit: ModelUnit,
    val itemVisualizer: ItemVisualizer<*>,
    private val onChange: () -> Unit
) : Observable() {
    val affineTransforms = Observable()

    var lastEntityData = mutableEntity.build()

    fun onTransformationChange(
        position: Vector3F,
        rotation: EulerAngle,
        scale: Vector3F
    ) {
        if (
            position != mutableEntity.position
            || rotation != mutableEntity.rotation
            || scale != mutableEntity.scale)
        {
            println("onTransformationChange ${mutableEntity.title} position = $position")

            mutableEntity.position = position
            mutableEntity.rotation = rotation
            mutableEntity.scale = scale
            affineTransforms.notifyChanged()
        }
    }

    fun onChange() {
        val newEntityData = mutableEntity.build()
        if (newEntityData != lastEntityData) {
            onChange.invoke()

            lastEntityData = newEntityData
        } else {
            println("Ignoring onChange() of unchanged ${mutableEntity.title}")
        }
    }
}

class MutableObjModel(
    baseObjModel: ObjModelData
) : MutableEntity<ObjGroup>(baseObjModel), MutableGroupEntity {
    var objData: String = baseObjModel.objData
        set(value) { field = value; loader = null }

    var objDataIsFileRef: Boolean = baseObjModel.objDataIsFileRef
        set(value) { field = value; loader = null }

    var metadata: EntityMetadataProvider? = baseObjModel.metadata
        set(value) { field = value; loader = null }

    private var loader: ObjModelLoader? = null
    private var importFail: Exception? = null
    val problems get() = (getLoader()?.errors ?: emptyList()).map { Problem("", it.message) } +
            listOfNotNull(importFail).map { Problem("", it.message) }

    private fun getLoader(): ObjModelLoader? =
        loader ?: try {
            importFail = null
            ObjModelLoader.doImport(objData, objDataIsFileRef, title) {
                metadata?.getMetadataFor(this.build())?.expectedPixelCount
            }.also {
                loader = it
            }
        } catch (e: Exception) {
            importFail = e
            null
        }

    override val children: MutableList<MutableEntity<*>> get() =
        (getLoader()?.allEntities ?: emptyList()).map {
            object : MutableEntity<Model.Surface>(it.title, null, Vector3F.origin, EulerAngle.identity, Vector3F.origin, Model.Entity.nextId()) {
                override fun build(): EntityData {
                    return SurfaceDataForTest(it.title)
                }

                override fun getEditorPanels(): List<EntityEditorPanel<in Model.Surface>> {
                    return emptyList()
                }
            }
        }.toMutableList()

    override fun build(): ObjModelData =
        ObjModelData(title, description, position, rotation, scale, id, objData, objDataIsFileRef, metadata)

    override fun getEditorPanels(): List<EntityEditorPanel<in ObjGroup>> =
        listOf(
            TitleAndDescEntityEditorPanel,
            TransformEntityEditorPanel,
            ObjModelEntityEditorPanel
        )

    fun reloadFile() {
        loader = null
        importFail = null
        getLoader()
    }
}

interface MutableGroupEntity {
    val children: MutableList<MutableEntity<*>>
}

class MutableMovingHeadData(
    baseMovingHeadData: MovingHeadData
) : MutableEntity<MovingHead>(baseMovingHeadData) {
    var adapter: MovingHeadAdapter = baseMovingHeadData.adapter

    override fun build(): EntityData =
        MovingHeadData(title, description, position, rotation, scale, id, adapter)

    override fun getEditorPanels(): List<EntityEditorPanel<in MovingHead>> =
        listOf(
            TitleAndDescEntityEditorPanel,
            TransformEntityEditorPanel,
            MovingHeadEditorPanel
        )
}

class MutableLightBarData(
    baseLightBar: LightBarData
) : MutableEntity<LightBar>(baseLightBar) {
    var startVertex = baseLightBar.startVertex
    var endVertex = baseLightBar.endVertex

    override fun build(): EntityData =
        LightBarData(title, description, position, rotation, scale, id, startVertex, endVertex)

    override fun getEditorPanels(): List<EntityEditorPanel<in LightBar>> =
        listOf(
            TitleAndDescEntityEditorPanel,
            TransformEntityEditorPanel,
            LightBarEditorPanel
        )
}

class MutablePolyLineData(
    basePolyLine: PolyLineData
) : MutableEntity<PolyLine>(basePolyLine) {
    var segments = basePolyLine.segments

    override fun build(): EntityData =
        PolyLineData(title, description, position, rotation, scale, id, segments)

    override fun getEditorPanels() =
        listOf(
            TitleAndDescEntityEditorPanel,
            TransformEntityEditorPanel
//            custom EditorPanel(this)
        )
}

class MutableGridData(
    baseGridData: GridData
) : MutableEntity<Grid>(baseGridData) {
    var rows = baseGridData.rows
    var columns = baseGridData.columns
    var rowGap = baseGridData.rowGap
    var columnGap = baseGridData.columnGap
    var direction = baseGridData.direction
    var zigZag = baseGridData.zigZag

    override fun build(): EntityData =
        GridData(title, description, position, rotation, scale, id, rows, columns, rowGap, columnGap, direction, zigZag)

    override fun getEditorPanels(): List<EntityEditorPanel<in Grid>> =
        listOf(
            TitleAndDescEntityEditorPanel,
            TransformEntityEditorPanel,
            GridEditorPanel
        )
}

class MutableLightRingData(
    baseLightRing: LightRingData
) : MutableEntity<LightRing>(baseLightRing) {
    var center = baseLightRing.center
    var radius = baseLightRing.radius
    var planeNormal = baseLightRing.planeNormal
    var firstPixelRadians = baseLightRing.firstPixelRadians
    var pixelDirection = baseLightRing.pixelDirection

    override fun build(): EntityData =
        LightRingData(
            title, description, position, rotation, scale, id,
            center, radius, planeNormal, firstPixelRadians, pixelDirection
        )

    override fun getEditorPanels() =
        listOf(
            TitleAndDescEntityEditorPanel,
            TransformEntityEditorPanel
//            custom EditorPanel(this)
        )
}