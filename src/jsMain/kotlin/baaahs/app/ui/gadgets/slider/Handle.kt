package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.and
import baaahs.ui.mixin
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.pct
import kotlinx.css.top
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.setProp
import react.useContext
import styled.inlineStyles
import web.html.HTMLElement

private val handle = xComponent<HandleProps>("Handle") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    val touchAreaRef = ref<HTMLElement>()
    val handleRef = ref<HTMLElement>()
    observe(props.handle) {
        handleRef.current?.let { handle ->
            handle.style.top = props.handle.percent.pct.toString()
            handle.ariaValueNow = props.handle.value.toString()
        }
        touchAreaRef.current?.let { touchArea ->
            touchArea.style.top = props.handle.percent.pct.toString()
        }
    }

    div(+styles.handleTouchArea) {
        ref = touchAreaRef
        inlineStyles {
            top = props.handle.percent.pct
            put("WebkitTapHighlightColor", "rgba(0,0,0,0)")
        }

        mixin(props.getHandleProps(props.handle.id))
    }

    div(+styles.handleWrapper) {
        ref = handleRef
        setProp("role", "slider")
        setProp("aria-valuemin", props.domain.start)
        setProp("aria-valuemax", props.domain.endInclusive)
        setProp("aria-valuenow", props.handle.value)
        inlineStyles {
            top = props.handle.percent.pct
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

external interface HandleProps : Props, StandardEventHandlers {
    var domain: Range
    var handle: BetterSliderItem
    var getHandleProps: (id: String) -> HandleProps
}

fun RBuilder.handle(handler: RHandler<HandleProps>) =
    child(handle, handler = handler)