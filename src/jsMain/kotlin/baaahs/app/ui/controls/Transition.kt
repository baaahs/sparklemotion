package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.slider.slider
import baaahs.gadgets.Slider
import baaahs.plugin.core.OpenTransitionControl
import baaahs.show.live.ControlProps
import baaahs.ui.and
import baaahs.ui.on
import baaahs.ui.xComponent
import kotlinx.css.StyledElement
import kotlinx.html.js.onClickFunction
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.button.enums.ButtonStyle
import materialui.components.card.card
import materialui.components.paper.enums.PaperStyle
import materialui.lab.components.togglebutton.enums.ToggleButtonStyle
import materialui.lab.components.togglebutton.toggleButton
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import styled.inlineStyles

val Transition = xComponent<TransitionProps>("Transition") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.controls

    var holdEngaged by state { false }
    val handleHoldButtonClick = handler("handleHoldButtonClick") { _: Event ->
        holdEngaged = !holdEngaged
        logger.warn { "holdEngaged: $holdEngaged" }
    }

    val handleGoButtonClick = handler("handleGoButtonClick") { _: Event ->
        holdEngaged = false
        logger.warn { "holdEngaged: $holdEngaged" }
    }


    val rootEl = ref<Element>()
//    clientPreview.transition.rotate = props.transitionControl.rotate

    val fader = memo { Slider("Transition Fader", 0f, 0f, 1f) }
    val speed by state { "1s" }
    val shape by state { "ease" }
    val effect by state { "fade" }

//    val transition = clientPreview.transition
    onMount {
//        transition.container = rootEl.current as HTMLDivElement

        withCleanup {
//            transition.container = null
//            clientPreview.detach()
        }
    }

    card(styles.transitionCard on PaperStyle.root) {
        ref = rootEl

//        header { +"Transitions! \uD83D\uDE1E" }

        val holdButtonStyle =
            styles.transitionHoldButton.let {
                if (holdEngaged) it and styles.transitionHoldEngaged else listOf(it)
            }

        button(holdButtonStyle on ButtonStyle.root) {
//            inlineStyles { gridArea = "hold" }
            inlineStyles { put("gridArea", "hold") }
            attrs.color = ButtonColor.secondary
            attrs.onClickFunction = handleHoldButtonClick
            +"Hold"
        }

        button {
            inlineStyles { gridArea = "go" }
            attrs.disabled = !holdEngaged
            attrs.onClickFunction = handleGoButtonClick
            +"Go"
        }

        div {
            inlineStyles { gridArea = "fader" }

            slider {
                attrs.gadget = fader
                attrs.reversed = false
                attrs.showTicks = false
            }
        }

        div {
            inlineStyles { gridArea = "speed" }
            +"Speed: "
            toggleButton(styles.speedButton on ToggleButtonStyle.label) {
                attrs["selected"] = speed == ".25s"; +"¼s"
            }
            toggleButton(styles.speedButton on ToggleButtonStyle.label) {
                attrs["selected"] = speed == ".5s"; +"½s"
            }
            toggleButton(styles.speedButton on ToggleButtonStyle.label) {
                attrs["selected"] = speed == "1s"; +"1s"
            }
            toggleButton(styles.speedButton on ToggleButtonStyle.label) {
                attrs["selected"] = speed == "2s"; +"2s"
            }
        }

        div {
            inlineStyles { gridArea = "shape" }
            +"Shape: "
            toggleButton { attrs["selected"] = shape == "linear"; +"Linear" }
            toggleButton { attrs["selected"] = shape == "ease"; +"Ease" }
        }

        div {
            inlineStyles { gridArea = "effect" }
            +"Effect: "
            toggleButton { attrs["selected"] = effect == "fade"; +"Fade" }
            toggleButton { attrs["selected"] = effect == "dissolve"; +"Dissolve" }
        }
    }
}

var StyledElement.gridArea: String
    get() = error("Not supported.")
    set(value) = put("gridArea", value)

external interface TransitionProps : RProps {
    var controlProps: ControlProps
    var transitionControl: OpenTransitionControl
}

fun RBuilder.transition(handler: RHandler<TransitionProps>) =
    child(Transition, handler = handler)