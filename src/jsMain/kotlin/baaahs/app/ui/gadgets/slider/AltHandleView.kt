package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.slider.Handle
import baaahs.ui.slider.getKeyDownHandlerFor
import baaahs.ui.slider.getPointerDownHandlerFor
import baaahs.ui.slider.sliderContext
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.pct
import kotlinx.css.top
import kotlinx.html.unsafe
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.*
import react.useContext
import styled.inlineStyles
import web.html.HTMLElement

val altHandle = xComponent<AltHandleProps>("AltHandle") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider
    val sliderContext = useContext(sliderContext)

    val handleRef = ref<HTMLElement>()
    val handle = props.handle
    observe(props.handle) {
        val value = handle.value
        val percent = sliderContext.scale.getValue(value)
        handleRef.current?.let { handle ->
            handle.style.top = percent.pct.toString()
            handle.ariaValueNow = value.toString()
        }
    }

    val value = handle.value
    val percent = sliderContext.scale.getValue(value)
    div(+styles.altHandleWrapper) {
        ref = handleRef

        setProp("role", "slider")
        setProp("aria-valuemin", sliderContext.domain.start)
        setProp("aria-valuemax", sliderContext.domain.endInclusive)
        setProp("aria-valuenow", props.handle.value)
        inlineStyles {
            top = percent.pct
        }
        attrs.onPointerDown = sliderContext.getPointerDownHandlerFor(handle)
        attrs.onKeyDown = sliderContext.getKeyDownHandlerFor(handle)

        svg(+styles.altHandleLeft) {
            attrs.unsafe {
                +"<path d=\"M 10 5 L 0 0 L 0 10 L 10 5 Z\"/>"
            }
        }
    }
}

external interface AltHandleProps : Props {
    var handle: Handle
}

fun RBuilder.altHandle(handler: RHandler<AltHandleProps>) =
    child(altHandle, handler = handler)