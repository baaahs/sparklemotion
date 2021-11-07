package baaahs.model

import baaahs.geom.Vector3F
import baaahs.io.getResource
import baaahs.util.Logger

abstract class ObjModel(private val objResourceName: String) : Model() {
    override val geomVertices: List<Vector3F> get() = vertices
    private lateinit var vertices: List<Vector3F>
    private lateinit var surfaces: List<Surface>

    override val allEntities: List<Entity> get() = surfaces
    private val surfacesByName = mutableMapOf<String, Surface>()

    abstract fun createSurface(name: String, faces: List<Face>, lines: List<Line>): Surface

    open fun load() {
        val allVertices: MutableList<Vector3F> = mutableListOf()
        var nextSurfaceName: String? = null
        var nextSurfaceFaces = mutableListOf<Face>()
        var nextSurfaceLines = mutableListOf<Line>()

        val surfacesByEdge = mutableMapOf<List<Int>, MutableList<String>>()
        val edgesBySurface = mutableMapOf<String, MutableList<List<Int>>>()

        val surfaces: MutableList<Surface> = mutableListOf()
        fun buildSurface() {
            nextSurfaceName?.let { name ->
                val surface = createSurface(name, nextSurfaceFaces, nextSurfaceLines)
                surfaces.add(surface)
                surfacesByName[name] = surface

                nextSurfaceName = null
                nextSurfaceFaces = mutableListOf()
                nextSurfaceLines = mutableListOf()
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
                        if (args.size != 3) error("Invalid vertex: $line")
                        val coords = args.map { it.toFloat() }
                        allVertices.add(Vector3F(coords[0], coords[1], coords[2]))
                    }
                    "o" -> {
                        buildSurface()
                        nextSurfaceName = args.joinToString(" ")
                    }
                    "f" -> {
                        val vertIs = args.map { it.toInt() - 1 }
                        if (vertIs.size != 3) error("A face must have three vertices: $line")
                        nextSurfaceFaces.add(Face(allVertices, vertIs[0], vertIs[1], vertIs[2]))
                    }
                    "l" -> {
                        val vertIs = args.map { it.toInt() - 1 }
                        nextSurfaceLines.add(Line(allVertices, vertIs))

                        val sortedVerts = vertIs.sorted()
                        surfacesByEdge.getOrPut(sortedVerts) { mutableListOf() }.add(nextSurfaceName!!)
                        edgesBySurface.getOrPut(nextSurfaceName!!) { mutableListOf() }.add(sortedVerts)

                    }
                }
            }

        buildSurface()

        logger.debug { "${this::class.simpleName} has ${surfaces.size} panels and ${allVertices.size} vertices" }
        this.vertices = allVertices
        this.surfaces = surfaces
    }

    companion object {
        val logger = Logger<ObjModel>()
    }
}