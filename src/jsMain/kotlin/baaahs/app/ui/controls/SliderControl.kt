package baaahs.app.ui.controls

import baaahs.app.ui.gadgets.slider.slider
import baaahs.control.OpenSliderControl
import baaahs.show.live.ControlProps
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.div

private val SliderControl = xComponent<SliderControlProps>("SliderControl") { props ->
    val sliderControl = props.sliderControl
    val title = sliderControl.title
//    val channel = props.sliderControl.channel

    observe(sliderControl.positionChannel, sliderControl.altPositionChannel)
//    val handlePositionChange = handler("handlePositionChange", channel) { newPosition: Float ->
//        channel.value = newPosition
//    }


    var position by state { sliderControl }

    val handlePositionChange = handler("handlePositionChange", sliderControl) { newPosition: Float ->
        sliderControl.positionChannel.value = newPosition
    }

    slider {
        attrs.title = props.sliderControl.title
        attrs.position = sliderControl.position
        attrs.contextPosition = sliderControl.altPosition
        attrs.contextPosition = null
        attrs.minValue = props.sliderControl.minValue
        attrs.maxValue = props.sliderControl.maxValue
        attrs.stepValue = props.sliderControl.stepValue
        attrs.reversed = true
        attrs.showTicks = true

        attrs.onChange = handlePositionChange
    }
    div(+Styles.dataSourceTitle) { +title }
}

external interface SliderControlProps : RProps {
    var controlProps: ControlProps
    var sliderControl: OpenSliderControl
}

fun RBuilder.sliderControl(handler: RHandler<SliderControlProps>) =
    child(SliderControl, handler = handler)