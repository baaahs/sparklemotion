package baaahs.ui.diagnostics

import baaahs.gl.patch.DefaultValueNode
import baaahs.gl.patch.ExprNode
import baaahs.gl.patch.ProgramNode
import baaahs.show.live.LinkedPatch
import baaahs.show.live.OpenPatch
import baaahs.ui.xComponent
import external.dagre_d3.Graph
import external.dagre_d3.render
import external.dagre_d3.select
import kotlinx.dom.clear
import kotlinx.js.jso
import org.w3c.dom.svg.SVGGElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.svg
import react.dom.svg.ReactSVG.g

private val DagView = xComponent<DagProps>("Dag") { props ->
    val svgRef = ref<SVGGElement>()
    val gRef = ref<SVGGElement>()

    onMount(gRef.current, props.rootNode) {
        gRef.current?.let { gEl ->
            gEl.clear()

            val dag = Dag(props.rootNode)
            val g = dag.graph

            // Create the renderer
            val renderer: dynamic = render()

            // Set up an SVG group so that we can translate the final graph.
//            val svgEl = select(gEl)
            val svgGroup = select(gEl)

            // Run the renderer. This is what draws the final graph.
            renderer(svgGroup, g)
            val graphHuh = (g.asDynamic()).graph()
            svgRef.current!!.style.width = graphHuh.width + "px"
            svgRef.current!!.style.height = graphHuh.height + "px"

            // Center the graph
//            val xCenterOffset: Unit = (svg.attr("width") - g.graph().width) / 2
//            svgGroup.attr("transform", "translate($xCenterOffset, 20)")
//            svg.attr("height", g.graph().height + 40)
        }
    }

    svg {
        ref = svgRef
        g {
            ref = gRef
        }
    }
}

class Dag(rootNode: ProgramNode) {
    private var nextNode = 0
    private val nodes = mutableMapOf<Any, String>()
    val graph = Graph()

    init {
        graph.setGraph(jso {})
            .setDefaultEdgeLabel { jso {} }
        visit(rootNode)
    }

    fun declareNode(id: String, label: String, shape: String, style: String? = null) {
        graph.setNode(id, jso {
            this.label = label
            this.shape = shape
            this.style = style
        })
    }

    fun declareLink(fromId: String, toId: String, label: String) {
        graph.setEdge(fromId, toId, jso {
            this.label = label
        })
    }

    fun visit(node: OpenPatch.DataSourceLink) =
        nodes.getOrPut(node) {
            "DS${nextNode++}".also {
                declareNode(it, node.title, "rect")
            }
        }

    fun visit(node: DefaultValueNode) =
        nodes.getOrPut(node) {
            "V${nextNode++}".also {
                declareNode(it, node.getExpression("pfx").s, "ellipse")
            }
        }

    fun visit(node: ExprNode) =
        nodes.getOrPut(node) {
            "E${nextNode++}".also {
                declareNode(it, node.getExpression("pfx").s, "ellipse")
            }
        }

    fun visit(node: LinkedPatch): String {
        val id = nodes.getOrPut(node) {
            "P${nextNode++}".also {
                declareNode(it, node.title, "circle", "fill:pink")
            }
        }

        node.incomingLinks.forEach { (linkId, toNode) ->
            val inputPort = node.shader.inputPorts.first { it.id == linkId }

            declareLink(visit(toNode), id, inputPort.title)
        }
        return id
    }

    fun visit(node: ProgramNode) =
        when (node) {
            is OpenPatch.DataSourceLink -> visit(node)
            is DefaultValueNode -> visit(node)
            is ExprNode -> visit(node)
            is LinkedPatch -> visit(node)
            else -> error("Huh? Unknown node type ${node::class}")
        }
}

external interface DagProps : Props {
    var rootNode: ProgramNode
}

fun RBuilder.dag(handler: RHandler<DagProps>) =
    child(DagView, handler = handler)