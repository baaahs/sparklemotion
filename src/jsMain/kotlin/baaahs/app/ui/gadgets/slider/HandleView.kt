package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
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
import react.dom.img
import react.dom.onKeyDown
import react.dom.onPointerDown
import react.useContext
import styled.inlineStyles
import web.html.HTMLElement

private val handle = xComponent<HandleProps>("Handle") { props ->
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

        img(src = "/assets/slider-handle-full.svg", classes = +styles.handleNormal) {}
    }
}

external interface HandleProps : Props {
    var handle: Handle
}

fun RBuilder.handle(handler: RHandler<HandleProps>) =
    child(handle, handler = handler)