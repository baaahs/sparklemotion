package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.mixin
import baaahs.ui.slider.GetRailProps
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val sliderRail = xComponent<SliderRailProps>("SliderRail") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    div(+styles.railBackground) {
        mixin(props.getRailProps())
    }

    div(+styles.railChannel) {}
}


external interface SliderRailProps : Props {
    var getRailProps: GetRailProps
}

fun RBuilder.sliderRail(handler: RHandler<SliderRailProps>) =
    child(sliderRail, handler = handler)