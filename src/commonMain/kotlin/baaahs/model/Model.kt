package baaahs.model

import baaahs.device.DeviceType
import baaahs.geom.Matrix4
import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.geom.center
import baaahs.sim.BrainSurfaceSimulation
import baaahs.sim.FixtureSimulation
import baaahs.sim.SimulationEnv

abstract class Model : ModelInfo {
    abstract val name: String
    open val movingHeads: List<MovingHead> get() = emptyList()
    open val allSurfaces: List<Surface> get() = emptyList()
    abstract val allEntities: List<Entity>

    open val geomVertices: List<Vector3F> get() = emptyList()

    private val allSurfacesByName: Map<String, Entity> by lazy { allEntities.associateBy { it.name } }

    fun findEntity(name: String) =
        allSurfacesByName[name] ?: throw RuntimeException("no such model surface $name")

    fun getEntity(name: String) = allSurfacesByName[name]

    private val allVertices by lazy {
        hashSetOf<Vector3F>().apply { allSurfaces.map { addAll(it.allVertices()) } }
    }

    val modelBounds by lazy {
        boundingBox(allEntities.flatMap { entity -> entity.bounds.let { listOf(it.first, it.second)} })
    }
    private val modelExtents by lazy { val (min, max) = modelBounds; max - min }
    private val modelCenter by lazy { center(allVertices) }

    override val extents get() = modelExtents.let { if (it == Vector3F.origin) Vector3F(1f, 1f, 1f) else it }
    override val center: Vector3F get() = modelCenter

    interface Entity {
        val name: String
        val description: String
        val deviceType: DeviceType
        val bounds: Pair<Vector3F, Vector3F>

        fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation
    }

    interface FixtureInfo {
        val origin: Vector3F?
        val heading: Vector3F?
        val matrix: Matrix4?
    }

    /** A named surface in the geometry model. */
    open class Surface(
        override val name: String,
        override val description: String,
        override val deviceType: DeviceType,
        val expectedPixelCount: Int?,
        val faces: List<Face>,
        val lines: List<Line>
    ) : Entity {
        override val bounds: Pair<Vector3F, Vector3F>
            get() = boundingBox(allVertices())

        open fun allVertices(): Collection<Vector3F> {
            val vertices = hashSetOf<Vector3F>()
            vertices.addAll(lines.flatMap { it.vertices })
            return vertices
        }

        override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation =
            BrainSurfaceSimulation(this, simulationEnv)
    }

    data class Line(val vertices: List<Vector3F>) {
        constructor(vararg vertices: Vector3F) : this(vertices.toList())
    }

    class Face(
        internal val allVertices: List<Vector3F>,
        val vertexA: Int,
        val vertexB: Int,
        val vertexC: Int
    ) {
        constructor(a: Vector3F, b: Vector3F, c: Vector3F) : this(listOf(a, b, c), 0, 1, 2)

        val a: Vector3F get() = allVertices[vertexA]
        val b: Vector3F get() = allVertices[vertexB]
        val c: Vector3F get() = allVertices[vertexC]

        val vertices: Array<Vector3F> = arrayOf(a, b, c)
    }
}