package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.slider.Location
import baaahs.ui.slider.sliderContext
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.PointerEvent
import react.dom.onPointerDown
import react.useContext

private val sliderRail = xComponent<Props>("SliderRail") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider
    val sliderContext = useContext(sliderContext)

    val handlePointerDown = callback(sliderContext.emitPointer) { e: PointerEvent<*> ->
        sliderContext.emitPointer(e, Location.Rail, null)
    }

    div(+styles.railBackground) {
        attrs.onPointerDown = handlePointerDown
    }

    div(+styles.railChannel) {}
}

fun RBuilder.sliderRail(handler: RHandler<Props>) =
    child(sliderRail, handler = handler)