package baaahs.ui.diagnostics

import baaahs.app.ui.appContext
import baaahs.device.FixtureType
import baaahs.gl.patch.LinkNode
import baaahs.gl.patch.LinkedProgram
import baaahs.gl.patch.ProgramNode
import baaahs.show.live.LinkedPatch
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.useContext

private val LinkedProgramView = xComponent<LinkedProgramProps>("LinkedProgram") { props ->
    val appContext = useContext(appContext)
    val diagnosticsStyles = appContext.allStyles.diagnosticsStyles

    div(+diagnosticsStyles.contentDiv) {
        table(+diagnosticsStyles.table) {
            tbody {
                props.linkedProgram.linkNodes
                    .toMutableMap().entries
                    .sortedWith(
                        compareBy<MutableMap.MutableEntry<ProgramNode, LinkNode>> { (_, linkNode) -> linkNode.index }
                            .thenBy { (_, linkNode) -> linkNode.modIndex ?: Int.MAX_VALUE }
                    )
                    .forEach { (programNode, linkNode) ->
                        if (programNode !is LinkedPatch) return@forEach

                        tr {
                            th {
                                attrs.colSpan = "6"
                                header { +programNode.title }
                            }
                        }
                        tr {
                            th { +"Program Index:" }
                            td { +linkNode.fullIndex }

                            th { +"ID:" }
                            td { +linkNode.id }

                            th { +"Max Observed Depth:" }
                            td { +"${linkNode.maxObservedDepth}" }
                        }

                        tr {
                            th {
                                attrs.colSpan = "6"
                                +"Incoming Links:"
                            }
                        }

                        programNode.incomingLinks.forEach { (linkId, linkedNode) ->
                            tr(+diagnosticsStyles.incomingLinkRow) {
                                th { +linkId }
                                td {
                                    attrs.colSpan = "5"
                                    +linkedNode.title
                                }
                            }
                        }
                    }
            }
        }
    }
}

external interface LinkedProgramProps : Props {
    var fixtureType: FixtureType
    var linkedProgram: LinkedProgram
}

fun RBuilder.linkedProgram(handler: RHandler<LinkedProgramProps>) =
    child(LinkedProgramView, handler = handler)