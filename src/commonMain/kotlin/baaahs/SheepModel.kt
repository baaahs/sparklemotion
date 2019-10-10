package baaahs

import baaahs.geom.Vector3F
import baaahs.geom.center
import baaahs.geom.extents

abstract class Model<T : Model.Surface> {
    abstract val movingHeads: List<MovingHead>
    abstract val allSurfaces: List<T>

    private val allSurfacesByName: Map<String, T> by lazy { allSurfaces.associateBy { it.name } }

    fun findModelSurface(name: String) =
        allSurfacesByName[name] ?: throw RuntimeException("no such model surface $name")

    val allVertices by lazy {
        val allVertices = hashSetOf<Vector3F>()
        allSurfaces.map { allVertices.addAll(it.allVertices()) }
        allVertices
    }
    val modelExtents by lazy {
        extents(allVertices)
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

class SheepModel : Model<SheepModel.Panel>() {
    lateinit var vertices: List<Vector3F>
    lateinit var panels: List<Panel>

    lateinit var eyes: List<MovingHead>

    val allPanels: List<Panel>
        get() = panels
    val partySide: List<Panel>
        get() = panels.filter { panel -> Regex("P$").matches(panel.name) }

    override val movingHeads: List<MovingHead> get() = eyes
    override val allSurfaces get() = allPanels
    lateinit var panelNeighbors: Map<Panel, List<Panel>>

    fun load() {
        val vertices: MutableList<Vector3F> = mutableListOf()
        val panels: MutableList<Panel> = mutableListOf()
        var currentPanel = Panel("initial")

        val panelsByEdge = mutableMapOf<List<Int>, MutableList<Panel>>()
        val edgesByPanel = mutableMapOf<Panel, MutableList<List<Int>>>()

        val pixelsPerPanel = hashMapOf<String, Int>()
        getResource("baaahs-panel-info.txt")
            .split("\n")
            .map { it.split(Regex("\\s+")) }
            .forEach { pixelsPerPanel[it[0]] = it[1].toInt() * 60 }


        getResource("baaahs-model.obj")
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
                        val name = args.joinToString(" ")
                        val expectedPixelCount = pixelsPerPanel[name]
                        if (expectedPixelCount == null) {
                            logger.warn { "No pixel count found for $name" }
                        }
                        currentPanel = Panel(name, expectedPixelCount)
                        panels.add(currentPanel)
                    }
                    "f" -> {
                        val verts = args.map { it.toInt() - 1 }
                        currentPanel.faces.add(Face(verts))
                    }
                    "l" -> {
                        val verts = args.map { it.toInt() - 1 }
                        val points = mutableListOf<Vector3F>()
                        for (vi in verts) {
                            val v = vertices[vi]
                            points.add(v)
                        }

                        val sortedVerts = verts.sorted()
                        panelsByEdge.getOrPut(sortedVerts) { mutableListOf() }.add(currentPanel)
                        edgesByPanel.getOrPut(currentPanel) { mutableListOf() }.add(sortedVerts)

                        currentPanel.lines.add(Line(points))
                    }
                }
            }

        println("Sheep model has ${panels.size} panels (and ${vertices.size} vertices)!")
        this.vertices = vertices
        this.panels = panels

        fun neighborsOf(panel: Panel): List<Panel> {
            return edgesByPanel[panel]
                ?.flatMap { panelsByEdge[it]?.toList() ?: emptyList() }
                ?.filter { it != panel }
                ?: emptyList()
        }

        panelNeighbors = allPanels.associateWith { neighborsOf(it) }

        eyes = arrayListOf(
            MovingHead("leftEye", Vector3F(0f, 204.361f, 48.738f)),
            MovingHead("rightEye", Vector3F(0f, 204.361f, -153.738f))
        )
    }

    fun neighborsOf(panel: Panel) = panelNeighbors[panel] ?: emptyList()

    class Panel(override val name: String, override val expectedPixelCount: Int? = null) : Surface {
        override val faces = mutableListOf<Face>()
        override val lines = mutableListOf<Line>()

        override fun allVertices(): Collection<Vector3F> {
            val vertices = hashSetOf<Vector3F>()
            vertices.addAll(lines.flatMap { it.vertices })
            return vertices
        }

        override val description: String = "Panel $name"
        override fun equals(other: Any?): Boolean = other is Panel && name == other.name
        override fun hashCode(): Int = name.hashCode()
    }

    companion object {
        val logger = Logger("SheepModel")
    }
}

