package baaahs.app.ui.controls

import baaahs.GadgetListener
import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.slider.slider
import baaahs.control.OpenSliderControl
import baaahs.show.live.ControlProps
import baaahs.ui.unaryPlus
import baaahs.ui.withEvent
import baaahs.ui.xComponent
import kotlinx.css.Color
import kotlinx.css.backgroundColor
import kotlinx.html.js.onClickFunction
import materialui.icon
import mui.icons.material.MusicNote
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.inlineStyles

private val SliderControlView = xComponent<SliderControlProps>("SliderControl") { props ->
    val appContext = useContext(appContext)
    val controlsStyles = appContext.allStyles.controls

    val sliderControl = props.sliderControl
    val title = sliderControl.slider.title
//    val channel = props.sliderControl.channel

//    observe(channel)
//    val handlePositionChange by eventHandler(channel) { newPosition: Float ->
//        channel.value = newPosition
//    }


    val slider = sliderControl.slider
    var position by state { slider.position }
    var floorPosition by state { slider.floor }
    var beatLinked by state { slider.beatLinked }
    onMount(slider) {
        val listener: GadgetListener = {
            position = slider.position
            floorPosition = slider.floor
            beatLinked = slider.beatLinked
        }
        slider.listen(listener)
        withCleanup { slider.unlisten(listener) }
    }

    val handlePositionChange by handler(slider) { newPosition: Float ->
        slider.position = newPosition
    }

    val handleFloorPositionChange by handler(slider) { newPosition: Float ->
        slider.floor = newPosition
    }

    val handleToggleBeatLinked by handler(slider) {
        slider.beatLinked = !slider.beatLinked
        slider.floor = slider.position
    }

    div(+sliderControl.inUseStyle) {
        slider {
            attrs.title = slider.title
//        attrs.position = channel.value
            attrs.position = position
            if (beatLinked) {
                attrs.floorPosition = floorPosition
            }
            attrs.contextPosition = null
            attrs.minValue = slider.minValue
            attrs.maxValue = slider.maxValue
            attrs.stepValue = slider.stepValue
            attrs.reversed = true
            attrs.showTicks = true
            if (slider.maxValue  <= 2) {
                attrs.ticksScale = 100f
            }

            attrs.onPositionChange = handlePositionChange
            attrs.onFloorPositionChange = handleFloorPositionChange
        }

        div(+Styles.beatLinkedSwitch) {
            attrs.onClickFunction = handleToggleBeatLinked.withEvent()

            if (beatLinked) {
                inlineStyles {
                    backgroundColor = Color.orange
                }
            }
            icon(MusicNote)
        }


        div(+controlsStyles.dataSourceTitle) { +title }
    }
}

external interface SliderControlProps : Props {
    var controlProps: ControlProps
    var sliderControl: OpenSliderControl
}

fun RBuilder.sliderControl(handler: RHandler<SliderControlProps>) =
    child(SliderControlView, handler = handler)