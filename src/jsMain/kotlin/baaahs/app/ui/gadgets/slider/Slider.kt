package baaahs.app.ui.gadgets.slider

import baaahs.Gadget
import baaahs.GadgetListener
import baaahs.app.ui.appContext
import baaahs.gadgets.Slider
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_compound_slider.*
import external.throttle
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.label
import kotlin.math.floor

private val sliderStyle = js(
    "{\n" +
            "  position: 'relative',\n" +
            "  height: '200px',\n" +
            "  marginLeft: '45%',\n" +
            "  touchAction: 'none',\n" +
            "}"
)

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

    var sliderValue by state { props.gadget.value }

    val handleChangeFromServer = handler("change from server") { gadget: Gadget ->
        gadget as Slider
        sliderValue = gadget.value
    }

    val handleChange = handler("gadget change", props.gadget) { value: Array<Number> ->
        props.gadget.withoutTriggering(handleChangeFromServer) {
            props.gadget.value = value[0].toFloat()
        }
    }

    val handleUpdate = throttle({ value: Array<Number> ->
        handleChange(value)
    }, wait = 10)


    onMount {
        props.gadget.listen(handleChangeFromServer as GadgetListener)
        withCleanup {
            try {
                props.gadget.unlisten(handleChangeFromServer)
            } catch (e: Exception) {
                // TODO: Why is this happening? It causes the UI to disappear. :-(
                logger.warn(e) { "Failed to unlisten on ${props.gadget}" }
            }
        }
    }

    val domain = memo(props.gadget) {
        arrayOf(props.gadget.minValue, props.gadget.maxValue)
    }
    val stepValue = memo(props.gadget) {
        props.gadget.stepValue ?: 0.01f
    }

    div(+styles.wrapper) {
        label(+styles.label) {
            setProp("for", "range-slider")
            props.gadget.title
        }

        Slider {
            attrs.vertical = true
            attrs.reversed = props.reversed
            attrs.mode = 2
            attrs.step = stepValue
            attrs.domain = domain.asDynamic()
            attrs.rootStyle = sliderStyle
            attrs.onSlideStart = disableScroll.asDynamic()
            attrs.onSlideEnd = enableScroll.asDynamic()
            attrs.onUpdate = handleUpdate
            attrs.onChange = handleChange
            attrs.values = arrayOf(sliderValue)

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
                        handlesObject.handles.forEach { handle ->
                            handle {
                                key = handle.id
                                attrs.domain = domain
                                attrs.handle = handle
                                attrs.getHandleProps = handlesObject.getHandleProps
                            }
                        }

                    }
                }

            }

            Tracks {
                attrs.left = false
                attrs.right = false
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

            if (props.showTicks) {
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
    var gadget: Slider
    var reversed: Boolean
    var showTicks: Boolean

}

fun RBuilder.slider(handler: RHandler<SliderProps>) =
    child(slider, handler = handler)
