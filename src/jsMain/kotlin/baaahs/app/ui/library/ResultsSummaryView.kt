package baaahs.app.ui.library

import baaahs.app.ui.editor.ShaderStates
import baaahs.gl.preview.ShaderBuilder
import baaahs.ui.xComponent
import mui.material.Chip
import mui.material.ChipColor
import mui.material.ChipVariant
import react.Props
import react.RBuilder
import react.RHandler
import react.buildElement
import react.dom.div

private val ResultsSummaryView = xComponent<ResultsSummaryProps>("ResultsSummary") { props ->
    observe(props.shaderStates)
    val stateCount = props.shaderStates.stateCount()

    div {
        stateCount.entries.sortedBy { it.key.ordinal }.forEach { (state, count) ->
            Chip {
                attrs.color = when (state) {
                    ShaderBuilder.State.Success -> ChipColor.success
                    ShaderBuilder.State.Errors -> ChipColor.error
                    else -> ChipColor.default
                }
                attrs.variant = ChipVariant.outlined
                attrs.label = buildElement { +"$state: $count" }
            }
        }
    }
}

external interface ResultsSummaryProps : Props {
    var shaderStates: ShaderStates
}

fun RBuilder.resultsSummary(handler: RHandler<ResultsSummaryProps>) =
    child(ResultsSummaryView, handler = handler)