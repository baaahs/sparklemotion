package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.gadgets.Slider
import baaahs.gadgets.toDoubles
import baaahs.ui.disableScroll
import baaahs.ui.enableScroll
import baaahs.ui.slider.*
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.lodash.throttle
import kotlinx.css.bottom
import kotlinx.css.pct
import react.*
import react.dom.div
import styled.inlineStyles
import kotlin.math.floor
import kotlin.math.roundToInt
import baaahs.ui.slider.slider as baaahsSlider

private const val positionHandle = "position"
private const val floorHandle = "floor"

private val slider = xComponent<SliderProps>("Slider") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    val slider = props.slider
    var isBeatLinked by state { slider.beatLinked }
    observe(slider) { isBeatLinked = slider.beatLinked }

    val handleChange by handler(slider) { values: Map<String, Double> ->
        val newPosition = values[positionHandle] ?: error("No position.")
        slider.position = newPosition.toFloat()

        val newFloorPosition = values[floorHandle]
        if (newFloorPosition != null) {
            slider.floor = newFloorPosition.toFloat()
        }
    }

    val handleUpdate = throttle({ values: Map<String, Double> ->
        handleChange(values)
    }, wait = 10)

    val maxValue = slider.maxValue
    val domain = slider.domain.toDoubles()

    val positionHandle = memo(slider) {
        ExtHandle(
            positionHandle, slider.position.toDouble(),
            component = handle.create()
        )
    }
    observe(positionHandle) { slider.position = positionHandle.value.toFloat() }

    val floorHandle = memo(slider) {
        ExtHandle(
            floorHandle, slider.floor.toDouble(),
            component = altHandle.create()
        )
    }
    observe(floorHandle) { slider.floor = floorHandle.value.toFloat() }

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
            attrs.onSlideStart = disableScroll.asDynamic()
            attrs.onSlideEnd = enableScroll.asDynamic()
            attrs.onUpdate = handleUpdate
            attrs.onChange = handleChange
            attrs.handles = handles

            attrs.renderRail = { railProps ->
                buildElement {
                    sliderRail {
                        attrs.onPointerDown = railProps.onPointerDown
                    }
                }
            }

            attrs.renderTrack = { trackProps ->
                buildElement {
                    track {
                        key = trackProps.id
                        attrs.fromHandle = trackProps.fromHandle
                        attrs.toHandle = trackProps.toHandle
                    }
                }
            }
            attrs.trackComponent = track.create()

            attrs.tickComponent = tick.create()
            attrs.tickCount = 10
            val ticksScale = if (maxValue <= 2) 100f else 1f
            attrs.tickFormatter = { value: Double ->
                if (ticksScale != 1f) {
                    floor(value * ticksScale).toString()
                } else {
                    ((value * 100f).roundToInt().toFloat() / 100f).toString()
                }
            }

//            div(+styles.defaultTickMark) {
//                inlineStyles {
//                    bottom = (100 - ticksScale.getValue(slider.initialValue.toDouble())).pct
//                }
//            }
        }
    }
}

external interface SliderProps : Props {
    var slider: Slider
}

fun RBuilder.slider(handler: RHandler<SliderProps>) =
    child(slider, handler = handler)
