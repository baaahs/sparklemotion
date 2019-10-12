package baaahs

import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.geom.center
import baaahs.glsl.CylindricalModelSpaceUvTranslator
import baaahs.glsl.LinearModelSpaceUvTranslator
import baaahs.glsl.UvTranslator

abstract class Model<T : Model.Surface> {
    abstract val movingHeads: List<MovingHead>
    abstract val allSurfaces: List<T>
    abstract val geomVertices: List<Vector3F>
    abstract val defaultUvTranslator: UvTranslator

    private val allSurfacesByName: Map<String, T> by lazy { allSurfaces.associateBy { it.name } }

    fun findModelSurface(name: String) =
        allSurfacesByName[name] ?: throw RuntimeException("no such model surface $name")

    val allVertices by lazy {
        val allVertices = hashSetOf<Vector3F>()
        allSurfaces.map { allVertices.addAll(it.allVertices()) }
        allVertices
    }

    val modelBounds by lazy {
        boundingBox(allVertices)
    }

    val modelExtents by lazy {
        val (min, max) = modelBounds
        max - min
    }

    val modelCenter by lazy {
        center(allVertices)
    }

    /** A named surface in the geometry model. */
    interface Surface {
        val name: String
        val description: String
        val expectedPixelCount: Int?
        fun allVertices(): Collection<Vector3F>
        val faces: List<Face>
        val lines: List<Line>
    }

    data class Line(val vertices: List<Vector3F>)

    class Face(val vertexIds: List<Int>)
}

class Decom2019Model : ObjModel<Model.Surface>("decom-2019-panels.obj") {
    override val defaultUvTranslator: UvTranslator by lazy { LinearModelSpaceUvTranslator(this) }

    override fun createSurface(name: String, faces: MutableList<Face>, lines: MutableList<Line>): Surface {
        return SheepModel.Panel(name, 10 * 60, faces, lines)
    }
}

class SheepModel : ObjModel<SheepModel.Panel>("baaahs-model.obj") {
    override val defaultUvTranslator: UvTranslator by lazy { CylindricalModelSpaceUvTranslator(this) }
    private val pixelsPerPanel = hashMapOf<String, Int>()

    init {
        getResource("baaahs-panel-info.txt")
            .split("\n")
            .map { it.split(Regex("\\s+")) }
            .forEach { pixelsPerPanel[it[0]] = it[1].toInt() * 60 }
    }

    override fun createSurface(
        name: String,
        faces: MutableList<Face>,
        lines: MutableList<Line>
    ): Panel {
        val expectedPixelCount = pixelsPerPanel[name]
        if (expectedPixelCount == null) {
            logger.warn { "No pixel count found for $name" }
        }
        return Panel(name, expectedPixelCount, faces, lines)
    }

    class Panel(
        override val name: String,
        override val expectedPixelCount: Int? = null,
        override val faces: List<Face> = listOf(),
        override val lines: List<Line> = listOf()
    ) : Surface {
        override fun allVertices(): Collection<Vector3F> {
            val vertices = hashSetOf<Vector3F>()
            vertices.addAll(lines.flatMap { it.vertices })
            return vertices
        }

        override val description: String = "Panel $name"
        override fun equals(other: Any?): Boolean = other is Panel && name == other.name
        override fun hashCode(): Int = name.hashCode()
    }
}

abstract class ObjModel<T : Model.Surface>(val objResourceName: String) : Model<T>() {
    override val geomVertices: List<Vector3F> get() = vertices
    lateinit var vertices: List<Vector3F>
    lateinit var panels: List<T>

    lateinit var eyes: List<MovingHead>

    val allPanels: List<T>
        get() = panels
    val partySide: List<T>
        get() = panels.filter { panel -> Regex("P$").matches(panel.name) }

    override val movingHeads: List<MovingHead> get() = eyes
    override val allSurfaces get() = allPanels
    lateinit var surfaceNeighbors: Map<T, List<T>>
    private val surfacesByName = mutableMapOf<String, T>()

    abstract fun createSurface(
        name: String,
        faces: MutableList<Face>,
        lines: MutableList<Line>
    ): T

    fun load() {
        val vertices: MutableList<Vector3F> = mutableListOf()
        val panels: MutableList<T> = mutableListOf()
        var surfaceName: String? = null
        var faces = mutableListOf<Face>()
        var lines = mutableListOf<Line>()

        val surfacesByEdge = mutableMapOf<List<Int>, MutableList<String>>()
        val edgesBySurface = mutableMapOf<String, MutableList<List<Int>>>()

        fun buildSurface() {
            surfaceName?.let {
                val surface = createSurface(it, faces, lines)
                panels.add(surface)
                surfacesByName[it] = surface

                surfaceName = null
                faces = mutableListOf()
                lines = mutableListOf()
            }
        }

        getResource(objResourceName)
            .split("\n")
            .map { it.trim() }
            .forEach { line ->
                val parts = line.split(" ")
                val args = parts.subList(1, parts.size)

                when (parts[0]) {
                    "v" -> {
                        if (args.size != 3) throw Exception("invalid vertex line: $line")
                        val coords = args.map { it.toFloat() }
                        vertices.add(Vector3F(coords[0], coords[1], coords[2]))
                    }
                    "o" -> {
                        buildSurface()

                        val name = args.joinToString(" ")
                        surfaceName = name
                    }
                    "f" -> {
                        val verts = args.map { it.toInt() - 1 }
                        faces.add(Face(verts))
                    }
                    "l" -> {
                        val verts = args.map { it.toInt() - 1 }
                        val points = mutableListOf<Vector3F>()
                        for (vi in verts) {
                            val v = vertices[vi]
                            points.add(v)
                        }

                        val sortedVerts = verts.sorted()
                        surfacesByEdge.getOrPut(sortedVerts) { mutableListOf() }.add(surfaceName!!)
                        edgesBySurface.getOrPut(surfaceName!!) { mutableListOf() }.add(sortedVerts)

                        lines.add(Line(points))
                    }
                }
            }

        buildSurface()

        println("Sheep model has ${panels.size} panels (and ${vertices.size} vertices)!")
        this.vertices = vertices
        this.panels = panels

        fun neighborsOf(surface: T): List<T> {
            return edgesBySurface[surface.name]
                ?.flatMap { surfacesByEdge[it]?.toList() ?: emptyList() }
                ?.filter { it != surface.name }
                ?.map { surfacesByName[it]!! }
                ?: emptyList()
        }

        surfaceNeighbors = allPanels.associateWith { neighborsOf(it) }

        eyes = arrayListOf(
            MovingHead("leftEye", Vector3F(0f, 204.361f, 48.738f)),
            MovingHead("rightEye", Vector3F(0f, 204.361f, -153.738f))
        )
    }

    fun neighborsOf(panel: T) = surfaceNeighbors[panel] ?: emptyList()

    companion object {
        val logger = Logger("ObjModel")
    }
}

