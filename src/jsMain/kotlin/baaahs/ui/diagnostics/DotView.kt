package baaahs.ui.diagnostics

import baaahs.app.ui.appContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslProgramImpl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
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

private val DotView = xComponent<DotProps>("Dot", isPure = true) { props ->
    val appContext = useContext(appContext)
    val style = appContext.allStyles.uiComponents

    val svgRef = ref<SVGGElement>()
    val gRef = ref<SVGGElement>()

    val program = props.program as? GlslProgramImpl
    val linkedProgram = program?.linkedProgram

    if (linkedProgram != null) {
        pre {
            inlineStyles {
                userSelect = UserSelect.all
            }

            +Dag().apply { visit(linkedProgram) }.text
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

external interface DotProps : Props {
    var dot: Boolean?
    var program: GlslProgram
}

fun RBuilder.dot(handler: RHandler<DotProps>) =
    child(DotView, handler = handler)