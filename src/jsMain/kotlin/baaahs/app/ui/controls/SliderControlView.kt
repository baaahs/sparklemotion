package baaahs.app.ui.controls

import baaahs.GadgetListener
import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.slider.resetButton
import baaahs.app.ui.gadgets.slider.slider
import baaahs.control.OpenSliderControl
import baaahs.gadgets.Slider
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import mui.icons.material.MusicNote
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.Size
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val SliderControlView = xComponent<SliderControlProps>("SliderControl") { props ->
    val appContext = useContext(appContext)
    val controlsStyles = appContext.allStyles.controls

    val slider = props.slider

    var beatLinked by state { slider.beatLinked }

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

    div(props.sliderControl?.inUseStyle?.let { +it }) {
        slider {
            attrs.slider = slider
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

external interface SliderControlProps : Props {
    var slider: Slider
    var sliderControl: OpenSliderControl?
}

fun RBuilder.sliderControl(handler: RHandler<SliderControlProps>) =
    child(SliderControlView, handler = handler)