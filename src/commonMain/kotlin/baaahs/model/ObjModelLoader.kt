package baaahs.model

import baaahs.geom.Vector3F
import baaahs.io.getResource
import baaahs.model.Model.*
import baaahs.util.Logger

class ObjModelLoader(
    private val objResourceName: String,
    private val createSurface: (name: String, faces: List<Face>, lines: List<Line>) -> Surface
) {
    private val vertices: List<Vector3F>
    private val surfaces: List<Surface>

    val allEntities: List<Entity> get() = surfaces
    val geomVertices: List<Vector3F> get() = vertices

    init {
        val allVertices: MutableList<Vector3F> = mutableListOf()
        var surfaceBuilder: SurfaceBuilder? = null

        val surfaces: MutableList<Surface> = mutableListOf()
        fun buildSurface() {
            surfaceBuilder?.let { builder ->
                surfaces.add(builder.build())
                surfaceBuilder = null
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
                        surfaceBuilder = SurfaceBuilder(name = args.joinToString(" "))
                    }
                    "f" -> {
                        val vertIs = args.map { it.toInt() - 1 }
                        if (vertIs.size != 3) error("A face must have three vertices: $line")
                        (surfaceBuilder ?: error("No object?")).faces.add(Face(allVertices, vertIs[0], vertIs[1], vertIs[2]))
                    }
                    "l" -> {
                        val vertIs = args.map { it.toInt() - 1 }
                        (surfaceBuilder ?: error("No object?")).lines.add(Line(allVertices, vertIs))
                    }
                }
            }

        buildSurface()

        logger.debug { "${this::class.simpleName} has ${surfaces.size} panels and ${allVertices.size} vertices" }
        this.vertices = allVertices
        this.surfaces = surfaces
    }

    private inner class SurfaceBuilder(val name: String) {
        val faces = mutableListOf<Face>()
        val lines = mutableListOf<Line>()

        fun build(): Surface = createSurface(name, faces, lines)
    }

    companion object {
        private val logger = Logger<ObjModelLoader>()
    }
}