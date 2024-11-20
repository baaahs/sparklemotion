package baaahs.ui.components

import baaahs.app.ui.appContext
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import materialui.icon
import mui.icons.material.Close
import mui.material.*
import mui.material.styles.Theme
import mui.material.styles.useTheme
import mui.system.Breakpoint
import mui.system.sx
import mui.system.useMediaQuery
import react.*
import react.dom.div
import react.dom.html.ReactHTML.header
import react.dom.span
import web.cssom.VerticalAlign
import web.cssom.px
import web.timers.setTimeout
import kotlin.time.Duration.Companion.milliseconds

private val ListAndDetailView = xComponent<ListAndDetailProps<*>>("ListAndDetail") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.listAndDetail
    val theme = useTheme<Theme>()
    val isSmallScreen = useMediaQuery(theme.breakpoints.down(Breakpoint.md))

    val handleDeselect by mouseEventHandler(props.onDeselect) {
        props.onDeselect?.invoke()
    }

    val speedMs = 300

    var cachedSelection by state<Any?> { props.selection }
    var cachedDetailHeader by state<String?> { props.detailHeader }
    memo(props.selection, props.detailHeader) {
        if (props.selection == null && cachedSelection != null) {
            setTimeout(speedMs.milliseconds) {
                cachedSelection = null
                cachedDetailHeader = props.detailHeader
            }
        } else {
            cachedSelection = props.selection
            cachedDetailHeader = props.detailHeader
        }
    }

    if (isSmallScreen) {
        Paper {
            attrs.className = -styles.listSheetSmall

            header {
                Fade {
                    attrs.`in` = props.selection == null
                    attrs.timeout = speedMs

                    span { child(props.listHeader) }
                }
            }

            with (props.listRenderer) { render() }

            Slide {
                attrs.direction = SlideDirection.left
                attrs.`in` = props.selection != null
                attrs.timeout = speedMs

                Paper {
                    attrs.className = -styles.detailSheetSmall

                    header {
                        attrs.className = -styles.detailHeader

                        Link {
                            attrs.onClick = handleDeselect
                            icon(mui.icons.material.ArrowBackIosNew) {
                                this.sx {
                                    verticalAlign = VerticalAlign.middle
                                    paddingBottom = 2.px
                                }
                            }

                            +props.listHeaderText
                        }

                        +(cachedDetailHeader ?: "")
                    }

                    div(+styles.detailContent) {
                        cachedSelection?.let { selection ->
                            with (props.detailRenderer) {
                                render(selection.unsafeCast<Nothing>())
                            }
                        }
                    }
                }
            }
        }
    } else {
        div(+styles.containerLarge) {
            Paper {
                attrs.className = -styles.listLarge

                header { span { child(props.listHeader) } }

                Paper { with (props.listRenderer) { render() } }
            }

            Paper {
                attrs.className = -styles.detailLarge

                if (props.selection != null) {
                    header {
                        attrs.className = -styles.detailHeader
                        +(cachedDetailHeader ?: "")

                        Button {
                            attrs.sx {
                                float = web.cssom.Float.right
                                marginTop = 5.px
                            }
                            attrs.onClick = handleDeselect
                            icon(Close)
                        }
                    }

                    div(+styles.detailContent) {
                        cachedSelection?.let { selection ->
                            with (props.detailRenderer) {
                                render(selection.unsafeCast<Nothing>())
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface ListAndDetailProps<T> : Props {
    var listHeader: ReactElement<*>
    var listHeaderText: ReactNode
    var listRenderer: ListRenderer
    var selection: T?
//    var showDetail: Boolean?
    var detailHeader: String?
    var detailRenderer: DetailRenderer<T>
    var onDeselect: (() -> Unit)?
}

fun interface ListRenderer {
    fun RBuilder.render()
}

fun interface DetailRenderer<T> {
    fun RBuilder.render(item: T)
}

fun <T> RBuilder.listAndDetail(handler: RHandler<ListAndDetailProps<T>>) =
    child(ListAndDetailView, handler = handler)