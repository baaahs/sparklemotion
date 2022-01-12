package baaahs.scene

import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.MutableEditable
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.model.*
import baaahs.show.mutable.MutableDocument
import baaahs.sim.SimulationEnv
import baaahs.ui.Icon
import baaahs.ui.Observable
import baaahs.ui.View
import baaahs.ui.addObserver
import baaahs.visualizer.visualizerBuilder

class MutableScene(
    baseScene: Scene
) : MutableDocument<Scene> {
    override var title = baseScene.title
    val model = MutableModel(baseScene.model)
    val controllers = baseScene.controllers
        .mapValues { (_, v) -> MutableControllerConfig(v) }.toMutableMap()
    val fixtures = baseScene.fixtures
        .mapValues { (_, v) -> MutableFixture(v) }.toMutableMap()

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> {
        TODO("not implemented")
    }

    override fun isChanged(originalDocument: Scene): Boolean {
        TODO("not implemented")
    }

    override fun build(): Scene {
        return Scene(
            model.build(),
            controllers.mapValues { (k, v) -> v.controllerConfig },
            fixtures.mapValues { (k, v) -> v.fixture }
        )
    }
}

class MutableControllerConfig(var controllerConfig: ControllerConfig)

class MutableFixture(var fixture: FixtureConfigNew)

class MutableModel(baseModel: ModelData) {
    var title = baseModel.title
    val entities = baseModel.entities.map { it.edit() }.toMutableList()
    var units = baseModel.units

    fun build(): ModelData {
        return ModelData(title, entities.map { it.build() }, units)
    }

//    fun edit(entity: Model.Entity) {
//        entities.forEach { candidate ->
//            if (candidate == entity) {
//                MutableEntity(candidate)
//            } else if (candidate is Model.EntityGroup) {
//
//            }
//        }
//    }
}

abstract class MutableEntity<T : Model.Entity>(
    override var title: String,
    var description: String?,
    var position: Vector3F,
    var rotation: EulerAngle,
    var scale: Vector3F
) : MutableEditable<T> {
    constructor(baseEntity: EntityData) :
            this(baseEntity.title, baseEntity.description, baseEntity.position, baseEntity.rotation, baseEntity.scale)

    abstract fun build(): EntityData

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> =
        emptyList()

    abstract fun getEditorPanels(): List<EntityEditorPanel<in T>>
}

class EditingEntity<T : Model.Entity>(
    val mutableEntity: MutableEntity<T>,
    val modelUnit: ModelUnit,
    simulationEnv: SimulationEnv
) : Observable() {
    val entityData = mutableEntity.build()
    val openEntity = entityData.open() as T
    val entityVisualizer = openEntity.createVisualizer(simulationEnv)
        .also {
            it.addObserver { notifyChanged() }
        }

    fun onChange() {
        notifyChanged()
    }
}

interface EntityEditorPanel<T : Model.Entity> {
    val title: String? get() = null
    val icon: Icon? get() = null

    fun getView(editingEntity: EditingEntity<out T>): View
}

class TitleAndDescEntityEditorPanel : EntityEditorPanel<Model.Entity> {
    override fun getView(editingEntity: EditingEntity<out Model.Entity>): View =
        visualizerBuilder.getTitleAndDescEditorView(editingEntity)
}

class TransformEntityEditorPanel : EntityEditorPanel<Model.Entity> {
    override val title: String get() = "Transformation"

    override fun getView(editingEntity: EditingEntity<out Model.Entity>): View =
        visualizerBuilder.getTransformEditorView(editingEntity)
}

class MutableObjModel(
    baseObjModel: ObjModelData
) : MutableEntity<ObjGroup>(baseObjModel) {
    var objData: String = baseObjModel.objData
    var objDataIsFileRef: Boolean = baseObjModel.objDataIsFileRef
    var metadata: EntityMetadataProvider? = baseObjModel.metadata

    override fun build(): EntityData =
        ObjModelData(title, description, position, rotation, scale, objData, objDataIsFileRef, metadata)

    override fun getEditorPanels(): List<EntityEditorPanel<in ObjGroup>> =
        listOf(
            TitleAndDescEntityEditorPanel(),
            TransformEntityEditorPanel(),
            ObjModelEntityEditorPanel()
        )
}

class MutableMovingHeadData(
    baseGridData: MovingHeadData
) : MutableEntity<MovingHead>(baseGridData) {

    override fun build(): EntityData =
        MovingHeadData(title, description, position, rotation, scale)

    override fun getEditorPanels() =
        listOf(
            TitleAndDescEntityEditorPanel(),
            TransformEntityEditorPanel()
//            custom EditorPanel(this)
        )
}

class MutableLightBarData(
    baseLightBar: LightBarData
) : MutableEntity<LightBar>(baseLightBar) {
    var startVertex = baseLightBar.startVertex
    var endVertex = baseLightBar.endVertex

    override fun build(): EntityData =
        LightBarData(title, description, position, rotation, scale, startVertex, endVertex)

    override fun getEditorPanels() =
        listOf(
            TitleAndDescEntityEditorPanel(),
            TransformEntityEditorPanel()
//            custom EditorPanel(this)
        )
}

class MutablePolyLineData(
    basePolyLine: PolyLineData
) : MutableEntity<PolyLine>(basePolyLine) {
    var segments = basePolyLine.segments

    override fun build(): EntityData =
        PolyLineData(title, description, position, rotation, scale, segments)

    override fun getEditorPanels() =
        listOf(
            TitleAndDescEntityEditorPanel(),
            TransformEntityEditorPanel()
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
        GridData(title, description, position, rotation, scale, rows, columns, rowGap, columnGap, direction, zigZag)

    override fun getEditorPanels() =
        listOf(
            TitleAndDescEntityEditorPanel(),
            TransformEntityEditorPanel()
//            custom EditorPanel(this)
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
            title, description, position, rotation, scale,
            center, radius, planeNormal, firstPixelRadians, pixelDirection
        )

    override fun getEditorPanels() =
        listOf(
            TitleAndDescEntityEditorPanel(),
            TransformEntityEditorPanel()
//            custom EditorPanel(this)
        )
}

class ObjModelEntityEditorPanel : EntityEditorPanel<ObjGroup> {
    override val title: String
        get() = "OBJ Model"

    override fun getView(editingEntity: EditingEntity<out ObjGroup>): View =
        visualizerBuilder.getObjModelEditorView(editingEntity)
}

