package baaahs.model

import baaahs.geom.Vector3F
import baaahs.getResource
import baaahs.util.Logger

abstract class ObjModel(private val objResourceName: String) : Model() {
    override val movingHeads: List<MovingHead>
        get() = emptyList()
    override val geomVertices: List<Vector3F> get() = vertices
    lateinit var vertices: List<Vector3F>
    lateinit var surfaces: List<Surface>

    override val allSurfaces get() = surfaces
    override val allEntities: List<Entity> get() = allSurfaces + movingHeads
    private val surfacesByName = mutableMapOf<String, Surface>()

    abstract fun createSurface(name: String, faces: List<Face>, lines: List<Line>): Surface

    open fun load() {
        val vertices: MutableList<Vector3F> = mutableListOf()
        val panels: MutableList<Surface> = mutableListOf()
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
        this.surfaces = panels
    }

    companion object {
        val logger = Logger("ObjModel")
    }
}