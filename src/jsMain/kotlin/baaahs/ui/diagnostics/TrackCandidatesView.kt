package baaahs.ui.diagnostics

import baaahs.app.ui.appContext
import baaahs.device.FixtureType
import baaahs.gl.patch.PortDiagram
import baaahs.show.live.OpenPatch
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.Overflow
import kotlinx.css.WhiteSpace
import kotlinx.css.overflow
import kotlinx.css.whiteSpace
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.useContext
import styled.inlineStyles

private val TrackCandidatesView = xComponent<TrackCandidatesProps>("TrackCandidates", isPure = true) { props ->
    val appContext = useContext(appContext)
    val diagnosticsStyles = appContext.allStyles.diagnosticsStyles

    val portDiagram = props.portDiagram

    div(+diagnosticsStyles.contentDiv) {
        table(+diagnosticsStyles.table) {
            tbody {
                portDiagram.candidates.forEach { (track, candidates) ->
                    tr {
                        th {
                            attrs.colSpan = "8"

                            header {
                                inlineStyles {
                                    whiteSpace = WhiteSpace.nowrap
                                    overflow = Overflow.scroll
                                }
                                +"Track: ${track.stream.id}:${track.contentType.id}"
                            }
                        }
                    }

                    candidates.sortedEntries.reversed().forEach { trackEntry ->
                        val patch = trackEntry.openPatch
                        tr {
                                td {
                                    attrs.colSpan = "8"

                                    h3 { +patch.shader.title }
                                }
                            }

                            tr {
                                th { +"Priority:" }
                                td { +"${trackEntry.priority}" }

                                th { +"Type Priority:" }
                                td { +"${trackEntry.typePriority}${if (patch.isFilter) " (isFilter)" else ""}" }

                                th { +"Level:" }
                                td { +"${trackEntry.level}" }

                                th { +"Title:" }
                                td { +patch.title.substring(0 until 4) }
                            }

                            tr {
                                th {
                                    attrs.colSpan = "8"
                                    +"Incoming Links:"
                                }
                            }
                            patch.incomingLinks.forEach { (id, link) ->
                                tr(+diagnosticsStyles.incomingLinkRow) {
                                    th { +id }
                                    td {
                                        attrs.colSpan = "7"

                                        when (link) {
                                            is OpenPatch.ConstLink -> {
                                                +"const: ${link.glsl} (${link.type} = ${link.type.glslLiteral})"
                                            }
                                            is OpenPatch.FeedLink -> {
                                                +"datasource: ${link.feed} (${link.deps})"
                                            }
                                            is OpenPatch.InjectedDataLink -> {
                                                +"injected data: ${link}"
                                            }
                                            is OpenPatch.ShaderOutLink -> {
                                                +"shader out: ${link}"
                                            }
                                            is OpenPatch.StreamLink -> {
                                                +"stream: ${link.stream.id}"
                                            }
                                            else -> {
                                                +"unknown link: $link"
                                            }
                                        }
                                    }
                                }
                            }
                        }
                }
            }
        }
    }
}

external interface TrackCandidatesProps : Props {
    var fixtureType: FixtureType
    var portDiagram: PortDiagram
}

fun RBuilder.trackCandidates(handler: RHandler<TrackCandidatesProps>) =
    child(TrackCandidatesView, handler = handler)