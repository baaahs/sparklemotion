package baaahs.ui.diagnostics

import external.dagre_d3.Graph
import external.dagre_d3.d3
import js.objects.jso

class GraphDag(
    includePatchMods: Boolean = false
) : Dag(includePatchMods) {
    val graph = Graph(jso { directed = true })
        .also { it.setGraph(jso {}) }

    override fun declareNode(id: String, label: String, shape: String, style: String?) {
        graph.setNode(id, jso {
            this.label = label
            this.shape = shape
            this.style = style
        })
    }

    override fun declareLink(fromId: String, toId: String, label: String) {
        graph.setEdge(fromId, toId, jso {
            this.label = label
            this.style = "fill: none; stroke: #66f; stroke-width: 3px; stroke-dasharray: 5, 5;"
            this.curve = d3.curveBasis
        })
    }
}