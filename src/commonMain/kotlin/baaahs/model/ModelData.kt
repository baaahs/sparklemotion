package baaahs.model

import baaahs.dmx.Shenzarpy
import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.io.Fs
import baaahs.io.getResource
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModelData(
    val title: String,
    val entities: List<EntityData>,
    val units: ModelUnit = ModelUnit.Meters
) {
    fun open(): Model =
        Model(title, entities.map { entity -> entity.open() }, units)
}

enum class ModelUnit(val display: String) {
    Inches("\""),
    Feet("'"),
    Centimeters("cm"),
    Meters("m");

    companion object {
        val default = Centimeters
    }
}

@Polymorphic
sealed interface EntityData {
    val title: String
    val description: String?
    val position: Vector3F
    val rotation: EulerAngle
    val scale: Vector3F

    fun open(): Model.Entity
}

@Serializable @SerialName("ObjModel")
data class ObjModelData(
    override val title: String,
    override val description: String?,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    val objData: String,
    val objDataIsFileRef: Boolean,
    @Polymorphic
    val metadata: EntityMetadataProvider? = null
) : EntityData {
    override fun open(): Model.Entity {
        val objModelLoader = ObjModelLoader.load(objData) {
            metadata?.getMetadataFor(this@ObjModelData)?.expectedPixelCount
        }
        return ObjGroup(title, description, position, rotation, scale, metadata, objModelLoader)
    }
}

@Serializable @SerialName("MovingHead")
data class MovingHeadData(
    override val title: String,
    override val description: String?,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d

) : EntityData {
    override fun open(): Model.Entity =
        MovingHead(title, description, 0, Shenzarpy, position, rotation, scale)
}

@Serializable @SerialName("LightBar")
data class LightBarData(
    override val title: String,
    override val description: String?,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    val startVertex: Vector3F,
    val endVertex: Vector3F
) : EntityData {
    override fun open(): Model.Entity =
        LightBar(title, description, startVertex, endVertex, position, rotation, scale)
}

@Serializable @SerialName("PolyLine")
data class PolyLineData(
    override val title: String,
    override val description: String?,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    val segments: List<SegmentData>
) : EntityData {
    override fun open(): Model.Entity =
        PolyLine(title, description, segments.map {
            PolyLine.Segment(it.startVertex, it.endVertex, it.pixelCount)
        }, position, rotation, scale)
}

@Serializable @SerialName("Grid")
data class GridData(
    override val title: String,
    override val description: String?,
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
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

    override fun open(): Model.Entity = Grid(
        title, description, position, rotation, scale, rows, columns, rowGap, columnGap, direction, zigZag
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
    override val position: Vector3F = Vector3F.origin,
    override val rotation: EulerAngle = EulerAngle.identity,
    override val scale: Vector3F = Vector3F.unit3d,
    val center: Vector3F,
    val radius: Float,
    val planeNormal: Vector3F,
    val firstPixelRadians: Float = 0f,
    val pixelDirection: LightRing.PixelDirection = LightRing.PixelDirection.Clockwise
) : EntityData {
    override fun open(): Model.Entity =
        LightRing(title, description, center, radius, planeNormal, firstPixelRadians, pixelDirection, position, rotation, scale)
}

@Polymorphic
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
    override fun getMetadataFor(entity: EntityData): EntityMetadata =
        object : EntityMetadata {
            override val expectedPixelCount: Int?
                get() = data[entity.title]
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