package baaahs.ui.diagnostics

import baaahs.app.ui.appContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslProgramImpl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.dagre_d3.d3
import external.dagre_d3.dagreD3
import kotlinx.css.UserSelect
import kotlinx.css.userSelect
import org.w3c.dom.svg.SVGGElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.pre
import react.dom.svg
import react.dom.svg.ReactSVG.g
import react.useContext
import styled.inlineStyles

private val DagView = xComponent<DagProps>("Dag", isPure = true) { props ->
    val appContext = useContext(appContext)
    val style = appContext.allStyles.uiComponents

    val svgRef = ref<SVGGElement>()
    val gRef = ref<SVGGElement>()

    val program = props.program as? GlslProgramImpl
    val linkedProgram = program?.linkedProgram

    onMount(gRef.current) {
        gRef.current?.let { gEl ->
            val svg = d3.select(svgRef.current!!)
            val g = d3.select(gEl)

            val zoom = d3.zoom().on("zoom") {
                g.attr("transform", d3.event.transform);
            }
            svg.call(zoom)
        }
    }

    onMount(gRef.current, linkedProgram) {
        gRef.current?.let { gEl ->
//            gEl.clear()

            if (linkedProgram != null) {
                val dag = Dag()
                dag.visit(linkedProgram)
                val g = dag.graph

                // Set up an SVG group so that we can translate the final graph.
//            val svgEl = select(gEl)
                val svgGroup = d3.select(gEl)

                g.graph().transition = { selection: dynamic ->
                    selection.transition().duration(500)
                }

                // Run the renderer. This is what draws the final graph.
                dagreD3.render()(svgGroup, g)

                val graphHuh = g.graph()
                val svgEl = svgRef.current!!
                svgEl.style.width = graphHuh.width + "px"
                svgEl.style.height = graphHuh.height + "px"

                // Center the graph
                val xCenterOffset = (svgEl.clientWidth - g.asDynamic().graph().width as Int) / 2;
                gEl.style.transform = "translate($xCenterOffset, 20)"
                svgEl.style.height = "${g.asDynamic().graph().height as Int + 40}px"
            }
        }
    }

    if (linkedProgram != null) {
        pre {
            inlineStyles {
                userSelect = UserSelect.all
            }

//            +DotDag(linkedProgram.rootNode).text
        }

//        describe(linkedProgram.rootNode)

        svg(+style.dagSvg) {
            ref = svgRef
            g {
                ref = gRef
            }
        }
    }
}

external interface DagProps : Props {
    var program: GlslProgram
}

fun RBuilder.dag(handler: RHandler<DagProps>) =
    child(DagView, handler = handler)