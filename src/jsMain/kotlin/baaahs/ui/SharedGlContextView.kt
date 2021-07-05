package baaahs.ui

import baaahs.app.ui.AppGlContext
import baaahs.app.ui.appContext
import baaahs.app.ui.appGlContext
import kotlinext.js.jsObject
import kotlinx.css.*
import org.w3c.dom.HTMLElement
import react.*
import react.dom.div
import styled.StyleSheet

private val SharedGlContext = xComponent<SharedGlContextProps>("SharedGlContext") { props ->
    val appContext = useContext(appContext)
    val oldSharedGlContext = useContext(appGlContext).sharedGlContext

    if (oldSharedGlContext != null) {
        error("There's already a SharedGlContext!")
    }

    val useSharedContexts = appContext.uiSettings.useSharedContexts

    val canvasParentRef = ref<HTMLElement?> { null }
    val appGlContext = memo(useSharedContexts) {
        jsObject<AppGlContext> {
            this.sharedGlContext = if (useSharedContexts) baaahs.gl.SharedGlContext() else null
        }
    }

    onMount(useSharedContexts) {
        if (useSharedContexts) {
            val sharedGlContext = appGlContext.sharedGlContext!!

            val canvas = sharedGlContext.canvas
            canvas.classList.add(SharedGlContextStyles.canvas.name)
            canvasParentRef.current!!.let { parent ->
                parent.insertBefore(canvas, parent.firstChild)
            }

            withCleanup {
                canvasParentRef.current!!.removeChild(canvas)
            }
        }
    }

    if (useSharedContexts) {
        div(+SharedGlContextStyles.container) {
            ref = canvasParentRef

            baaahs.app.ui.appGlContext.Provider {
                attrs.value = appGlContext

                props.children()
            }
        }
    } else {
        props.children()
    }
}

external interface SharedGlContextProps : RProps {
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
    }
}