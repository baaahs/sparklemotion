package baaahs.app.ui.controls

import baaahs.GadgetListener
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
    val title = sliderControl.slider.title
//    val channel = props.sliderControl.channel

//    observe(channel)
//    val handlePositionChange by eventHandler(channel) { newPosition: Float ->
//        channel.value = newPosition
//    }


    val slider = sliderControl.slider
    var position by state { slider.position }
    onMount(slider) {
        val listener: GadgetListener = { position = slider.position }
        slider.listen(listener)
        withCleanup { slider.unlisten(listener) }
    }

    val handlePositionChange by handler(slider) { newPosition: Float ->
        slider.position = newPosition
    }

    slider {
        attrs.title = props.sliderControl.slider.title
//        attrs.position = channel.value
        attrs.position = position
        attrs.contextPosition = null
        attrs.minValue = props.sliderControl.slider.minValue
        attrs.maxValue = props.sliderControl.slider.maxValue
        attrs.stepValue = props.sliderControl.slider.stepValue
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