package baaahs.model

import baaahs.geom.Vector3F
import baaahs.getResource
import baaahs.util.Logger

object ObjLoader {
    private val logger = Logger<ObjLoader>()

    fun loadResource(resourceName: String): ObjData {
        logger.debug { "Loading model data from $resourceName..." }
        return load(getResource(resourceName))
    }

    fun load(objData: String): ObjData {
        val vertices: MutableList<Vector3F> = mutableListOf()
        var surfaceName: String? = null
        var faceVertices = mutableListOf<List<Int>>()
        var lines = mutableListOf<Model.Line>()

        val surfacesByEdge = mutableMapOf<List<Int>, MutableList<String>>()
        val edgesBySurface = mutableMapOf<String, MutableList<List<Int>>>()

        val objs = mutableListOf<Obj>()

        fun buildAccumulatedObj() {
            surfaceName?.let { name ->
                val faces = faceVertices.map { v -> Model.Face(vertices, v[0], v[1], v[2]) }
                objs.add(Obj(name, faces, lines))
            }
            surfaceName = null
            faceVertices = mutableListOf()
            lines = mutableListOf()
        }

        objData
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
                        buildAccumulatedObj()
                        surfaceName = args.joinToString(" ")
                    }
                    "f" -> {
                        val verts = args.map { it.toInt() - 1 }
                        faceVertices.add(verts)
                    }
                    "l" -> {
                        val vertIs = args.map { it.toInt() - 1 }
                        val points = vertIs.map { vi -> vertices[vi] }

                        val sortedVertices = vertIs.sorted()
                        surfacesByEdge.getOrPut(sortedVertices) { mutableListOf() }.add(surfaceName!!)
                        edgesBySurface.getOrPut(surfaceName!!) { mutableListOf() }.add(sortedVertices)

                        lines.add(Model.Line(points))
                    }
                }
            }

        buildAccumulatedObj()

        logger.debug { "Loaded ${objs.size} objects and ${vertices.size} vertices" }
        return ObjData(objs, vertices)
    }

    data class Obj(val name: String, val faces: List<Model.Face>, val lines: List<Model.Line>)
    data class ObjData(val objs: List<Obj>, val vertices: List<Vector3F>)
}