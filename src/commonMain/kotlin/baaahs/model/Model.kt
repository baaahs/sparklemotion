package baaahs.model

import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.geom.*
import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.sim.BrainSurfaceSimulation
import baaahs.sim.FixtureSimulation
import baaahs.sim.SimulationEnv
import baaahs.visualizer.EntityVisualizer
import baaahs.visualizer.visualizerBuilder

class Model(
    val name: String,
    val entities: List<Entity>,
    val units: ModelUnit = ModelUnit.default
) : ModelInfo {
    val allEntities: List<Entity> =
        entities.flatMap { entity ->
            entity.containedEntities
            if (entity is EntityGroup) listOf(entity) + entity.entities else listOf(entity)
        }

    private val allEntitiesByName: Map<String, Entity> by lazy { allEntities.associateBy { it.name } }

    fun getEntity(name: String) = allEntitiesByName[name]

    fun findEntity(name: String) = getEntity(name)
        ?: error("Unknown model surface \"$name\".")

    val modelBounds by lazy {
        boundingBox(entities.flatMap { entity ->
            entity.bounds.let {
                listOf(
                    it.first.transform(entity.transformation),
                    it.second.transform(entity.transformation)
                )
            } }
        )
    }
    private val modelExtents by lazy { val (min, max) = modelBounds; max - min }
    private val modelCenter by lazy { center(modelBounds.toList()) }

    override val extents get() = modelExtents.let { if (it == Vector3F.origin) Vector3F(1f, 1f, 1f) else it }
    override val center: Vector3F get() = modelCenter

    fun generateFixtureMappings(): Map<ControllerId, List<FixtureMapping>> = emptyMap()

    fun visit(callback: (Entity) -> Unit) {
        entities.forEach { entity -> entity.visit(callback) }
    }

    interface Entity {
        val name: String
        val title: String get() = name
        val description: String?
        val deviceType: DeviceType
        /** Bounds in entity's local space. */
        val bounds: Pair<Vector3F, Vector3F>
        val position: Vector3F
        val rotation: EulerAngle
        val scale: Vector3F
        val transformation: Matrix4F
        val containedEntities: List<Entity> get() = listOf(this)

        fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation?
        fun createVisualizer(simulationEnv: SimulationEnv): EntityVisualizer<*>

        fun visit(callback: (Entity) -> Unit) = callback(this)
    }

    abstract class BaseEntity : Entity {
        override val transformation: Matrix4F
                by lazy { Matrix4F.fromPositionAndRotation(position, rotation) }
    }

    interface EntityGroup : Entity {
        val entities: List<Entity>

        override val containedEntities: List<Entity>
            get() = super.containedEntities + entities.flatMap { it.containedEntities }

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
    ) : BaseEntity(), EntityWithGeometry {
        override val deviceType: DeviceType
            get() = PixelArrayDevice
        override val bounds: Pair<Vector3F, Vector3F>
            get() = boundingBox(allVertices())

        open fun allVertices(): Collection<Vector3F> = geometry.vertices

        override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation =
            BrainSurfaceSimulation(this, simulationEnv)

        override fun createVisualizer(simulationEnv: SimulationEnv): EntityVisualizer<*> =
            visualizerBuilder.createSurfaceVisualizer(this, simulationEnv)
    }

    data class Line(
        private val geometry: Geometry,
        val vertexIndices: List<Int>
    ) {
        constructor(geometry: Geometry, vararg vertices: Int) :
                this(geometry, vertices.toList())

        val vertices: List<Vector3F> get() = vertexIndices.map { geometry.vertices[it] }
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

        val vertices: Array<Vector3F> = arrayOf(a, b, c)
    }
}