package baaahs.scene

import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.MutableEditable
import baaahs.camelize
import baaahs.controller.*
import baaahs.device.FixtureType
import baaahs.dmx.DirectDmxControllerConfig
import baaahs.dmx.DmxManager
import baaahs.dmx.DmxTransportType
import baaahs.fixtures.FixtureOptions
import baaahs.fixtures.TransportConfig
import baaahs.fixtures.TransportType
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.model.*
import baaahs.model.importers.ObjImporter
import baaahs.scene.mutable.SceneBuilder
import baaahs.show.mutable.MutableDocument
import baaahs.sm.brain.BrainControllerConfig
import baaahs.sm.brain.BrainManager
import baaahs.sm.brain.BrainTransportType
import baaahs.sm.webapi.Problem
import baaahs.ui.View

class MutableScene(
    val model: MutableModel,
    val controllers: MutableMap<ControllerId, MutableControllerConfig>,
    val fixtureMappings: MutableMap<ControllerId, MutableList<MutableFixtureMapping>>
) : MutableDocument<Scene> {
    constructor(
        title: String,
        block: MutableScene.() -> Unit = {}
    ) : this(MutableModel(title, mutableListOf(), ModelUnit.default, 0f), mutableMapOf(), mutableMapOf()) {
        this.block()
    }

    override var title
        get() = model.title
        set(value) { model.title = value }

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> = listOf(
        ScenePropertiesEditorPanel(
            editableManager,
            SceneTitlePropsEditor(this),
            ModelUnitsPropsEditor(this)
        )
    )

    fun build(sceneBuilder: SceneBuilder): Scene {
        return Scene(
            model = model.build(sceneBuilder),
            entities = sceneBuilder.entityIds.all(),
            controllers = controllers.mapValues { (_, v) -> v.build(sceneBuilder) },
            fixtureMappings = fixtureMappings.mapValues { (_, v) -> v.map { it.build(sceneBuilder) } }
        )
    }

    override fun build(): Scene = build(SceneBuilder())
}

interface MutableControllerConfig {
    val controllerMeta: ControllerManager.Meta
    var title: String
    var defaultFixtureOptions: MutableFixtureOptions?
    var defaultTransportConfig: MutableTransportConfig?
    val supportedTransportTypes: List<TransportType>

    fun suggestId(): String =
        title.camelize()
    fun matches(controllerMatcher: ControllerMatcher): Boolean =
        controllerMatcher.matches(title, controllerMeta.controllerTypeName)

    fun build(sceneBuilder: SceneBuilder): ControllerConfig
    fun getEditorPanels(editingController: EditingController<*>): List<ControllerEditorPanel<*>>
}

class MutableBrainControllerConfig(
    override var title: String,
    var address: String?,
    override var defaultFixtureOptions: MutableFixtureOptions?,
    override var defaultTransportConfig: MutableTransportConfig?
) : MutableControllerConfig {
    override val controllerMeta: ControllerManager.Meta
        get() = BrainManager
    override val supportedTransportTypes: List<TransportType>
        get() = listOf(BrainTransportType)

    override fun build(sceneBuilder: SceneBuilder): ControllerConfig =
        BrainControllerConfig(title, address, defaultFixtureOptions?.build(), defaultTransportConfig?.build()
        )

    override fun getEditorPanels(editingController: EditingController<*>): List<ControllerEditorPanel<*>> =
        listOf(BrainControllerEditorPanel)
}

class MutableDirectDmxControllerConfig(
    override var title: String,
    override var defaultFixtureOptions: MutableFixtureOptions?,
    override var defaultTransportConfig: MutableTransportConfig?
) : MutableControllerConfig {
    override val controllerMeta: ControllerManager.Meta
        get() = DmxManager
    override val supportedTransportTypes: List<TransportType>
        get() = listOf(DmxTransportType)

    override fun build(sceneBuilder: SceneBuilder): ControllerConfig =
        DirectDmxControllerConfig(title, defaultFixtureOptions?.build(), defaultTransportConfig?.build())

    override fun getEditorPanels(editingController: EditingController<*>): List<ControllerEditorPanel<*>> =
        listOf(DirectDmxControllerEditorPanel)
}

class MutableSacnControllerConfig(
    override var title: String,
    var address: String,
    var universes: Int,
    override var defaultFixtureOptions: MutableFixtureOptions?,
    override var defaultTransportConfig: MutableTransportConfig?,
) : MutableControllerConfig {
    override val controllerMeta: ControllerManager.Meta
        get() = SacnManager
    override val supportedTransportTypes: List<TransportType>
        get() = listOf(DmxTransportType)

    override fun build(sceneBuilder: SceneBuilder): ControllerConfig =
        SacnControllerConfig(
            title, address, universes,
            defaultFixtureOptions?.build(),
            defaultTransportConfig?.build()
        )

    override fun getEditorPanels(editingController: EditingController<*>): List<ControllerEditorPanel<*>> =
        listOf(SacnControllerEditorPanel)
}

class MutableFixtureMapping(
    var entity: MutableEntity?,
    var fixtureOptions: MutableFixtureOptions?,
    var transportConfig: MutableTransportConfig?,
) {
    fun build(sceneBuilder: SceneBuilder): FixtureMappingData {
        val entityId = entity?.let { sceneBuilder.idFor(it.build()) }
        return FixtureMappingData(entityId, fixtureOptions?.build(), transportConfig?.build())
    }
}

class MutableModel(
    var title: String,
    val entities: MutableList<MutableEntity>,
    var units: ModelUnit,
    var initialViewingAngle: Float
) {
    fun build(sceneBuilder: SceneBuilder): ModelData {
        return ModelData(title, entities.map { sceneBuilder.idFor(it.build()) }, units, initialViewingAngle)
    }

    fun visit(callback: (MutableEntity) -> Unit) {
        entities.forEach { it.visit(callback) }
    }

    fun findByLocator(locator: EntityLocator): MutableEntity? =
        entities.firstNotNullOfOrNull { it.findByLocator(locator) }

    /** @return `true` if `mutableEntity` was found and deleted. */
    fun delete(mutableEntity: MutableEntity): Boolean =
        if (entities.remove(mutableEntity)) true else {
            entities.any { it.delete(mutableEntity) }
        }
}

abstract class MutableEntity(
    final override var title: String,
    var description: String?,
    var position: Vector3F,
    var rotation: EulerAngle,
    var scale: Vector3F,
    val locator: EntityLocator
) : MutableEditable {
    constructor(baseEntity: EntityData) : this(
        baseEntity.title, baseEntity.description, baseEntity.position, baseEntity.rotation, baseEntity.scale, baseEntity.locator
    )

    abstract fun build(): EntityData

    open fun visit(callback: (MutableEntity) -> Unit) =
        callback(this)

    open fun findByLocator(locator: EntityLocator): MutableEntity? =
        if (this.locator == locator) this else null

    fun matches(mutableEntityMatcher: MutableEntityMatcher): Boolean =
        mutableEntityMatcher.matches(title, description)

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

    override fun visit(callback: (MutableEntity) -> Unit) {
        super.visit(callback)
        children.forEach { it.visit(callback) }
    }

    override fun findByLocator(locator: EntityLocator): MutableEntity? =
        super.findByLocator(locator)
            ?: children.firstNotNullOfOrNull { it.findByLocator(locator) }

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

    private val baseEntityMetadata = baseImportedEntityData.entityMetadata

    private fun getImporterResults(): Importer.Results? =
        importerResults ?: try {
            importFail = null
            ObjImporter.doImport(objData, objDataIsFileRef, title, locator) { childName ->
                val fromProvider = metadata?.getMetadataFor(childName)
                baseEntityMetadata[childName]?.plus(fromProvider)
                    ?: fromProvider
            }.also {
                importerResults = it
            }
        } catch (e: Exception) {
            importFail = e
            null
        }

    override val children: MutableList<MutableEntity> =
        (getImporterResults()?.entities ?: emptyList()).map { childEntity ->
            val entityMetadata = baseImportedEntityData.entityMetadata[childEntity.name]
                ?: EntityMetadata.defaults
            MutableChildMetadata(
                childEntity,
                entityMetadata.position ?: Vector3F.origin,
                entityMetadata.rotation ?: EulerAngle.identity,
                entityMetadata.scale ?: Vector3F.unit3d
            )
        }.toMutableList()

    override fun build(): ImportedEntityData =
        ImportedEntityData(
            title, description,
            position, rotation, scale, locator,
            objData, objDataIsFileRef, metadata,
            buildMap {
                children.forEach { child ->
                    val entityMetadata = EntityMetadata(child.position, child.rotation, child.scale)
                    if (!entityMetadata.isDefaults())
                        put(child.title, entityMetadata)
                }
            }
        )

    override fun getEditorPanels() = listOf(ImportedEntityEditorPanel)

    fun reloadFile() {
        importerResults = null
        importFail = null
        getImporterResults()
    }

    inner class MutableChildMetadata(
        private val childEntity: Model.Entity,
        position: Vector3F,
        rotation: EulerAngle,
        scale: Vector3F
    ) : MutableEntity(
        childEntity.title, null, position, rotation, scale, childEntity.locator
    ) {
        val childId = nextChildId++

        init {
            println("New MutableChildMeta(parent=${this@MutableImportedEntityGroup.locator}).child for ${childEntity.title}, id=${childEntity.locator} childId=$childId")
            if (childEntity.title == "F3P") {
                Exception().printStackTrace()
            }
        }

        override fun build(): EntityData =
            SurfaceDataForTest(
                childEntity.title, childEntity.description,
                position, rotation, scale, locator
            )

        override fun getEditorPanels(): List<EntityEditorPanel<in MutableEntity>> =
            listOf(
                TitleAndDescEntityEditorPanel,
                TransformEntityEditorPanel
            )
    }

    companion object {
        var nextChildId = 0
    }
}

class MutableMovingHeadData(
    baseMovingHeadData: MovingHeadData
) : MutableEntity(baseMovingHeadData) {
    var baseDmxChannel: Int = baseMovingHeadData.baseDmxChannel
    var adapter: MovingHeadAdapter = baseMovingHeadData.adapter

    override fun build(): EntityData =
        MovingHeadData(title, description, position, rotation, scale, locator, baseDmxChannel, adapter)

    override fun getEditorPanels() = listOf(MovingHeadEditorPanel)
}

class MutableLightBarData(
    baseLightBar: LightBarData
) : MutableEntity(baseLightBar) {
    var startVertex = baseLightBar.startVertex
    var endVertex = baseLightBar.endVertex

    override fun build(): EntityData =
        LightBarData(title, description, position, rotation, scale, locator, startVertex, endVertex)

    override fun getEditorPanels() = listOf(LightBarEditorPanel)
}

class MutablePolyLineData(
    basePolyLine: PolyLineData
) : MutableEntity(basePolyLine) {
    var segments = basePolyLine.segments

    override fun build(): EntityData =
        PolyLineData(title, description, position, rotation, scale, locator, segments)

    override fun getEditorPanels() =
        emptyList<EntityEditorPanel<MutablePolyLineData>>()
//        listOf(
//            custom EditorPanel(this)
//        )
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
    var stagger = baseGridData.stagger

    override fun build(): EntityData =
        GridData(title, description, position, rotation, scale, locator, rows, columns, rowGap, columnGap, direction, zigZag, stagger)

    override fun getEditorPanels() = listOf(GridEditorPanel)
}

class MutableLightRingData(
    baseLightRing: LightRingData
) : MutableEntity(baseLightRing) {
    var radius = baseLightRing.radius
    var firstPixelRadians = baseLightRing.firstPixelRadians
    var pixelDirection = baseLightRing.pixelDirection

    override fun build(): EntityData =
        LightRingData(
            title, description, position, rotation, scale, locator,
            radius, firstPixelRadians, pixelDirection
        )

    override fun getEditorPanels() = listOf(LightRingEditorPanel)
}

interface MutableFixtureOptions {
    val fixtureType: FixtureType

    fun build(): FixtureOptions
    fun getEditorView(editingController: EditingController<*>): View
}

interface MutableTransportConfig {
    val transportType: TransportType

    fun build(): TransportConfig
    fun getEditorView(editingController: EditingController<*>): View
    fun toSummaryString(): String
}

class MutableEntityMatcher(val searchString: String = "") {
    private val searchTerms = searchString.lowercase().split(" ")

    fun matches(
        mutableEntity: MutableEntity?
    ): Boolean {
        if (searchTerms.isEmpty()) return true

        return mutableEntity?.matches(this) == true
    }

    fun matches(vararg s: String?): Boolean {
        return s.filterNotNull().any { searchTarget ->
            searchTerms.any { searchTarget.lowercase().contains(it) }
        }
    }
}