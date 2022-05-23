package baaahs.ui.diagnostics

import baaahs.app.ui.appContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslProgramImpl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import external.dagre_d3.d3
import external.dagre_d3.dagreD3
import org.w3c.dom.svg.SVGGElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.svg
import react.dom.svg.ReactSVG.g
import react.useContext
import kotlin.math.min

private val DagView = xComponent<DagProps>("Dag", isPure = true) { props ->
    val appContext = useContext(appContext)
    val style = appContext.allStyles.uiComponents

    val svgRef = ref<SVGGElement>()
    val gRef = ref<SVGGElement>()

    val dagGraph = memo(props.program) {
        val program = props.program as? GlslProgramImpl
        val linkedProgram = program?.linkedProgram
        val dag = linkedProgram?.let { Dag().apply { visit(it) } }
        dag?.graph
    }

    val zoom = memo {
        d3.zoom()
            .scaleExtent(arrayOf(1/3, 3))
    }
    onMount {
        val d3Svg = d3.select(svgRef.current!!)
        val d3G = d3.select(gRef.current!!)

        zoom.on("zoom") {
            d3G.attr("transform", d3.event.transform)
        }

        d3Svg.call(zoom)
    }

    val zoomToFit by handler(dagGraph) {
        val gEl = gRef.current!!
        val svgEl = svgRef.current!!
        console.log("svg width=${svgEl.clientWidth} height=${svgEl.clientHeight}")
        val graph = dagGraph!!.graph()
        val xScale = svgEl.clientWidth / (graph.width as Double)
        val yScale = svgEl.clientHeight / (graph.height as Double)

        val transform = d3.zoomIdentity.scale(min(xScale, yScale))
        d3.select(gEl).transition().duration(750)
            .call(zoom.transform, transform)
    }

    onMount(dagGraph, zoomToFit) {
        val gEl = gRef.current!!
        if (dagGraph != null) {
            // Set up an SVG group so that we can translate the final graph.
            val d3G = d3.select(gEl)

            dagGraph.graph().transition = { selection: dynamic ->
                selection.transition().duration(500)
            }

            // Run the renderer. This is what draws the final graph.
            dagreD3.render()(d3G, dagGraph)

            globalLaunch {
                zoomToFit()
            }
        }
    }


    svg(+style.dagSvg) {
        ref = svgRef
        g {
            ref = gRef
        }
    }
}

external interface DagProps : Props {
    var program: GlslProgram
}

fun RBuilder.dag(handler: RHandler<DagProps>) =
    child(DagView, handler = handler)