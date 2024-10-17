package baaahs.model.importers

import baaahs.geom.EulerAngle
import baaahs.geom.Vector3F
import baaahs.io.getResource
import baaahs.model.EntityMetadata
import baaahs.model.Importer
import baaahs.model.Model
import baaahs.util.Logger

object ObjImporter : Importer {
    private val logger = Logger<ObjImporter>()

    fun import(
        objText: String,
        objName: String = "OBJ file",
        idPrefix: String,
        getEntityMetadata: (name: String) -> EntityMetadata? = { null }
    ): Importer.Results {
        val allVertices: MutableList<Vector3F> = mutableListOf()
        val geometry = Model.Geometry(allVertices)
        var objBuilder: ObjBuilder? = null
        val errors = arrayListOf<Importer.Error>()

        val surfaces: MutableList<Model.Surface> = mutableListOf()
        fun buildSurface() {
            objBuilder?.let { builder ->
                surfaces.add(builder.build())
                objBuilder = null
            }
        }

        fun addError(message: String, lineNumber: Int) {
            errors.add(Importer.Error(message, lineNumber + 1))
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
                        objBuilder = ObjBuilder(name = args.joinToString(" "), geometry, idPrefix, getEntityMetadata)
                    }
                    "f" -> {
                        val vertIs = try {
                            args.map { it.toInt() - 1 }
                        } catch (e: NumberFormatException) {
                            addError("Vertex indices must be integers: $line", lineNumber)
                            return@forEachIndexed
                        }
                        if (vertIs.size != 3) {
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

                        currentObjBuilder.faces.add(Model.Face(geometry, vertIs[0], vertIs[1], vertIs[2]))
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
                        currentObjBuilder.lines.add(Model.Line(geometry, vertIs))
                    }
                }
            }

        buildSurface()

        logger.debug { "$objName has ${surfaces.size} panels and ${allVertices.size} vertices" }

        return Results(surfaces, allVertices, errors)
    }

    private class ObjBuilder(
        val name: String,
        val geometry: Model.Geometry,
        val idPrefix: String,
        val getEntityMetadata: (name: String) -> EntityMetadata?
    ) {
        val faces = mutableListOf<Model.Face>()
        val lines = mutableListOf<Model.Line>()

        fun build(): Model.Surface {
            val entityMetadata = getEntityMetadata(name)
            return Model.Surface(
                name, null,
                entityMetadata?.expectedPixelCount,
                faces, lines, geometry,
                entityMetadata?.position ?: Vector3F.origin,
                entityMetadata?.rotation ?: EulerAngle.identity,
                entityMetadata?.scale ?: Vector3F.unit3d,
                id = "$idPrefix:$name"
            )
        }
    }

    class Results(
        override val entities: List<Model.Entity>,
        override val vertices: List<Vector3F>,
        override val errors: List<Importer.Error>
    ) : Importer.Results


    fun doImport(
        objData: String,
        objDataIsFileRef: Boolean,
        title: String,
        idPrefix: String,
        getEntityMetadata: (name: String) -> EntityMetadata? = { null }
    ): Importer.Results {
        val data = if (objDataIsFileRef) getResource(objData) else objData
        val name = if (objDataIsFileRef) objData else title
        return import(data, name, idPrefix, getEntityMetadata)
    }
}