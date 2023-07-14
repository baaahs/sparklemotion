package baaahs.ui

import baaahs.app.ui.AppGlContext
import baaahs.app.ui.StyleConstants
import baaahs.app.ui.appContext
import baaahs.app.ui.appGlContext
import js.core.jso
import kotlinx.css.*
import react.PropsWithChildren
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.StyleSheet
import styled.inlineStyles
import web.html.HTMLElement

private val SharedGlContext = xComponent<SharedGlContextProps>("SharedGlContext") { props ->
    val appContext = useContext(appContext)
    val oldSharedGlContext = useContext(appGlContext).sharedGlContext

    if (oldSharedGlContext != null) {
        error("There's already a SharedGlContext!")
    }

    val useSharedContexts = appContext.uiSettings.useSharedContexts

    val canvasParentRef = ref<HTMLElement>()
    val appGlContext = memo(useSharedContexts) {
        jso<AppGlContext> {
            this.sharedGlContext = if (useSharedContexts) baaahs.gl.SharedGlContext() else null
        }
    }

    onMount(useSharedContexts) {
        val canvasParent = canvasParentRef.current
        if (useSharedContexts && canvasParent != null) {
            val sharedGlContext = appGlContext.sharedGlContext!!

            val canvas = sharedGlContext.canvas
            canvas.classList.add(SharedGlContextStyles.canvas.name)
            if (props.inFront == true) {
                canvasParent.appendChild(canvas)
            } else {
                canvasParent.insertBefore(canvas, canvasParent.firstChild)
            }

            withCleanup {
                canvasParent.removeChild(canvas)
            }
        }
    }

    if (useSharedContexts) {
        div(+SharedGlContextStyles.container) {
            ref = canvasParentRef
            props.inlineStyles?.let { styler ->
                inlineStyles {
                    with (styler) { invoke() }
                }
            }

            baaahs.app.ui.appGlContext.Provider {
                attrs.value = appGlContext

                props.children()
            }
        }
    } else {
        props.children()
    }
}

fun interface StyleElement {
    fun StyledElement.invoke()
}

external interface SharedGlContextProps : PropsWithChildren {
    var inFront: Boolean?
    var inlineStyles: StyleElement?
}

fun RBuilder.sharedGlContext(handler: RHandler<SharedGlContextProps>) =
    child(SharedGlContext, handler = handler)

private object SharedGlContextStyles : StyleSheet("ui-SharedGlContext", isStatic = true) {
    val container by css {
        minHeight = 0.px
    }

    val canvas by css(container) {
        position = Position.absolute
        top = 0.px
        left = 0.px
        width = 100.pct
        height = 100.pct
        pointerEvents = PointerEvents.none
        zIndex = StyleConstants.Layers.sharedGlCanvas
    }
}