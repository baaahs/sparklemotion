package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.gadgets.Slider
import baaahs.gadgets.toDoubles
import baaahs.ui.disableScroll
import baaahs.ui.enableScroll
import baaahs.ui.slider.Handle
import baaahs.ui.slider.ticks
import baaahs.ui.slider.tracks
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.*
import react.dom.div
import kotlin.math.floor
import kotlin.math.roundToInt
import baaahs.ui.slider.slider as baaahsSlider

private const val positionHandle = "position"
private const val floorHandle = "floor"

private val slider = xComponent<SliderProps>("Slider") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    val slider = props.slider
    val maxValue = slider.maxValue
    val domain = slider.domain.toDoubles()

    var isBeatLinked by state { slider.beatLinked }

    val positionHandle = memo(slider) {
        Handle(positionHandle, slider.position.toDouble()) { value ->
            slider.position = value.toFloat()
        }
    }

    val floorHandle = memo(slider) {
        Handle(floorHandle, slider.floor.toDouble()) { value ->
            slider.floor =  value.toFloat()
        }
    }

    observe(slider) {
        positionHandle.update(slider.position.toDouble())
        floorHandle.update(slider.floor.toDouble())
        isBeatLinked = slider.beatLinked
    }

    val handles = memo(positionHandle, floorHandle, isBeatLinked) {
        buildList {
            add(positionHandle)
            if (isBeatLinked) add(floorHandle)
        }
    }

    div(+styles.wrapper) {
        baaahsSlider {
            attrs.className = +styles.slider
            attrs.vertical = true
            attrs.reversed = true
            attrs.step = slider.stepValue?.toDouble()
            attrs.domain = domain
            attrs.handles = handles
            attrs.onSlideStart = disableScroll.asDynamic()
            attrs.onSlideEnd = enableScroll.asDynamic()

            sliderRail {}

            div(+styles.tracks) {
                tracks {
                    attrs.renderTrack = track.create() {
                        this.fillToZero = !isBeatLinked
                    }
                }
            }

            handle {
                attrs.handle = positionHandle
                attrs.variant = if (isBeatLinked) HandleVariant.MAX else HandleVariant.FULL
            }

            if (isBeatLinked) {
                handle {
                    attrs.handle = floorHandle
                    attrs.variant = HandleVariant.MIN
                }
            }

            val ticksScale = if (maxValue <= 2) 100f else 1f
            div(+styles.ticks) {
                ticks {
                    attrs.count = 10
                    attrs.tickComponent = tick.create() {
                        this.formatter = { value ->
                            if (ticksScale != 1f) {
                                floor(value * ticksScale).toString()
                            } else {
                                ((value * 100f).roundToInt().toFloat() / 100f).toString()
                            }
                        }
                    }
                    attrs.defaultValue = slider.initialValue.toDouble()
                    attrs.defaultTickComponent = tick.create() {
                        this.isDefaultValue = true
                    }
                }
            }
        }
    }
}

external interface SliderProps : Props {
    var slider: Slider
}

fun RBuilder.slider(handler: RHandler<SliderProps>) =
    child(slider, handler = handler)
