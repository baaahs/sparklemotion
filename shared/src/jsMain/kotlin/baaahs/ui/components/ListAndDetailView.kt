package baaahs.ui.components

import baaahs.app.ui.appContext
import baaahs.mapper.styleIf
import baaahs.ui.and
import baaahs.ui.components.ListAndDetail.Orientation.*
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
    val isNarrowScreen = useMediaQuery(theme.breakpoints.down(Breakpoint.md))

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

    val orientation = props.orientation ?: when {
        isNarrowScreen -> zStacked
        else -> xStacked
    }

    when (orientation) {
        zStacked -> {
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
        }
        xStacked, yStacked -> {
            div(+styles.containerLarge and
                    styleIf(orientation == xStacked, styles.containerXStacked, styles.containerYStacked)
            ) {
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
}

external interface ListAndDetailProps<T> : Props {
    var listHeader: ReactElement<*>
    var listHeaderText: ReactNode
    var listRenderer: ListAndDetail.ListRenderer
    var selection: T?
//    var showDetail: Boolean?
    var detailHeader: String?
    var detailRenderer: ListAndDetail.DetailRenderer<T>
    var orientation: ListAndDetail.Orientation?
    var onDeselect: (() -> Unit)?
}

object ListAndDetail {
    fun interface ListRenderer {
        fun RBuilder.render()
    }

    fun interface DetailRenderer<T> {
        fun RBuilder.render(item: T)
    }

    enum class Orientation {
        xStacked, yStacked, zStacked
    }
}

fun <T> RBuilder.listAndDetail(handler: RHandler<ListAndDetailProps<T>>) =
    child(ListAndDetailView, handler = handler)