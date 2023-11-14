package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.slider.slider
import baaahs.gadgets.Slider
import baaahs.plugin.core.OpenTransitionControl
import baaahs.show.live.ControlProps
import baaahs.ui.and
import baaahs.ui.unaryMinus
import baaahs.ui.xComponent
import js.core.jso
import kotlinx.css.StyledElement
import mui.material.Button
import mui.material.ButtonColor
import mui.material.Card
import mui.material.ToggleButton
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.inlineStyles
import web.cssom.ident
import web.dom.Element

object GridAreas {
    val hold = ident("hold")
    val go = ident("go")
}

private val TransitionControlView = xComponent<TransitionProps>("TransitionControl") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.controls

    var holdEngaged by state { false }
    val handleHoldButtonClick by mouseEventHandler {
        holdEngaged = !holdEngaged
        logger.warn { "holdEngaged: $holdEngaged" }
    }

    val handleGoButtonClick by mouseEventHandler {
        holdEngaged = false
        logger.warn { "holdEngaged: $holdEngaged" }
    }


    val rootEl = ref<Element>()
//    clientPreview.transition.rotate = props.transitionControl.rotate

    val slider = memo { Slider("Manual", 0f, 0f, 1f) }

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

    Card {
        attrs.classes = jso { this.root = -styles.transitionCard }
        ref = rootEl

//        header { +"Transitions! \uD83D\uDE1E" }

        Button {
            attrs.classes = jso {
                if (holdEngaged) {
                    this.root = -styles.transitionHoldButton
                } else {
                    this.root = -styles.transitionHoldButton and styles.transitionHoldEngaged
                }
            }
            attrs.sx { gridArea = GridAreas.hold }
            attrs.color = ButtonColor.secondary
            attrs.onClick = handleHoldButtonClick
            +"Hold"
        }

        Button {
            attrs.sx { gridArea = GridAreas.go }
            attrs.disabled = !holdEngaged
            attrs.onClick = handleGoButtonClick
            +"Go"
        }

        div {
            inlineStyles { gridArea = "fader" }

            slider {
                attrs.slider = slider
            }
        }

        div {
            inlineStyles { gridArea = "speed" }
            +"Speed: "
            ToggleButton {
                attrs.classes = jso { this.root = -styles.speedButton }
                attrs.selected = speed == ".25s"; +"¼s"
                attrs.value = ".25s"
            }
            ToggleButton {
                attrs.classes = jso { this.root = -styles.speedButton }
                attrs.selected = speed == ".5s"; +"½s"
                attrs.value = ".5s"
            }
            ToggleButton {
                attrs.classes = jso { this.root = -styles.speedButton }
                attrs.selected = speed == "1s"; +"1s"
                attrs.value = "1s"
            }
            ToggleButton {
                attrs.classes = jso { this.root = -styles.speedButton }
                attrs.selected = speed == "2s"; +"2s"
                attrs.value = "2s"
            }
        }

        div {
            inlineStyles { gridArea = "shape" }
            +"Shape: "
            ToggleButton { attrs.selected = shape == "linear"; attrs.value = "linear"; +"Linear" }
            ToggleButton { attrs.selected = shape == "ease"; attrs.value = "ease"; +"Ease" }
        }

        div {
            inlineStyles { gridArea = "effect" }
            +"Effect: "
            ToggleButton { attrs.selected = effect == "fade"; attrs.value = "fade"; +"Fade" }
            ToggleButton { attrs.selected = effect == "dissolve"; attrs.value = "dissolve"; +"Dissolve" }
        }
    }
}

var StyledElement.gridArea: String
    get() = error("Not supported.")
    set(value) = put("gridArea", value)

external interface TransitionProps : Props {
    var controlProps: ControlProps
    var transitionControl: OpenTransitionControl
}

fun RBuilder.transitionControl(handler: RHandler<TransitionProps>) =
    child(TransitionControlView, handler = handler)