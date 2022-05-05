package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.*
import external.lodash.throttle
import external.react_compound_slider.*
import react.*
import react.dom.div
import kotlin.math.floor

private val slider = xComponent<SliderProps>("Slider") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    val priorValuesRef = ref<Array<Number>>(arrayOf(0f, 0f))
    val handleChange by handler(priorValuesRef, props.onPositionChange) { values: Array<Number> ->
        val priorValues = priorValuesRef.current!!
        val newPosition = values[0].toFloat()
        if (newPosition != priorValues[0]) {
            priorValues[0] = newPosition
            props.onPositionChange(newPosition)
        }

        if (values.size > 1) {
            val newFloorPosition = values[1].toFloat()
            if (newFloorPosition != priorValues[1]) {
                priorValues[0] = newFloorPosition
                props.onFloorPositionChange?.invoke(newFloorPosition)
            }
        }
    }

    val handleUpdate = throttle({ value: Array<Number> ->
        handleChange(value)
    }, wait = 10)

    val domain = memo(props.minValue, props.maxValue) {
        arrayOf(props.minValue, props.maxValue)
    }

    div(+styles.wrapper) {
//        label(+styles.label) {
//            setProp("htmlFor", "range-slider")
//            +props.title
//        }

        Slider {
            attrs.className = styles.slider.name
            attrs.vertical = true
            attrs.reversed = props.reversed
            attrs.mode = 3
            attrs.step = props.stepValue ?: ((props.maxValue - props.minValue) / 256)
            attrs.domain = domain.asDynamic()
            attrs.onSlideStart = disableScroll.asDynamic()
            attrs.onSlideEnd = enableScroll.asDynamic()
            attrs.onUpdate = handleUpdate
            attrs.onChange = handleChange
            attrs.values = listOfNotNull(props.position, props.floorPosition, props.contextPosition).toTypedArray()

            Rail {
                attrs.children = { railObject ->
                    buildElement {
                        sliderRail {
                            attrs.getRailProps = railObject.getRailProps
                        }
                    }
                }
            }

            Handles {
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
//                                else -> {
//                                    // Non-draggable alt handle.
//                                    altHandle {
//                                        key = handle.id
//                                        attrs.domain = domain
//                                        attrs.handle = handle
//                                        attrs.getHandleProps = handlesObject.getHandleProps
//                                    }
//                                }
                            }
                        }

                        }
                    }
                }
            }

            Tracks {
                attrs.left = false
                attrs.right = props.floorPosition == null
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
                                            floor(item.value.toFloat() * (props.ticksScale ?: 1f)).toString()
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
