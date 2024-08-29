package baaahs.app.ui.controls

import baaahs.GadgetListener
import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.slider.resetButton
import baaahs.app.ui.gadgets.slider.slider
import baaahs.client.EventManager.Companion.visibleSliders
import baaahs.control.OpenSliderControl
import baaahs.gadgets.Slider
import baaahs.show.live.ControlProps
import baaahs.ui.*
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
import kotlin.time.Duration.Companion.milliseconds

private val SliderControlView = xComponent<SliderControlProps>("SliderControl") { props ->
    val appContext = useContext(appContext)
    val controlsStyles = appContext.allStyles.controls

    val slider = props.slider

    var beatLinked by state { slider.beatLinked }

    val containerRef = ref<HTMLElement>()
    val sliderControl = props.sliderControl
    val openShow = props.controlProps?.openShow
    openShow?.activePatchSetMonitor?.addObserver {
        forceRender()
    }
    val controlsInfo = openShow?.getSnapshot()?.controlsInfo

    val sliderNumber = memo(controlsInfo, sliderControl) {
        val visibleSliders = controlsInfo?.visibleSliders() ?: emptyList()
        visibleSliders.indexOf(sliderControl)
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
        }

        if (sliderNumber > -1) {
            div(+Styles.deviceChannelNumber) { +sliderNumber.toString() }
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

val DEVICE_CONTROL_INDICATOR_PERIOD = 100.milliseconds

external interface SliderControlProps : Props {
    var controlProps: ControlProps?
    var slider: Slider
    var sliderControl: OpenSliderControl?
}

fun RBuilder.sliderControl(handler: RHandler<SliderControlProps>) =
    child(SliderControlView, handler = handler)