package baaahs.model

import baaahs.geom.Vector3F
import baaahs.io.getResource
import baaahs.model.Model.*
import baaahs.util.Logger

class ObjModelLoader(
    objText: String,
    objName: String = "OBJ file",
    private val expectedPixelCount: (name: String) -> Int?
) {
    private val vertices: List<Vector3F>
    private val surfaces: List<Surface>

    val allEntities: List<Surface> get() = surfaces
    val geomVertices: List<Vector3F> get() = vertices

    init {
        val allVertices: MutableList<Vector3F> = mutableListOf()
        val geometry = Geometry(allVertices)
        var surfaceBuilder: SurfaceBuilder? = null

        val surfaces: MutableList<Surface> = mutableListOf()
        fun buildSurface() {
            surfaceBuilder?.let { builder ->
                surfaces.add(builder.build())
                surfaceBuilder = null
            }
        }

        objText
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
                        surfaceBuilder = SurfaceBuilder(name = args.joinToString(" "), geometry)
                    }
                    "f" -> {
                        val vertIs = args.map { it.toInt() - 1 }
                        if (vertIs.size != 3) error("A face must have three vertices: $line")
                        (surfaceBuilder ?: error("No object?")).faces.add(Face(geometry, vertIs[0], vertIs[1], vertIs[2]))
                    }
                    "l" -> {
                        val vertIs = args.map { it.toInt() - 1 }
                        (surfaceBuilder ?: error("No object?")).lines.add(Line(geometry, vertIs))
                    }
                }
            }

        buildSurface()

        logger.debug { "$objName has ${surfaces.size} panels and ${allVertices.size} vertices" }
        this.vertices = allVertices
        this.surfaces = surfaces
    }

    private inner class SurfaceBuilder(val name: String, val geometry: Geometry) {
        val faces = mutableListOf<Face>()
        val lines = mutableListOf<Line>()

        fun build(): Surface =
            Surface(name, name, expectedPixelCount(name), faces, lines, geometry)
    }

    companion object {
        fun load(
            resourceName: String,
            expectedPixelCount: (name: String) -> Int?
        ): ObjModelLoader {
            logger.debug { "Loading model data from $resourceName..." }
            val objText = getResource(resourceName)
            return ObjModelLoader(objText, resourceName, expectedPixelCount)
        }

        private val logger = Logger<ObjModelLoader>()
    }
}