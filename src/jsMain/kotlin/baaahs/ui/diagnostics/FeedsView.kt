package baaahs.ui.diagnostics

import baaahs.app.ui.appContext
import baaahs.device.FixtureType
import baaahs.gl.glsl.GlslProgramImpl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.material.*
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.button
import react.dom.div
import react.dom.onClick
import react.useContext

private val FeedsView = xComponent<FeedsProps>("Feeds") { props ->
    val appContext = useContext(appContext)
    val diagnosticsStyles = appContext.allStyles.diagnosticsStyles

    button {
        attrs.onClick = { this@xComponent.forceRender() }
        +"Update"
    }

    div(+diagnosticsStyles.contentDiv) {
        Table {
            attrs.padding = TablePadding.none
            attrs.size = Size.small
            attrs.stickyHeader = true

            TableHead {
                TableRow {
                    TableCell { +"ID" }
                    TableCell { +"Feed" }
                    TableCell { +"Value" }
                }
            }

            TableBody {
                props.program.openFeeds
                    .sortedBy { it.id }
                    .forEach { feed ->
                        TableRow {
                            TableCell { +feed.id }
                            TableCell { +feed.dataSource.title }
                            TableCell {
                                feed.glslProgramSpy?.let {
                                    Table {
                                        attrs.padding = TablePadding.none
                                        attrs.size = Size.small

                                        TableBody {
                                            it.uniforms.forEach { (name, uniformSpy) ->
                                                TableRow {
                                                    TableCell {
                                                        attrs.variant = TableCellVariant.head
                                                        +name
                                                    }
                                                    TableCell {
                                                        +(uniformSpy?.value?.toString() ?: "none")
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
    }
}

external interface FeedsProps : Props {
    var fixtureType: FixtureType
    var program: GlslProgramImpl
}

fun RBuilder.feeds(handler: RHandler<FeedsProps>) =
    child(FeedsView, handler = handler)