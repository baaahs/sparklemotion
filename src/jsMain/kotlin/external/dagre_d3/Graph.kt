@file:JsModule("dagre-d3")
@file:JsQualifier("dagre.graphlib")

package external.dagre_d3

//external val dagreD3: DagreD3

external class Graph(options: dynamic) {
    fun graph(): dynamic
    fun setGraph(graph: dynamic): Graph
    fun setNode(id: dynamic, options: GraphOptions): Graph
    fun setEdge(fromId: dynamic, toId: dynamic, options: GraphOptions): Graph
    fun setDefaultEdgeLabel(block: () -> dynamic): Graph
    fun layout(): Graph
}

external interface GraphOptions {
    var label: String?
    var shape: String?
    var style: String?
    var curve: String?
    var `class`: String?
}