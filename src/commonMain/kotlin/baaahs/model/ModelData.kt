package baaahs.model

import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.dmx.Shenzarpy
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.io.Fs
import baaahs.io.getResource
import baaahs.sim.FixtureSimulation
import baaahs.sim.SimulationEnv
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModelData(
    val title: String,
    val entities: List<EntityData>,
    val units: ModelUnit = ModelUnit.Meters
) {
    fun open(metadata: EntityMetadataProvider): Model {
        return object : Model() {
            //            entities.forEach { open(metadata) }
            override val name = title
            override val allEntities =
                entities.flatMap {
                    val entity = it.open(metadata)
                    if (entity is EntityGroup) listOf(entity) + entity.entities else listOf(entity)
                }
        }
    }
}

enum class ModelUnit {
    Inches,
    Feet,
    Centimeters,
    Meters
}

sealed interface EntityData {
    val title: String
    val description: String?
    val transformation: Matrix4F

    fun open(metadata: EntityMetadataProvider): Model.Entity
}

@Serializable @SerialName("ObjModel")
data class ObjModelData(
    override val title: String,
    override val description: String?,
    override val transformation: Matrix4F,
    val objData: String,
    val objDataIsFileRef: Boolean
) : EntityData {
    override fun open(metadata: EntityMetadataProvider): Model.Entity = object : Model.EntityGroup {
        val objModelLoader = ObjModelLoader.load(objData) {
            metadata.getMetadataFor(this@ObjModelData).expectedPixelCount
        }
        override val name: String get() = title
        override val description: String? get() = this@ObjModelData.description
        override val deviceType: DeviceType get() = PixelArrayDevice // TODO
        override val bounds: Pair<Vector3F, Vector3F> get() = boundingBox(objModelLoader.geomVertices)
        override val transformation: Matrix4F get() = TODO("not implemented")
        override val entities: List<Model.Entity> get() = objModelLoader.allEntities

        override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation? = null
    }
}

@Serializable @SerialName("MovingHead")
data class MovingHeadData(
    override val title: String,
    override val description: String?,
    override val transformation: Matrix4F
) : EntityData {
    override fun open(metadata: EntityMetadataProvider): Model.Entity =
        MovingHead(title, description, 0, Shenzarpy, transformation)
}

@Serializable @SerialName("LightBar")
data class LightBarData(
    override val title: String,
    override val description: String?,
    override val transformation: Matrix4F,
    val startVertex: Vector3F,
    val endVertex: Vector3F
) : EntityData {
    override fun open(metadata: EntityMetadataProvider): Model.Entity =
        LightBar(title, description, startVertex, endVertex, transformation)
}

@Serializable @SerialName("PolyLine")
data class PolyLineData(
    override val title: String,
    override val description: String?,
    override val transformation: Matrix4F,
    val segments: List<SegmentData>
) : EntityData {
    override fun open(metadata: EntityMetadataProvider): Model.Entity =
        PolyLine(title, description, segments.map {
            PolyLine.Segment(it.startVertex, it.endVertex, it.pixelCount)
        }, transformation)
}

@Serializable @SerialName("Grid")
data class GridData(
    override val title: String,
    override val description: String?,
    override val transformation: Matrix4F,
    val rows: Int,
    val columns: Int,
    val rowGap: Float,
    val columnGap: Float,
    val direction: Direction = Direction.ColumnsThenRows,
    val zigZag: Boolean = false
) : EntityData {
    enum class Direction {
        ColumnsThenRows,
        RowsThenColumns,
    }

    override fun open(metadata: EntityMetadataProvider): Model.Entity = Grid(
        title, description, transformation, rows, columns, rowGap, columnGap, direction, zigZag
    )
}

@Serializable
data class SegmentData(
    val startVertex: Vector3F,
    val endVertex: Vector3F,
    val pixelCount: Int
)

@Serializable @SerialName("LightRing")
data class LightRingData(
    override val title: String,
    override val description: String?,
    override val transformation: Matrix4F,
    val center: Vector3F,
    val radius: Float,
    val planeNormal: Vector3F,
    val firstPixelRadians: Float = 0f,
    val pixelDirection: LightRing.PixelDirection = LightRing.PixelDirection.Clockwise
) : EntityData {
    override fun open(metadata: EntityMetadataProvider): Model.Entity =
        LightRing(title, description, center, radius, planeNormal, firstPixelRadians, pixelDirection, transformation)
}

sealed interface EntityMetadataProvider {
    fun getMetadataFor(entity: EntityData): EntityMetadata
}

@Serializable
class ConstEntityMetadataProvider(val pixelCount: Int?): EntityMetadataProvider {
    override fun getMetadataFor(entity: EntityData): EntityMetadata =
        object : EntityMetadata {
            override val expectedPixelCount: Int? get() = pixelCount
        }
}

@Serializable
class StrandCountEntityMetadataProvider(
    val data: Map<String, Int>
) : EntityMetadataProvider {
    override fun getMetadataFor(entity: EntityData): EntityMetadata {
        TODO("not implemented")
    }

    companion object {
        suspend fun open(file: Fs.File): StrandCountEntityMetadataProvider {
            return open(file.read() ?: error("Unknown file $file."))
        }

        fun openResource(name: String): StrandCountEntityMetadataProvider {
            return open(getResource(name))
        }

        fun open(data: String): StrandCountEntityMetadataProvider {
            return data.split("\n")
                .map { it.split(Regex("\\s+")) }
                .associate { it[0] to it[1].toInt() * 60 }
                .let { StrandCountEntityMetadataProvider(it) }
        }
    }
}

interface EntityMetadata {
    val expectedPixelCount: Int?
}