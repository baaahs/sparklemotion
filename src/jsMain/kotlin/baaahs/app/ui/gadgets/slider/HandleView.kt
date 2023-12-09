package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.and
import baaahs.ui.slider.*
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.pct
import kotlinx.css.top
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.KeyboardEvent
import react.dom.events.PointerEvent
import react.dom.onKeyDown
import react.dom.onPointerDown
import react.dom.setProp
import react.useContext
import styled.inlineStyles
import web.html.HTMLElement

val handle = xComponent<HandleProps>("Handle") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider
    val sliderContext = useContext(sliderContext)

    val touchAreaRef = ref<HTMLElement>()
    val handleRef = ref<HTMLElement>()
    val handle = props.handle
    observe(handle) {
        val value = handle.value
        val percent = sliderContext.scale.getValue(value)
        handleRef.current?.let { handle ->
            handle.style.top = percent.pct.toString()
            handle.ariaValueNow = value.toString()
        }
        touchAreaRef.current?.let { touchArea ->
            touchArea.style.top = percent.pct.toString()
        }
    }

    val handlePointerDown = callback() { e: PointerEvent<*> ->
        sliderContext.emitPointer(e, Location.Handle, props.handle.id)
    }
    val handleKeyDown = callback() { e: KeyboardEvent<*> ->
        sliderContext.emitKeyboard(e, props.handle.id)
    }

    val value = handle.value
    val percent = sliderContext.scale.getValue(value)
    div(+styles.handleTouchArea) {
        ref = touchAreaRef
        inlineStyles {
            top = percent.pct
            put("WebkitTapHighlightColor", "rgba(0,0,0,0)")
        }

        attrs.onPointerDown = sliderContext.getPointerDownHandlerFor(handle)
        attrs.onKeyDown = sliderContext.getKeyDownHandlerFor(handle)
    }

    div(+styles.handleWrapper) {
        ref = handleRef
        setProp("role", "slider")
        setProp("aria-valuemin", sliderContext.domain.start)
        setProp("aria-valuemax", sliderContext.domain.endInclusive)
        setProp("aria-valuenow", value)
        inlineStyles {
            top = percent.pct
        }

        div(+styles.handleNotch) {}
        div(+styles.handleNotch) {}
        div(+styles.handleNotch) {}
        div(+styles.handleNotch and styles.handleNotchMiddle) {}
        div(+styles.handleNotch and styles.handleNotchLower) {}
        div(+styles.handleNotch and styles.handleNotchLower) {}
        div(+styles.handleNotch and styles.handleNotchLower) {}
    }
}

external interface HandleProps : Props {
    var handle: Handle
}

fun RBuilder.handle(handler: RHandler<HandleProps>) =
    child(handle, handler = handler)