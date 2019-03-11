package baaahs

class SheepModel {
    lateinit var vertices: List<Point>
    lateinit var panels: List<Panel>

    fun load() {
        val vertices: MutableList<Point> = mutableListOf()
        val panels: MutableList<Panel> = mutableListOf()
        var currentPanel = Panel("initial")

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
                        val match = Regex("^G_([^_]+).*?\$").matchEntire(name)
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
                        currentPanel.lines.add(Line(points))
                    }
                }
            }

        println("Sheep model has ${panels.size} panels (and ${vertices.size} vertices)!")
        this.vertices = vertices
        this.panels = panels
    }

    data class Point(val x: Float, val y: Float, val z: Float)
    data class Line(val points: List<Point>)

    class Face(val vertexIds: List<Int>)

    class Faces {
        val vertices: MutableList<Point> = mutableListOf()
        val faces: MutableList<Face> = mutableListOf()
    }

    class Panel(val name: String) {
        val faces = Faces()
        val lines = mutableListOf<Line>()
    }

}

