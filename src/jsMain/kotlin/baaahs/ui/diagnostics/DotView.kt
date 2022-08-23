package baaahs.ui.diagnostics

import baaahs.app.ui.appContext
import baaahs.device.FixtureType
import baaahs.gl.patch.LinkedProgram
import baaahs.sim.ui.Styles
import baaahs.ui.and
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.Checkbox
import mui.material.FormControl
import mui.material.FormControlLabel
import react.*
import react.dom.code
import react.dom.div
import react.dom.pre

private val DotView = xComponent<DotProps>("Dot", isPure = true) { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.uiComponents

    var includePatchMods by state { false }
    val handleChangeIncludePatchMods by switchEventHandler { _, checked ->
        includePatchMods = checked
    }

    val linkedProgram = props.linkedProgram

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

    div(+Styles.contentDiv and styles.codeContainer) {
        pre(+styles.code) {
            Dag(includePatchMods).apply { visit(props.fixtureType, linkedProgram) }.text
                .split("\n").forEach { line ->
                    code { +line; +"\n" }
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