package baaahs.ui.diagnostics

import baaahs.app.ui.appContext
import baaahs.device.FixtureType
import baaahs.gl.patch.LinkedProgram
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import external.dagre_d3.d3
import external.dagre_d3.dagreD3
import mui.material.Checkbox
import mui.material.FormControl
import mui.material.FormControlLabel
import org.w3c.dom.svg.SVGGElement
import react.*
import react.dom.svg
import react.dom.svg.ReactSVG.g
import kotlin.math.min

private val DagView = xComponent<DagProps>("Dag", isPure = true) { props ->
    val appContext = useContext(appContext)
    val style = appContext.allStyles.uiComponents

    val svgRef = ref<SVGGElement>()
    val gRef = ref<SVGGElement>()

    var includePatchMods by state { false }
    val handleChangeIncludePatchMods by switchEventHandler { _, checked ->
        includePatchMods = checked
    }

    val dagGraph = memo(props.fixtureType, props.linkedProgram, includePatchMods) {
        Dag(includePatchMods).apply {
            visit(props.fixtureType, props.linkedProgram)
        }.graph
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
        val graph = dagGraph.graph()
        val xScale = svgEl.clientWidth / (graph.width as Double)
        val yScale = svgEl.clientHeight / (graph.height as Double)

        val transform = d3.zoomIdentity.scale(min(xScale, yScale))
        d3.select(gEl).transition().duration(750)
            .call(zoom.transform, transform)
    }

    onMount(dagGraph, zoomToFit) {
        val gEl = gRef.current!!
        // Set up an SVG group so that we can translate the final graph.
        val d3G = d3.select(gEl)

        val graph = dagGraph.graph()
        graph.marginx = 5
        graph.marginy = 5
        graph.transition = { selection: dynamic ->
            selection.transition().duration(500)
        }

        // Run the renderer. This is what draws the final graph.
        dagreD3.render()(d3G, dagGraph)

        globalLaunch {
            zoomToFit()
        }
    }

    FormControl {
        FormControlLabel {
            attrs.label = buildElement { +"Include Patch Mods" }
            attrs.control = buildElement {
                Checkbox {
                    attrs.checked = includePatchMods
                    attrs.onChange = handleChangeIncludePatchMods
                }
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
    var fixtureType: FixtureType
    var linkedProgram: LinkedProgram
}

fun RBuilder.dag(handler: RHandler<DagProps>) =
    child(DagView, handler = handler)