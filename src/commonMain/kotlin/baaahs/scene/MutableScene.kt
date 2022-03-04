package baaahs.scene

import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.MutableEditable
import baaahs.camelize
import baaahs.controller.*
import baaahs.device.FixtureType
import baaahs.dmx.DirectDmxController
import baaahs.dmx.DirectDmxControllerConfig
import baaahs.dmx.DmxManager
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.TransportConfig
import baaahs.fixtures.TransportType
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.model.*
import baaahs.model.importers.ObjImporter
import baaahs.show.mutable.MutableDocument
import baaahs.sm.brain.BrainControllerConfig
import baaahs.sm.brain.BrainManager
import baaahs.sm.webapi.Problem
import baaahs.ui.View

class MutableScene(
    baseScene: Scene
) : MutableDocument<Scene> {
    override var title
        get() = model.title
        set(value) { model.title = value }
    val model = MutableModel(baseScene.model)
    val controllers: MutableMap<ControllerId, MutableControllerConfig> =
        baseScene.controllers
            .mapValues { (_, v) -> v.edit() }.toMutableMap()

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> = listOf(
        ScenePropertiesEditorPanel(
            editableManager,
            SceneTitlePropsEditor(this),
            ModelUnitsPropsEditor(this)
        )
    )

    override fun build(): Scene {
        return Scene(
            model.build(),
            controllers.mapValues { (_, v) -> v.build() }
        )
    }
}

interface MutableControllerConfig {
    val controllerMeta: ControllerManager.MetaManager
    var title: String
    val fixtures: MutableList<MutableFixtureMapping>

    fun build(): ControllerConfig
    fun suggestId(): String
    fun matches(controllerMatcher: ControllerMatcher): Boolean
    fun getEditorPanels(editingController: EditingController<*>): List<ControllerEditorPanel<*>>
}

class MutableBrainControllerConfig(config: BrainControllerConfig) : MutableControllerConfig {
    override val controllerMeta: ControllerManager.MetaManager
        get() = BrainManager
    override var title: String = config.title
    override val fixtures: MutableList<MutableFixtureMapping> =
        config.fixtures.map { it.edit() }.toMutableList()

    override fun build(): ControllerConfig =
        BrainControllerConfig(title, fixtures.map { it.build() })

    override fun suggestId(): String = title.camelize()

    override fun matches(controllerMatcher: ControllerMatcher): Boolean =
        controllerMatcher.matches(title, BrainManager.controllerTypeName)

    override fun getEditorPanels(editingController: EditingController<*>): List<ControllerEditorPanel<*>> =
        listOf(BrainControllerEditorPanel)
}

class MutableDirectDmxControllerConfig(config: DirectDmxControllerConfig) : MutableControllerConfig {
    override val controllerMeta: ControllerManager.MetaManager
        get() = DmxManager
    override var title: String = config.title
    override val fixtures: MutableList<MutableFixtureMapping> =
        config.fixtures.map { it.edit() }.toMutableList()

    override fun build(): ControllerConfig =
        DirectDmxControllerConfig(title, fixtures.map { it.build() })

    override fun suggestId(): String = title.camelize()

    override fun matches(controllerMatcher: ControllerMatcher): Boolean =
        controllerMatcher.matches(title, DirectDmxController.controllerType)

    override fun getEditorPanels(editingController: EditingController<*>): List<ControllerEditorPanel<*>> =
        listOf(DirectDmxControllerEditorPanel)
}

class MutableSacnControllerConfig(config: SacnControllerConfig) : MutableControllerConfig {
    override val controllerMeta: ControllerManager.MetaManager
        get() = SacnManager
    override var title: String = config.title
    var address: String = config.address
    var universes: Int = config.universes
    override val fixtures: MutableList<MutableFixtureMapping> =
        config.fixtures.map { it.edit() }.toMutableList()

    override fun build(): ControllerConfig =
        SacnControllerConfig(title, address, universes, fixtures.map { it.build() })

    override fun suggestId(): String = title.camelize()

    override fun matches(controllerMatcher: ControllerMatcher): Boolean =
        controllerMatcher.matches(title, SacnManager.controllerTypeName)

    override fun getEditorPanels(editingController: EditingController<*>): List<ControllerEditorPanel<*>> =
        listOf(SacnControllerEditorPanel)
}

class MutableFixtureMapping(fixtureMappingData: FixtureMappingData) {
    var entityId: String? = fixtureMappingData.entityId
    var deviceConfig: MutableFixtureConfig? = fixtureMappingData.deviceConfig?.edit()
    var transportConfig: MutableTransportConfig? = fixtureMappingData.transportConfig?.edit()

    fun build(): FixtureMappingData =
        FixtureMappingData(entityId, deviceConfig?.build(), transportConfig?.build())
}

class MutableModel(baseModel: ModelData) {
    var title = baseModel.title
    val entities = baseModel.entities.map { it.edit() }.toMutableList()
    var units = baseModel.units

    fun build(): ModelData {
        return ModelData(title, entities.map { it.build() }, units)
    }

    fun findById(id: EntityId): MutableEntity? =
        entities.firstNotNullOfOrNull { it.findById(id) }

    /** @return `true` if `mutableEntity` was found and deleted. */
    fun delete(mutableEntity: MutableEntity): Boolean =
        if (entities.remove(mutableEntity)) true else {
            entities.any { it.delete(mutableEntity) }
        }
}

abstract class MutableEntity : MutableEditable<Model.Entity> {
    constructor(baseEntity: EntityData) {
        this.title = baseEntity.title
        this.description = baseEntity.description
        this.position = baseEntity.position
        this.rotation = baseEntity.rotation
        this.scale = baseEntity.scale
        this.id = baseEntity.id
    }

    constructor(
        title: String, description: String?,
        position: Vector3F, rotation: EulerAngle, scale: Vector3F,
        id: EntityId
    ) {
        this.title = title
        this.description = description
        this.position = position
        this.rotation = rotation
        this.scale = scale
        this.id = id
    }

    override var title: String
    var description: String?
    var position: Vector3F
    var rotation: EulerAngle
    var scale: Vector3F
    val id: EntityId

    abstract fun build(): EntityData

    open fun findById(id: EntityId): MutableEntity? =
        if (this.id == id) this else null

    /** @return `true` if `mutableEntity` was found and deleted. */
    open fun delete(mutableEntity: MutableEntity): Boolean = false

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> =
        emptyList()

    abstract fun getEditorPanels(): List<EntityEditorPanel<out MutableEntity>>
}

abstract class MutableEntityGroup(
    baseEntityData: EntityData
): MutableEntity(baseEntityData) {
    abstract val children: MutableList<MutableEntity>

    override fun findById(id: EntityId): MutableEntity? =
        super.findById(id)
            ?: children.firstNotNullOfOrNull { it.findById(id) }

    override fun delete(mutableEntity: MutableEntity): Boolean =
        if (children.remove(mutableEntity)) true else {
            children.any { it.delete(mutableEntity) }
        }
}

class MutableImportedEntityGroup(
    baseImportedEntityData: ImportedEntityData
) : MutableEntityGroup(baseImportedEntityData) {
    var objData: String = baseImportedEntityData.objData
        set(value) { field = value; importerResults = null }

    var objDataIsFileRef: Boolean = baseImportedEntityData.objDataIsFileRef
        set(value) { field = value; importerResults = null }

    var metadata: EntityMetadataProvider? = baseImportedEntityData.metadata
        set(value) { field = value; importerResults = null }

    private var importerResults: Importer.Results? = null
    private var importFail: Exception? = null
    val problems get() = (getImporterResults()?.errors ?: emptyList()).map { Problem("", it.message) } +
            listOfNotNull(importFail).map { Problem("", it.message) }

    private fun getImporterResults(): Importer.Results? =
        importerResults ?: try {
            importFail = null
            ObjImporter.doImport(objData, objDataIsFileRef, title) {
                metadata?.getMetadataFor(this.build())?.expectedPixelCount
            }.also {
                importerResults = it
            }
        } catch (e: Exception) {
            importFail = e
            null
        }

    override val children: MutableList<MutableEntity> get() =
        (getImporterResults()?.entities ?: emptyList()).map {
            object : MutableEntity(it.title, null, Vector3F.origin, EulerAngle.identity, Vector3F.origin, Model.Entity.nextId()) {
                override fun build(): EntityData {
                    return SurfaceDataForTest(it.title)
                }

                override fun getEditorPanels(): List<EntityEditorPanel<in MutableEntity>> =
                    emptyList()
            }
        }.toMutableList()

    override fun build(): ImportedEntityData =
        ImportedEntityData(title, description, position, rotation, scale, id, objData, objDataIsFileRef, metadata)

    override fun getEditorPanels() = listOf(
        TitleAndDescEntityEditorPanel,
        TransformEntityEditorPanel,
        ImportedEntityEditorPanel
    )

    fun reloadFile() {
        importerResults = null
        importFail = null
        getImporterResults()
    }
}

class MutableMovingHeadData(
    baseMovingHeadData: MovingHeadData
) : MutableEntity(baseMovingHeadData) {
    var baseDmxChannel: Int = baseMovingHeadData.baseDmxChannel
    var adapter: MovingHeadAdapter = baseMovingHeadData.adapter

    override fun build(): EntityData =
        MovingHeadData(title, description, position, rotation, scale, id, baseDmxChannel, adapter)

    override fun getEditorPanels() =
        listOf(
            TitleAndDescEntityEditorPanel,
            TransformEntityEditorPanel,
            MovingHeadEditorPanel
        )
}

class MutableLightBarData(
    baseLightBar: LightBarData
) : MutableEntity(baseLightBar) {
    var startVertex = baseLightBar.startVertex
    var endVertex = baseLightBar.endVertex

    override fun build(): EntityData =
        LightBarData(title, description, position, rotation, scale, id, startVertex, endVertex)

    override fun getEditorPanels() =
        listOf(
            TitleAndDescEntityEditorPanel,
            TransformEntityEditorPanel,
            LightBarEditorPanel
        )
}

class MutablePolyLineData(
    basePolyLine: PolyLineData
) : MutableEntity(basePolyLine) {
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
) : MutableEntity(baseGridData) {
    var rows = baseGridData.rows
    var columns = baseGridData.columns
    var rowGap = baseGridData.rowGap
    var columnGap = baseGridData.columnGap
    var direction = baseGridData.direction
    var zigZag = baseGridData.zigZag

    override fun build(): EntityData =
        GridData(title, description, position, rotation, scale, id, rows, columns, rowGap, columnGap, direction, zigZag)

    override fun getEditorPanels() =
        listOf(
            TitleAndDescEntityEditorPanel,
            TransformEntityEditorPanel,
            GridEditorPanel
        )
}

class MutableLightRingData(
    baseLightRing: LightRingData
) : MutableEntity(baseLightRing) {
    var radius = baseLightRing.radius
    var firstPixelRadians = baseLightRing.firstPixelRadians
    var pixelDirection = baseLightRing.pixelDirection

    override fun build(): EntityData =
        LightRingData(
            title, description, position, rotation, scale, id,
            radius, firstPixelRadians, pixelDirection
        )

    override fun getEditorPanels() =
        listOf(
            TitleAndDescEntityEditorPanel,
            TransformEntityEditorPanel,
            LightRingEditorPanel
        )
}

interface MutableFixtureConfig {
    val fixtureType: FixtureType

    fun build(): FixtureConfig
    fun getEditorView(editingController: EditingController<*>, mutableFixtureMapping: MutableFixtureMapping): View
}

interface MutableTransportConfig {
    val transportType: TransportType?

    fun build(): TransportConfig
    fun getEditorView(editingController: EditingController<*>, mutableFixtureMapping: MutableFixtureMapping): View
    fun toSummaryString(): String
}