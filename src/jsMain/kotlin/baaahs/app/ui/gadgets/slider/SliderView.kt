package baaahs.app.ui.gadgets.slider

import baaahs.GadgetListener
import baaahs.app.ui.appContext
import baaahs.gadgets.Slider
import baaahs.ui.disableScroll
import baaahs.ui.enableScroll
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.lodash.throttle
import external.react_compound_slider.*
import kotlinx.css.bottom
import kotlinx.css.pct
import react.*
import react.dom.div
import styled.inlineStyles
import kotlin.math.floor
import kotlin.math.roundToInt

private const val positionHandle = "position"
private const val floorHandle = "floor"

private val slider = xComponent<SliderProps>("Slider") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    val slider = props.slider

    val listener: GadgetListener = {
        forceRender()
    }
    onMount(slider) {
        slider.listen(listener)
        withCleanup { slider.unlisten(listener) }
    }

    val handleChange by handler(slider) { values: Map<String, Double> ->
        // BetterSlider will deal with moving the handles, so we don't want to trigger a full re-render.
        slider.withoutTriggering(listener) {
            val newPosition = values[positionHandle] ?: error("No position.")
            slider.position = newPosition.toFloat()

            val newFloorPosition = values[floorHandle]
            if (newFloorPosition != null) {
                slider.floor = newFloorPosition.toFloat()
            }
        }
    }

    val handleUpdate = throttle({ values: Map<String, Double> ->
        handleChange(values)
    }, wait = 10)

    val minValue = slider.minValue
    val maxValue = slider.maxValue
    val domain = memo(minValue, maxValue) {
        minValue.toDouble()..maxValue.toDouble()
    }

    val showSecondSlider = slider.beatLinked

    div(+styles.wrapper) {
//        label(+styles.label) {
//            setProp("htmlFor", "range-slider")
//            +props.title
//        }

        betterSlider {
            attrs.className = +styles.slider
            attrs.vertical = true
            attrs.reversed = true
            attrs.step = slider.stepValue?.toDouble()
            attrs.domain = domain
            attrs.onSlideStart = disableScroll.asDynamic()
            attrs.onSlideEnd = enableScroll.asDynamic()
            attrs.onUpdate = handleUpdate
            attrs.onChange = handleChange
            attrs.values = buildMap {
                put(positionHandle, slider.position.toDouble())
                if (slider.beatLinked) { put(floorHandle, slider.floor.toDouble()) }
            }

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
                                when (handle.id) {
                                    positionHandle -> {
                                        handle {
                                            key = handle.id
                                            attrs.domain = domain
                                            attrs.handle = handle
                                            attrs.getHandleProps = handlesObject.getHandleProps
                                        }
                                    }
                                    floorHandle -> {
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

            val ticksScale = if (maxValue <= 2) 100f else 1f
            betterTicks {
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

                            val scale = ticksObject.scale
                            if (scale != null) {
                                div(+styles.defaultTickMark) {
                                    inlineStyles {
                                        bottom = (100 - scale.getValue(slider.initialValue.toDouble())).pct
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
    var slider: Slider
}

fun RBuilder.slider(handler: RHandler<SliderProps>) =
    child(slider, handler = handler)
