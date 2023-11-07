package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.disableScroll
import baaahs.ui.enableScroll
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.lodash.throttle
import external.react_compound_slider.*
import react.*
import react.dom.div
import kotlin.math.floor
import kotlin.math.roundToInt

private val slider = xComponent<SliderProps>("Slider") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    val priorValuesRef = ref(arrayOf(0.0, 0.0))
    val handleChange by handler(priorValuesRef, props.onPositionChange) { values: Array<Double> ->
        val priorValues = priorValuesRef.current!!
        val newPosition = values[0]
        if (newPosition != priorValues[0]) {
            priorValues[0] = newPosition
            props.onPositionChange(newPosition.toFloat())
        }

        if (values.size > 1) {
            val newFloorPosition = values[1]
            if (newFloorPosition != priorValues[1]) {
                priorValues[0] = newFloorPosition
                props.onFloorPositionChange?.invoke(newFloorPosition.toFloat())
            }
        }
    }

    val handleUpdate = throttle({ value: Array<Double> ->
        handleChange(value)
    }, wait = 10)

    val domain = memo(props.minValue, props.maxValue) {
        arrayOf(props.minValue, props.maxValue)
    }

    val showSecondSlider = props.floorPosition != null

    div(+styles.wrapper) {
//        label(+styles.label) {
//            setProp("htmlFor", "range-slider")
//            +props.title
//        }

        betterSlider {
            attrs.className = +styles.slider
            attrs.vertical = true
            attrs.reversed = props.reversed
            attrs.mode = 3
            attrs.step = (props.stepValue ?: ((props.maxValue - props.minValue) / 256)).toDouble()
            attrs.domain = domain.asDynamic()
            attrs.onSlideStart = disableScroll.asDynamic()
            attrs.onSlideEnd = enableScroll.asDynamic()
            attrs.onUpdate = handleUpdate
            attrs.onChange = handleChange
            attrs.values = listOfNotNull(props.position, props.floorPosition, props.contextPosition)
                .map { it.toDouble() }
                .toTypedArray()

            betterRail {
                attrs.children = { railObject ->
                    buildElement {
                        sliderRail {
                            attrs.getRailProps = railObject.getRailProps
                        }
                    }
                }
            }

            betterHandles {
                attrs.children = { handlesObject: HandlesObject ->
                    buildElement {
                        div(+styles.handles) {
                            handlesObject.handles.forEachIndexed { index, handle ->
                                when (index) {
                                    0 -> {
                                        handle {
                                            key = handle.id
                                            attrs.domain = domain
                                            attrs.handle = handle
                                            attrs.getHandleProps = handlesObject.getHandleProps
                                        }
                                    }
                                    1 -> {
                                        // Floor handle.
                                        altHandle {
                                            key = handle.id
                                            attrs.domain = domain
                                            attrs.handle = handle
                                            attrs.getHandleProps = handlesObject.getHandleProps
                                        }
                                    }
//                                    else -> {
//                                        // Non-draggable alt handle.
//                                        altHandle {
//                                            key = handle.id
//                                            attrs.domain = domain
//                                            attrs.handle = handle
//                                            attrs.getHandleProps = handlesObject.getHandleProps
//                                        }
//                                    }
                                }
                            }

                        }
                    }
                }
            }

            betterTracks {
                attrs.left = false
                attrs.right = !showSecondSlider
                attrs.children = { tracksObject ->
                    buildElement {
                        div(+styles.tracks) {
                            tracksObject.tracks.forEach { track ->
                                track {
                                    key = track.id
                                    attrs.source = track.source
                                    attrs.target = track.target
                                    attrs.getTrackProps = tracksObject.getTrackProps
                                }
                            }
                        }
                    }
                }
            }

            if (props.showTicks != false) {
                val ticksScale = props.ticksScale ?: 1f

                Ticks {
                    attrs.count = 10
                    attrs.children = { ticksObject ->
                        buildElement {
                            div(+styles.ticks) {
                                ticksObject.ticks.forEach { tick ->
                                    tick {
                                        key = tick.id
                                        attrs.tick = tick
                                        attrs.format = { item ->
                                            if (ticksScale != 1f) {
                                                floor(item.value.toFloat() * ticksScale).toString()
                                            } else {
                                                ((item.value.toFloat() * 100f).roundToInt().toFloat() / 100f).toString()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface SliderProps : Props {
    var title: String

    var position: Float

    /** If non-null, this is a second slider constrained to less than the main slider, representing a floor. */
    var floorPosition: Float?

    /** If non-null, this position is displayed as uneditable context. */
    var contextPosition: Float?

    var minValue: Float
    var maxValue: Float
    var stepValue: Float?

    var reversed: Boolean?
    var showTicks: Boolean?
    var ticksScale: Float?

    var onPositionChange: (Float) -> Unit
    var onFloorPositionChange: ((Float) -> Unit)?
}

fun RBuilder.slider(handler: RHandler<SliderProps>) =
    child(slider, handler = handler)
