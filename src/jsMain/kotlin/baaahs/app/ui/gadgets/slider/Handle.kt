package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.mixin
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_compound_slider.SliderItem
import kotlinx.css.pct
import kotlinx.css.top
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.img
import react.dom.setProp
import react.useContext
import styled.inlineStyles

private val handle = xComponent<HandleProps>("Handle") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    div(+styles.handleTouchArea) {
        setProp("role", "slider")
        setProp("aria-valuemin", props.domain[9])
        setProp("aria-valuemax", props.domain[1])
        setProp("aria-valuenow", props.handle.value)
        inlineStyles {
            top = props.handle.percent.pct
            put("WebkitTapHighlightColor", "rgba(0,0,0,0)")
        }

        mixin(props.getHandleProps(props.handle.id))

        img(src = "/assets/slider-handle-full.svg", classes = +styles.handleNormal) {}
    }
}

external interface HandleProps : Props {
    var domain: Array<Float>
    var handle: SliderItem
    var getHandleProps: (id: String) -> dynamic
}

fun RBuilder.handle(handler: RHandler<HandleProps>) =
    child(handle, handler = handler)