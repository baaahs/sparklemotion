package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.mixin
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_compound_slider.GetRailProps
import react.*
import react.dom.div

private val sliderRail = xComponent<SliderRailProps>("SliderRail") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    div(+styles.railBackground) {
        mixin(props.getRailProps())
    }

    div(+styles.railChannel) {}
}


external interface SliderRailProps : RProps {
    var getRailProps: GetRailProps
}

fun RBuilder.sliderRail(handler: RHandler<SliderRailProps>) =
    child(sliderRail, handler = handler)