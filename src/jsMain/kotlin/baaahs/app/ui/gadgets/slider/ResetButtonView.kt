package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.controls.Styles
import baaahs.gadgets.Slider
import baaahs.ui.icons.ResetIcon
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import mui.material.IconButton
import mui.material.Size
import react.Props
import react.RBuilder
import react.RHandler

private val ResetButtonView = xComponent<ResetButtonProps>("ResetButton") { props ->
    val isDefaults = props.slider.isDefaults()

    val slider = observe(props.slider, isDefaults) {
        if (isDefaults != props.slider.isDefaults())
            forceRender()
    }

    val handleReset by mouseEventHandler(slider) {
        slider.position = slider.initialValue
        slider.beatLinked = false
    }

    IconButton {
        attrs.className = -Styles.resetSwitch
        attrs.size = Size.small

        if (isDefaults) {
            attrs.disabled = true
        }

        attrs.onClick = handleReset

        ResetIcon {}
    }
}

private fun Slider.isDefaults() =
    position == initialValue && !beatLinked

external interface ResetButtonProps : Props {
    var slider: Slider
}

fun RBuilder.resetButton(handler: RHandler<ResetButtonProps>) =
    child(ResetButtonView, handler = handler)