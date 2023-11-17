package baaahs.app.ui.controls

import baaahs.GadgetListener
import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.slider.slider
import baaahs.control.OpenSliderControl
import baaahs.gadgets.Slider
import baaahs.ui.icons.ResetIcon
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.Color
import kotlinx.css.backgroundColor
import materialui.icon
import mui.icons.material.MusicNote
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.onClick
import react.useContext
import styled.inlineStyles

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

    val handleReset by mouseEventHandler(slider) {
        slider.position = slider.initialValue
        slider.floor = slider.initialValue
        slider.beatLinked = false
    }

    div(props.sliderControl?.inUseStyle?.let { +it }) {
        slider {
            attrs.slider = slider
        }

        div(+Styles.beatLinkedSwitch) {
            attrs.onClick = handleToggleBeatLinked

            if (beatLinked) {
                inlineStyles {
                    backgroundColor = Color.orange
                }
            }
            icon(MusicNote)
        }

        div(+Styles.resetSwitch) {
            attrs.onClick = handleReset

            ResetIcon {}
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