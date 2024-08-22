package baaahs.app.ui.controls

import baaahs.GadgetListener
import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.slider.resetButton
import baaahs.app.ui.gadgets.slider.slider
import baaahs.client.EventManager
import baaahs.control.OpenSliderControl
import baaahs.gadgets.Slider
import baaahs.scale
import baaahs.show.live.ControlProps
import baaahs.ui.*
import baaahs.util.globalLaunch
import kotlinx.coroutines.delay
import mui.icons.material.MusicNote
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.Size
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import web.html.HTMLElement

private val SliderControlView = xComponent<SliderControlProps>("SliderControl") { props ->
    val appContext = useContext(appContext)
    val controlsStyles = appContext.allStyles.controls

    val slider = props.slider

    var beatLinked by state { slider.beatLinked }

    val containerRef = ref<HTMLElement>()
    val eventManager = appContext.webClient.eventManager
    val midiEventDeBumpifier = memo(slider) {
        MidiEventDeBumpifier(slider)
    }
    val sliderControl = props.sliderControl
    val openShow = props.controlProps?.openShow
    openShow?.activePatchSetMonitor?.addObserver {
        forceRender()
    }
    val controlsInfo = openShow?.getSnapshot()?.controlsInfo

    val sliderNumber = memo(controlsInfo, sliderControl) {
        val visibleSliders = controlsInfo
            ?.orderedOnScreenControls
            ?.filterIsInstance<OpenSliderControl>() ?: emptyList()
        visibleSliders.indexOf(sliderControl)
    }
    val deviceEventListener = memo(slider, eventManager, sliderNumber) {
        EventManager.DeviceEventListener { channel, value ->
            val scaledValue = slider.domain.scale(value)
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

    onMount(eventManager, deviceEventListener) {
        eventManager.addSliderListener(deviceEventListener)
        withCleanup { eventManager.removeSliderListener(deviceEventListener) }
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

        slider {
            attrs.slider = slider
            attrs.onSlideEnd = handleSliderUpdate
        }

        div(+Styles.deviceChannelNumber) { +sliderNumber.toString() }

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

external interface SliderControlProps : Props {
    var controlProps: ControlProps?
    var slider: Slider
    var sliderControl: OpenSliderControl?
}

fun RBuilder.sliderControl(handler: RHandler<SliderControlProps>) =
    child(SliderControlView, handler = handler)