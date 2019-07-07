package baaahs

class SheepModel {
    lateinit var vertices: List<Point>
    lateinit var panels: List<Panel>

    lateinit var eyes: List<MovingHead>

    val allPanels: List<Panel>
        get() = panels
    val partySide: List<Panel>
        get() = panels.filter { panel -> Regex("P$").matches(panel.name) }

    lateinit var panelNeighbors: Map<Panel, List<Panel>>

    fun load() {
        val vertices: MutableList<Point> = mutableListOf()
        val panels: MutableList<Panel> = mutableListOf()
        var currentPanel = Panel("initial")

        val panelsByEdge = mutableMapOf<List<Int>, MutableList<Panel>>()
        val edgesByPanel = mutableMapOf<Panel, MutableList<List<Int>>>()

        getResource("newsheep_processed.obj")
            .split("\n")
            .map { it.trim() }
            .forEach { line ->
                val parts = line.split(" ")
                val args = parts.subList(1, parts.size)

                when (parts[0]) {
                    "v" -> {
                        if (args.size != 3) throw Exception("invalid vertex line: $line")
                        val coords = args.map { it.toFloat() }
                        vertices.add(Point(coords[0], coords[1], coords[2]))
                    }
                    "g" -> {
                        var name = args.joinToString(" ")
                        val match = Regex("^G_0?([^_]+).*?\$").matchEntire(name)
                        if (match != null) {
                            name = match.groups[1]!!.value
                        }
                        currentPanel = Panel(name)
                        panels.add(currentPanel)
                    }
                    "f" -> {
                        val verts = args.map { it.toInt() - 1 }
                        currentPanel.faces.faces.add(Face(verts))
                    }
                    "l" -> {
                        val verts = args.map { it.toInt() - 1 }
                        val points = mutableListOf<Point>()
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
            MovingHead("leftEye", Point(-163.738f, 204.361f, 439.302f)),
            MovingHead("rightEye", Point(-103.738f, 204.361f, 439.302f))
        )
    }

    fun neighborsOf(panel: Panel) = panelNeighbors[panel] ?: emptyList()

    data class Point(val x: Float, val y: Float, val z: Float)
    data class Line(val points: List<Point>)

    class Face(val vertexIds: List<Int>)

    class Faces {
        val vertices: MutableList<Point> = mutableListOf()
        val faces: MutableList<Face> = mutableListOf()
    }

    class Panel(val name: String) : Surface {
        override val pixelCount = SparkleMotion.PIXEL_COUNT_UNKNOWN

        val faces = Faces()
        val lines = mutableListOf<Line>()

        override fun describe(): String = "Panel $name"
        override fun equals(other: Any?): Boolean = other is Panel && name == other.name
        override fun hashCode(): Int = name.hashCode()
    }

}

