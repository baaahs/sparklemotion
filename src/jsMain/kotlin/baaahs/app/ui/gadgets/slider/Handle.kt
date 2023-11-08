package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.and
import baaahs.ui.mixin
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_compound_slider.SliderItem
import external.react_compound_slider.StandardEventHandlers
import kotlinx.css.pct
import kotlinx.css.top
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.setProp
import react.useContext
import styled.inlineStyles

private val handle = xComponent<HandleProps>("Handle") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    div(+styles.handleTouchArea) {
        inlineStyles {
            top = props.handle.percent.pct
            put("WebkitTapHighlightColor", "rgba(0,0,0,0)")
        }

        mixin(props.getHandleProps(props.handle.id))
    }

    div(+styles.handleWrapper) {
        setProp("role", "slider")
        setProp("aria-valuemin", props.domain[0])
        setProp("aria-valuemax", props.domain[1])
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
    var domain: Array<Float>
    var handle: SliderItem
    var getHandleProps: (id: String) -> HandleProps
}

fun RBuilder.handle(handler: RHandler<HandleProps>) =
    child(handle, handler = handler)