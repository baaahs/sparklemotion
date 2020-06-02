package baaahs.model

import baaahs.Logger
import baaahs.geom.Vector3F
import baaahs.getResource

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

    abstract fun createSurface(name: String, faces: List<Face>, lines: List<Line>): T

    open fun load() {
        val vertices: MutableList<Vector3F> = mutableListOf()
        val panels: MutableList<T> = mutableListOf()
        var surfaceName: String? = null
        var faceVerts = mutableListOf<List<Int>>()
        var lines = mutableListOf<Line>()

        val surfacesByEdge = mutableMapOf<List<Int>, MutableList<String>>()
        val edgesBySurface = mutableMapOf<String, MutableList<List<Int>>>()

        fun buildSurface() {
            surfaceName?.let {
                val faces = faceVerts.map { Face(vertices, it[0], it[1], it[2]) }
                val surface = createSurface(it, faces, lines)
                panels.add(surface)
                surfacesByName[it] = surface

                surfaceName = null
                faceVerts = mutableListOf()
                lines = mutableListOf()
            }
        }

        logger.debug { "Loading model data from $objResourceName..." }
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
                        surfaceName = args.joinToString(" ")
                    }
                    "f" -> {
                        val verts = args.map { it.toInt() - 1 }
                        faceVerts.add(verts)
                    }
                    "l" -> {
                        val vertIs = args.map { it.toInt() - 1 }
                        val points = vertIs.map { vi -> vertices[vi] }

                        val sortedVerts = vertIs.sorted()
                        surfacesByEdge.getOrPut(sortedVerts) { mutableListOf() }.add(surfaceName!!)
                        edgesBySurface.getOrPut(surfaceName!!) { mutableListOf() }.add(sortedVerts)

                        lines.add(Line(points))
                    }
                }
            }

        buildSurface()

        logger.debug { "${this::class.simpleName} has ${panels.size} panels and ${vertices.size} vertices" }
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
            MovingHead(
                "leftEye",
                Vector3F(0f, 204.361f, 48.738f)
            ),
            MovingHead(
                "rightEye",
                Vector3F(0f, 204.361f, -153.738f)
            )
        )
    }

    fun neighborsOf(panel: T) = surfaceNeighbors[panel] ?: emptyList()

    companion object {
        val logger = Logger("ObjModel")
    }
}