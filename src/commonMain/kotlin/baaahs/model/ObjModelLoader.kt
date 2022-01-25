package baaahs.model

import baaahs.geom.Vector3F
import baaahs.io.getResource
import baaahs.model.Model.*
import baaahs.util.Logger

class ObjModelLoader(
    objText: String,
    objName: String = "OBJ file",
    private val expectedPixelCount: (name: String) -> Int? = { null }
) {
    private val vertices: List<Vector3F>
    private val surfaces: List<Surface>

    val allEntities: List<Surface> get() = surfaces
    val geomVertices: List<Vector3F> get() = vertices
    val errors: List<Error>

    init {
        val allVertices: MutableList<Vector3F> = mutableListOf()
        val geometry = Geometry(allVertices)
        var objBuilder: ObjBuilder? = null
        val errors = arrayListOf<Error>()

        val surfaces: MutableList<Surface> = mutableListOf()
        fun buildSurface() {
            objBuilder?.let { builder ->
                surfaces.add(builder.build())
                objBuilder = null
            }
        }

        fun addError(message: String, lineNumber: Int) {
            errors.add(Error(message, lineNumber + 1))
        }

        objText
            .split("\n")
            .map { it.trim() }
            .forEachIndexed { lineNumber, line ->
                val parts = line.split(" ")
                val args = parts.subList(1, parts.size)

                when (parts[0]) {
                    "v" -> {
                        if (args.size != 3) {
                            addError("A vertex must have three coordinates: $line", lineNumber)
                            allVertices.add(Vector3F.origin)
                            return@forEachIndexed
                        }
                        val coords = try {
                            args.map { it.toFloat() }
                        } catch (e: NumberFormatException) {
                            addError("Vertex coordinates must be numbers: $line", lineNumber)
                            allVertices.add(Vector3F.origin)
                            return@forEachIndexed
                        }

                        allVertices.add(Vector3F(coords[0], coords[1], coords[2]))
                    }
                    "g", "o" -> {
                        buildSurface()
                        objBuilder = ObjBuilder(name = args.joinToString(" "), geometry)
                    }
                    "f" -> {
                        val vertIs = try {
                            args.map { it.toInt() - 1 }
                        } catch (e: NumberFormatException) {
                            addError("Vertex indices must be integers: $line", lineNumber)
                            return@forEachIndexed
                        }
                        if (vertIs.size != 3)  {
                            addError("A face must have three vertices: $line", lineNumber)
                            return@forEachIndexed
                        }

                        val currentObjBuilder = objBuilder
                        if (currentObjBuilder == null) {
                            addError("No current object.", lineNumber)
                            return@forEachIndexed
                        }

                        vertIs.forEach {
                            if (it > geometry.vertices.size) {
                                addError("No such vertex, index $it", lineNumber)
                                return@forEachIndexed
                            }
                        }

                        currentObjBuilder.faces.add(Face(geometry, vertIs[0], vertIs[1], vertIs[2]))
                    }
                    "l" -> {
                        val vertIs = try {
                            args.map { it.toInt() - 1 }
                        } catch (e: NumberFormatException) {
                            addError("Vertex indices must be integers: $line", lineNumber)
                            return@forEachIndexed
                        }
                        val currentObjBuilder = objBuilder
                        if (currentObjBuilder == null) {
                            addError("No current object.", lineNumber)
                            return@forEachIndexed
                        }
                        currentObjBuilder.lines.add(Line(geometry, vertIs))
                    }
                }
            }

        buildSurface()

        logger.debug { "$objName has ${surfaces.size} panels and ${allVertices.size} vertices" }
        this.vertices = allVertices
        this.surfaces = surfaces
        this.errors = errors
    }

    private inner class ObjBuilder(val name: String, val geometry: Geometry) {
        val faces = mutableListOf<Face>()
        val lines = mutableListOf<Line>()

        fun build(): Surface =
            Surface(name, name, expectedPixelCount(name), faces, lines, geometry)
    }

    data class Error(
        val message: String,
        val lineNumber: Int?
    )

    companion object {
        fun doImport(
            objData: String,
            objDataIsFileRef: Boolean,
            title: String,
            expectedPixelCount: (name: String) -> Int? = { null }
        ): ObjModelLoader {
            val data = if (objDataIsFileRef) getResource(objData) else objData
            val name = if (objDataIsFileRef) objData else title
            return ObjModelLoader(data, name, expectedPixelCount)
        }

        @Deprecated("No! Should come from an FS somehow!")
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