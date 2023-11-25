package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.pct
import kotlinx.css.top
import kotlinx.html.DIV
import kotlinx.html.HTMLTag
import kotlinx.html.unsafe
import react.RBuilder
import react.RHandler
import react.dom.*
import react.useContext
import styled.inlineStyles
import web.html.HTMLElement

val altHandle = xComponent<AltHandleProps>("AltHandle") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

//    val handleRef = ref<HTMLElement>()
//    observe(props.handle) {
//        handleRef.current?.let { handle ->
//            handle.style.top = props.handle.percent.pct.toString()
//            handle.ariaValueNow = props.handle.value.toString()
//        }
//    }

    div(+styles.altHandleWrapper) {
//        ref = handleRef
//        val rdomBuilder: RDOMBuilder<HTMLTag> = this
//        attrs.onPointerDown = props.onPointerDown

//        props.onPointerDown?.let { attrs.onPointerDown = it }
//        props.onKeyDown?.let { attrs.onKeyDown = it }
//        setProp("role", "slider")
//        setProp("aria-valuemin", props.domain.start)
//        setProp("aria-valuemax", props.domain.endInclusive)
//        setProp("aria-valuenow", props.handle.value)
//        inlineStyles {
//            top = props.handle.percent.pct
//        }

        svg(+styles.altHandleLeft) {
            attrs.unsafe {
                +"<path d=\"M 10 5 L 0 0 L 0 10 L 10 5 Z\"/>"
            }
        }
    }
}

external interface AltHandleProps : baaahs.ui.slider.HandleProps

fun RBuilder.altHandle(handler: RHandler<AltHandleProps>) =
    child(altHandle, handler = handler)