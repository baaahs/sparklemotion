package baaahs.app.ui.dev

import baaahs.gl.GlContext
import baaahs.ui.components.palette
import baaahs.ui.xComponent
import baaahs.window
import kotlinx.html.unsafe
import mui.material.*
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.span
import web.cssom.Margin
import web.cssom.em

private val DevGlContextsView = xComponent<DevGlContextsProps>("DevGlContexts") { props ->
    val allocatedContexts = observe(GlContext.allocatedContexts)
    val contextsByParent = allocatedContexts.groupBy { it.parentId }

    palette {
        attrs.title = "GL Contexts"
        attrs.initialWidth = window.innerWidth / 4
        attrs.initialHeight = window.innerHeight * 2 / 3
        attrs.onClose = props.onClose
        attrs.autoScroll = true

        Table {
            TableHead {
                TableCell { +"Type" }
                TableCell { +"Count" }
            }

            TableBody {
                allocatedContexts.groupBy { it.type }.forEach { (type, contexts) ->
                    TableRow {
                        TableCell {
                            attrs.align = TableCellAlign.right
                            attrs.variant = TableCellVariant.head
                            Typography { +"${type.simpleName}:" }
                        }
                        TableCell {
                            Typography { +contexts.size.toString() }
                        }
                    }
                }
            }
        }

        Divider {
            attrs.sx { margin = Margin(1.em, 0.em) }
        }

        Table {
            TableHead {
                TableCell { +"ID" }
                TableCell { +"Name" }
                TableCell { +"Type" }
            }
            TableBody {
                fun write(
                    parentId: Int?,
                    depth: Int = 0
                ) {
                    contextsByParent[parentId]?.forEach { childContext ->
                        TableRow {
                            TableCell {
                                if (depth > 0) {
                                    attrs.sx { paddingLeft = depth.em }
                                    span { attrs.unsafe { +"&rdsh; " } }
                                }

                                +childContext.id.toString()
                            }
                            TableCell { +childContext.name }
                            TableCell { +childContext.type.simpleName.toString() }
                        }
                        write(childContext.id, depth + 1)
                    }
                }

                write(null)
            }
        }
    }
}

external interface DevGlContextsProps : Props {
    var onClose: (() -> Unit)?
}

fun RBuilder.devGlContexts(handler: RHandler<DevGlContextsProps>) =
    child(DevGlContextsView, handler = handler)