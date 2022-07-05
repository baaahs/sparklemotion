package baaahs.ui.diagnostics

import baaahs.app.ui.appContext
import baaahs.device.FixtureType
import baaahs.gl.patch.LinkedProgram
import baaahs.sim.ui.Styles
import baaahs.ui.and
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.code
import react.dom.div
import react.dom.pre
import react.useContext

private val DotView = xComponent<DotProps>("Dot", isPure = true) { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.uiComponents

    val linkedProgram = props.linkedProgram

    div(+Styles.contentDiv and styles.codeContainer) {
        pre(+styles.code) {
            Dag().apply { visit(props.fixtureType, linkedProgram) }.text
                .split("\n").forEach { line ->
                    code { +"    "; +line; +"\n" }
                }
        }
    }
}

external interface DotProps : Props {
    var fixtureType: FixtureType
    var linkedProgram: LinkedProgram
}

fun RBuilder.dot(handler: RHandler<DotProps>) =
    child(DotView, handler = handler)