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
    abstract val geomVertices: List<Vector3F>

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

    fun visitEntities(block: (vertices: List<Vector3F>?, entities: List<Entity>) -> Unit) {
        block(geomVertices, allEntities)
    }

    interface Entity {
        val name: String
        val description: String
        val deviceType: DeviceType
        val bounds: Pair<Vector3F, Vector3F>

        fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation
    }

    interface FixtureInfo {
        val position: Vector3F?
        val rotation: EulerAngle?
        val matrix: Matrix4?
    }

    /** A named surface in the geometry model. */
    open class Surface(
        override val name: String,
        override val description: String,
        val expectedPixelCount: Int?,
        val faces: List<Face>,
        val lines: List<Line>
    ) : Entity {
        override val deviceType: DeviceType
            get() = PixelArrayDevice

        override val bounds: Pair<Vector3F, Vector3F>
            get() = boundingBox(allVertices())

        open fun allVertices(): Collection<Vector3F> {
            val vertices = hashSetOf<Vector3F>()
            vertices.addAll(lines.flatMap { it.allVertices })
            return vertices
        }

        override fun createFixtureSimulation(simulationEnv: SimulationEnv): FixtureSimulation =
            BrainSurfaceSimulation(this, simulationEnv)
    }

    data class Line(val allVertices: List<Vector3F>, val vertexIndices: List<Int>) {
        constructor(vararg vertices: Vector3F) :
                this(vertices.toList(), vertices.mapIndexed { i: Int, _: Vector3F -> i })

        val vertices: List<Vector3F> get() = vertexIndices.map { allVertices[it] }
    }

    class Face(
        private val allVertices: List<Vector3F>,
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