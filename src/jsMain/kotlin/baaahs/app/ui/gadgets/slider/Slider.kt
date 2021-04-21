package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.name
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_compound_slider.*
import external.throttle
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.label
import kotlin.math.floor

private val preventDefault: (Event) -> Unit = { event -> event.preventDefault() }

private val disableScroll = {
    baaahs.document.body?.addEventListener("touchmove", preventDefault, js("{ passive: false }"))
}

private val enableScroll = {
    baaahs.document.body?.removeEventListener("touchmove", preventDefault)
}

private val slider = xComponent<SliderProps>("Slider") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    val handleChange = handler("gadget change", props.onChange) { value: Array<Number> ->
        val newValue = value[0].toFloat()
        props.onChange(newValue)
    }

    val handleUpdate = throttle({ value: Array<Number> ->
        handleChange(value)
    }, wait = 10)

    val domain = memo(props.minValue, props.maxValue) {
        arrayOf(props.minValue, props.maxValue)
    }
    val stepValue = memo { props.stepValue ?: 0.01f }

    div(+styles.wrapper) {
        label(+styles.label) {
            setProp("for", "range-slider")
            props.title
        }

        Slider {
            attrs.className = styles.slider.name
            attrs.vertical = true
            attrs.reversed = props.reversed
            attrs.mode = 1
            attrs.step = stepValue
            attrs.domain = domain.asDynamic()
            attrs.onSlideStart = disableScroll.asDynamic()
            attrs.onSlideEnd = enableScroll.asDynamic()
            attrs.onUpdate = handleUpdate
            attrs.onChange = handleChange
            attrs.values = listOfNotNull(props.position, props.contextPosition).toTypedArray()

            Rail {
                attrs.children = { railObject ->
                    sliderRail {
                        attrs.getRailProps = railObject.getRailProps
                    }
                }
            }

            Handles {
                attrs.children = { handlesObject: HandlesObject ->
                    div(+styles.handles) {
                        handlesObject.handles.forEachIndexed { index, handle ->
                            if (index == 0) {
                                handle {
                                    key = handle.id
                                    attrs.domain = domain
                                    attrs.handle = handle
                                    attrs.getHandleProps = handlesObject.getHandleProps
                                }
                            } else {
                                // Non-draggable alt handle.
                                altHandle {
                                    key = handle.id
                                    attrs.domain = domain
                                    attrs.handle = handle
                                    attrs.getHandleProps = handlesObject.getHandleProps
                                }
                            }
                        }

                    }
                }
            }

            Tracks {
                attrs.left = false
                attrs.right = true
                attrs.children = { tracksObject ->
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

            if (props.showTicks != false) {
                Ticks {
                    attrs.count = 10
                    attrs.children = { ticksObject ->
                        div(+styles.ticks) {
                            ticksObject.ticks.forEach { tick ->
                                tick {
                                    key = tick.id
                                    attrs.tick = tick
                                    attrs.format = { item ->
                                        floor(item.value.toFloat() * 100).toString()
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

external interface SliderProps : RProps {
    var title: String

    var position: Float
    /** If non-null, this position is displayed as uneditable context. */
    var contextPosition: Float?

    var minValue: Float
    var maxValue: Float
    var stepValue: Float?

    var reversed: Boolean?
    var showTicks: Boolean?

    var onChange: (Float) -> Unit
}

fun RBuilder.slider(handler: RHandler<SliderProps>) =
    child(slider, handler = handler)
