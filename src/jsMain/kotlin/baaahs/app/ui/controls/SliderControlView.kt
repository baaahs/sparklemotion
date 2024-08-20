package baaahs.app.ui.controls

import baaahs.GadgetListener
import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.slider.resetButton
import baaahs.app.ui.gadgets.slider.slider
import baaahs.client.EventManager
import baaahs.control.OpenSliderControl
import baaahs.document
import baaahs.gadgets.Slider
import baaahs.scale
import baaahs.ui.and
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.globalLaunch
import js.array.asList
import kotlinx.coroutines.delay
import mui.icons.material.MusicNote
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.Size
import react.*
import react.dom.div
import web.html.HTMLElement

private val SliderControlView = xComponent<SliderControlProps>("SliderControl") { props ->
    val appContext = useContext(appContext)
    val controlsStyles = appContext.allStyles.controls

    val slider = props.slider

    var beatLinked by state { slider.beatLinked }

    val containerRef = ref<HTMLElement>()
    val eventManager = appContext.webClient.eventManager
    val sliderNumber = ref<Int>(null)
    val midiEventDeBumpifier = memo(slider) {
        MidiEventDeBumpifier(slider)
    }
    val sliderListener = memo(slider, eventManager) {
        EventManager.SliderListener { channel, value ->
            val scaledValue = slider.domain.scale(value)
            sliderNumber.current?.let { sliderNumber ->
                if (channel == sliderNumber) {
                    midiEventDeBumpifier.maybeApplyChange(scaledValue)
                    containerRef.current?.let {
                        it.style.transition = "background-color 0.1s"
                        it.style.backgroundColor = "rgba(255, 127, 0, .5)"
                        globalLaunch {
                            delay(10)
                            it.style.backgroundColor = "rgba(255, 127, 0, 0)"
                        }
                    }
                }
            }
        }
    }
    console.log("I'm a slider!", slider.title)
    val sliderControl = props.sliderControl
    onMount(sliderListener) {
        if (sliderControl != null) {
            SliderFinder.hereIAm(sliderControl, sliderNumber)
        }
        eventManager.addSliderListener(sliderListener)
        withCleanup {
            eventManager.removeSliderListener(sliderListener)
            if (sliderControl != null) {
                SliderFinder.thereIGo(sliderControl)
            }
        }
    }
    val handleSliderUpdate by handler(midiEventDeBumpifier) {
        midiEventDeBumpifier.lastUpdate = null
    }

    onMount(slider) {
        val listener: GadgetListener = {
            beatLinked = slider.beatLinked
        }
        slider.listen(listener)
        withCleanup { slider.unlisten(listener) }
    }

    val handleToggleBeatLinked by mouseEventHandler(slider) {
        slider.beatLinked = !slider.beatLinked
        slider.floor = slider.position
    }

    div(+Styles.slider and sliderControl?.inUseStyle?.let { +it }) {
        ref = containerRef
        attrs[DATA_CONTROL_ID] = sliderControl?.id ?: "???"

        slider {
            attrs.slider = slider
            attrs.onSlideEnd = handleSliderUpdate
        }

        IconButton {
            attrs.className = -Styles.beatLinkedSwitch
            attrs.size = Size.small
            if (beatLinked) {
                attrs.color = IconButtonColor.primary
            }
            attrs.onClick = handleToggleBeatLinked

            MusicNote {}
        }

        resetButton {
            attrs.slider = slider
        }

        div(+controlsStyles.feedTitle) { +slider.title }
    }
}

class MidiEventDeBumpifier(
    private val slider: Slider
) {
    var lastUpdate: Float? = null

    fun maybeApplyChange(
        scaledValue: Float
    ) {
        val lastUpdate = this.lastUpdate
        when {
            lastUpdate == null -> { /* No-op. */ }
            lastUpdate == slider.position ->
                slider.position = scaledValue
            lastUpdate < slider.position && scaledValue >= slider.position ->
                slider.position = scaledValue
            lastUpdate > slider.position && scaledValue <= slider.position ->
                slider.position = scaledValue
            else -> { /* No-op. */ }
        }
        this.lastUpdate = scaledValue
    }
}

private const val DATA_CONTROL_ID: String = "data-control-id"

object SliderFinder {
    val knownSliders = mutableMapOf<String, MutableRefObject<Int>>()

    fun hereIAm(sliderControl: OpenSliderControl, sliderNumber: MutableRefObject<Int>) {
        knownSliders[sliderControl.id] = sliderNumber
        changed()
    }

    fun thereIGo(sliderControl: OpenSliderControl) {
        knownSliders.remove(sliderControl.id)
        changed()
    }

    fun changed() {
        document.getElementsByClassName(+Styles.slider).asList().forEachIndexed { index, element ->
            val controlId = element.attributes.getNamedItem(DATA_CONTROL_ID)?.value
            knownSliders[controlId]?.current = index
            console.log("I'm a slider", controlId, "and my number is", index)
        }
    }

    fun findSliderNumber(sliderControl: OpenSliderControl): Int? {
        return knownSliders[sliderControl.id]?.current
    }

}

external interface SliderControlProps : Props {
    var slider: Slider
    var sliderControl: OpenSliderControl?
}

fun RBuilder.sliderControl(handler: RHandler<SliderControlProps>) =
    child(SliderControlView, handler = handler)