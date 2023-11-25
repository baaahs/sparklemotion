package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.slider.RailProps
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.onPointerDown
import react.useContext

private val sliderRail = xComponent<SliderRailProps>("SliderRail") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    div(+styles.railBackground) {
        props.onPointerDown?.run { attrs.onPointerDown = this }
    }

    div(+styles.railChannel) {}
}

external interface SliderRailProps : Props, RailProps {
}

fun RBuilder.sliderRail(handler: RHandler<SliderRailProps>) =
    child(sliderRail, handler = handler)