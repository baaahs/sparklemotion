package baaahs.model

import baaahs.controller.ControllerId
import baaahs.device.FixtureType
import baaahs.device.PixelArrayDevice
import baaahs.fixtures.FixtureMapping
import baaahs.fixtures.FixtureOptions
import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.geom.center
import baaahs.geom.compose
import baaahs.model.WtfMaths.cross
import baaahs.sim.FixtureSimulation
import baaahs.sim.simulations
import baaahs.sm.webapi.Problem
import baaahs.visualizer.EntityAdapter
import baaahs.visualizer.entity.ItemVisualizer
import kotlin.math.abs

class Model(
    val name: String,
    val entities: List<Entity>,
    val units: ModelUnit = ModelUnit.default,
    val initialViewingAngle: Float = 0f
) : ModelInfo {
    @Deprecated("Find all model entities some other way, like with visit().")
    val allEntities: List<Entity> =
        entities.flatMap { entity ->
            entity.containedEntities
            if (entity is EntityGroup) listOf(entity) + entity.entities else listOf(entity)
        }

    val problems get() = buildList { visit { addAll(it.problems) } }

    fun findEntityByNameOrNull(name: String) =
        entities.firstNotNullOfOrNull { it.findByNameOrNull(name) }

    fun findEntityByName(name: String) =
        findEntityByNameOrNull(name)
            ?: error("Unknown model entity \"$name\".")

    fun findEntityByLocator(locator: EntityLocator) =
        entities.firstNotNullOfOrNull { it.findByLocator(locator) }

    val modelBounds by lazy {
        boundingBox(entities.flatMap { entity ->
            entity.worldBounds.toList()
        })
    }
    private val modelExtents by lazy { val (min, max) = modelBounds; max - min }
    private val modelCenter by lazy { center(modelBounds.toList()) }

    // No zero-sized coordinates so we don't get division by zero errors later.
    override val extents get() = modelExtents.let { extent ->
        Vector3F(
            if (abs(extent.x) < 0.0001) 1f else extent.x,
            if (abs(extent.y) < 0.0001) 1f else extent.y,
            if (abs(extent.z) < 0.0001) 1f else extent.z
        )
    }

    override val center: Vector3F get() = modelCenter

    fun generateFixtureMappings(): Map<ControllerId, List<FixtureMapping>> = emptyMap()

    fun visit(callback: (Entity) -> Unit) {
        entities.forEach { entity -> entity.visit(callback) }
    }

    interface Entity : FixtureInfo {
        val name: String
        val title: String get() = name
        val description: String?
        val defaultFixtureOptions: FixtureOptions?
            get() = null
        val fixtureType: FixtureType
        /** Bounds in entity's local space. */
        override val bounds: Pair<Vector3F, Vector3F>
        /** The center of the entity's bounding box, in local space. */
        val center: Vector3F get() = center(bounds)
        /** The centroid of the entity, in local space. */
        val centroid: Vector3F get() = center
        override val position: Vector3F
        override val rotation: EulerAngle
        val scale: Vector3F
        override val transformation: Matrix4F
        val worldBounds get() = bounds.let { (min, max) ->
            min.transform(transformation) to max.transform(transformation)
        }
        val containedEntities: List<Entity> get() = listOf(this)
        val problems: Collection<Problem>
        val locator: EntityLocator

        fun createFixtureSimulation(adapter: EntityAdapter): FixtureSimulation?
        fun createVisualizer(adapter: EntityAdapter): ItemVisualizer<out Entity>

        fun findByLocator(locator: EntityLocator): Entity? =
            if (locator == this.locator) this else null

        fun findByNameOrNull(name: String): Entity? =
            if (name == this.name) this else null

        fun visit(callback: (Entity) -> Unit) = callback(this)
    }

    abstract class BaseEntity : Entity {
        override val transformation: Matrix4F
                by lazy { Matrix4F.compose(position, rotation, scale) }

        override val problems: Collection<Problem>
            get() = emptyList()
    }

    interface EntityGroup : Entity {
        val entities: List<Entity>

        override val containedEntities: List<Entity>
            get() = super.containedEntities + entities.flatMap { it.containedEntities }

        override fun findByLocator(locator: EntityLocator): Entity? =
            super.findByLocator(locator)
                ?: entities.firstNotNullOfOrNull { it.findByLocator(locator) }

        override fun findByNameOrNull(name: String): Entity? =
            super.findByNameOrNull(name)
                ?: entities.firstNotNullOfOrNull { it.findByNameOrNull(name) }

        override fun visit(callback: (Entity) -> Unit) {
            super.visit(callback).also {
                entities.forEach { it.visit(callback) }
            }
        }
    }

    interface EntityWithGeometry: Entity {
        val geometry: Geometry
    }

    class Geometry(
        val vertices: List<Vector3F>
    )

    interface FixtureInfo {
        val transformation: Matrix4F
        val bounds: Pair<Vector3F, Vector3F>

        val position: Vector3F
            get() = transformation.position
        val rotation: EulerAngle
            get() = transformation.rotation
    }

    /** A named surface in the geometry model. */
    open class Surface(
        override val name: String,
        override val description: String? = null,
        val expectedPixelCount: Int?,
        val faces: List<Face>,
        val lines: List<Line>,
        override val geometry: Geometry,
        override val position: Vector3F = Vector3F.origin,
        override val rotation: EulerAngle = EulerAngle.identity,
        override val scale: Vector3F = Vector3F.unit3d,
        override val locator: EntityLocator = EntityLocator.next()
    ) : BaseEntity(), EntityWithGeometry {
        override val defaultFixtureOptions: PixelArrayDevice.Options?
            get() = PixelArrayDevice.Options(pixelCount = expectedPixelCount)
        override val fixtureType: FixtureType
            get() = PixelArrayDevice
        override val bounds: Pair<Vector3F, Vector3F>
            get() = boundingBox(allVertices())
        override val centroid: Vector3F
            get() = faces.fold(Vector3F()) { acc, face -> acc + face.centroid * face.area } /
                    faces.sumOf { it.area.toDouble() }

        open fun allVertices(): Collection<Vector3F> = faces.flatMap { it.vertices.toList() }

        override fun createFixtureSimulation(adapter: EntityAdapter): FixtureSimulation =
            simulations.forSurface(this, adapter)

        override fun createVisualizer(adapter: EntityAdapter) =
            adapter.createSurfaceVisualizer(this)
    }

    /** A non-auto-closing line; for a triangle, there should be four vertexIndices. */
    data class Line(
        private val geometry: Geometry,
        val vertexIndices: List<Int>
    ) {
        constructor(geometry: Geometry, vararg vertices: Int) :
                this(geometry, vertices.toList())

        val vertices: List<Vector3F> get() = vertexIndices.map { geometry.vertices[it] }

        fun shortestDistanceTo(point: Vector3F): Float? {
            return List(vertexIndices.size - 1) { index ->
                // From https://math.stackexchange.com/questions/1905533/find-perpendicular-distance-from-point-to-line-in-3d
                //  double computeDistance(vec3 point, vec3 start, vec3 end) {
                //      vec3 d = (end - start) / end.distance(start);
                //      vec3 v = point - start;
                //      double t = v.dot(d);
                //      vec3 P = start + t * d;
                //      return P.distance(point);
                //  }

                val start = vertices[index]
                val end = vertices[(index + 1) % vertexIndices.size]
                val lineVector = end - start
                val lineDirection = lineVector / lineVector.length()
                val vectorToLineStart = point - start
                val t = vectorToLineStart.dot(lineDirection)
                val p = start + lineDirection * t
                (p - point).length()
            }.minOrNull()
        }
    }

    class Face(
        private val geometry: Geometry,
        val vertexA: Int,
        val vertexB: Int,
        val vertexC: Int
    ) {
        constructor(a: Vector3F, b: Vector3F, c: Vector3F) : this(Geometry(listOf(a, b, c)), 0, 1, 2)

        val a: Vector3F get() = geometry.vertices[vertexA]
        val b: Vector3F get() = geometry.vertices[vertexB]
        val c: Vector3F get() = geometry.vertices[vertexC]

        val vertices: Array<Vector3F> get() = arrayOf(a, b, c)

        val centroid: Vector3F get() = (a + b + c) / 3f
        val area: Float get() = 0.5f * (b - a).cross(c - a).length()
    }
}

typealias EntityId = String