package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.slider.Handle
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

private val sliderRail = xComponent<SliderRailProps>("SliderRail") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider
    val sliderContext = useContext(sliderContext)

    val handlePointerDown = callback(sliderContext.emitPointer) { e: PointerEvent<*> ->
        sliderContext.emitPointer(e, Location.Rail, props.handle?.id)
    }

    val railBackgroundClass = when (props.variant ?: HandleVariant.FULL) {
        HandleVariant.FULL -> styles.railBackground
        HandleVariant.MIN -> styles.railBackgroundMin
        HandleVariant.MAX -> styles.railBackgroundMax
    }

    div(+railBackgroundClass) {
        attrs.onPointerDown = handlePointerDown
    }
}

external interface SliderRailProps : Props {
    var handle: Handle?
    var variant: HandleVariant?
}

fun RBuilder.sliderBackground(handler: RHandler<SliderRailProps>) =
    child(sliderRail, handler = handler)