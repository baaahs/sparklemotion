package baaahs.ui.diagnostics

import baaahs.app.ui.appContext
import baaahs.device.FixtureType
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

private val GlslView = xComponent<GlslProps>("Glsl") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.uiComponents

    div(+Styles.contentDiv and styles.codeContainer) {
        pre(+styles.code) {
            props.source
                .let { glsl ->
                    glsl.split("\n").forEach { line ->
                        code { +line; +"\n" }
                    }
                }
        }
    }
}

external interface GlslProps : Props {
    var fixtureType: FixtureType
    var source: String
}

fun RBuilder.glsl(handler: RHandler<GlslProps>) =
    child(GlslView, handler = handler)