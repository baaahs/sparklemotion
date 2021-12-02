package baaahs.model

import baaahs.device.DeviceType
import baaahs.device.PixelArrayDevice
import baaahs.geom.*
import baaahs.mapper.ControllerId
import baaahs.mapper.FixtureMapping
import baaahs.sim.BrainSurfaceSimulation
import baaahs.sim.FixtureSimulation
import baaahs.sim.SimulationEnv

abstract class Model : ModelInfo {
    abstract val name: String
    abstract val allEntities: List<Entity>

    private val allEntitiesByName: Map<String, Entity> by lazy { allEntities.associateBy { it.name } }

    fun getEntity(name: String) = allEntitiesByName[name]

    fun findEntity(name: String) = getEntity(name)
        ?: error("Unknown model surface \"$name\".")

    val modelBounds by lazy {
        boundingBox(allEntities.flatMap { entity -> entity.bounds.let { listOf(it.first, it.second)} })
    }
    private val modelExtents by lazy { val (min, max) = modelBounds; max - min }
    private val modelCenter by lazy { center(modelBounds.toList()) }

    override val extents get() = modelExtents.let { if (it == Vector3F.origin) Vector3F(1f, 1f, 1f) else it }
    override val center: Vector3F get() = modelCenter

    open fun generateFixtureMappings(): Map<ControllerId, List<FixtureMapping>> = emptyMap()

    interface Entity {
        val name: String
        val description: String?
        val deviceType: DeviceType
        val bounds: Pair<Vector3F, Vector3F>
        val transformation: Matrix4F

        fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation?
    }

    interface EntityGroup : Entity {
        val entities: List<Entity>
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
        override val description: String,
        val expectedPixelCount: Int?,
        val faces: List<Face>,
        val lines: List<Line>,
        override val geometry: Geometry,
        override val transformation: Matrix4F = Matrix4F.identity
    ) : EntityWithGeometry {
        override val deviceType: DeviceType
            get() = PixelArrayDevice
        override val bounds: Pair<Vector3F, Vector3F>
            get() = boundingBox(allVertices())

        open fun allVertices(): Collection<Vector3F> = geometry.vertices

        override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation =
            BrainSurfaceSimulation(this, simulationEnv)
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