package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.slider.slider
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

    var position by state { 0f }
    val handlePositionChange by handler { newPosition: Float ->
        position = newPosition
    }

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
                attrs.title = "Manual"
                attrs.position = position
                attrs.contextPosition = null
                attrs.minValue = 0f
                attrs.maxValue = 1f
                attrs.reversed = false
                attrs.showTicks = false

                attrs.onPositionChange = handlePositionChange
            }
        }

        div {
            inlineStyles { gridArea = "speed" }
            +"Speed: "
            ToggleButton {
                attrs.classes = jso { this.root = -styles.speedButton }
                attrs.selected = speed == ".25s"; +"¼s"
            }
            ToggleButton {
                attrs.classes = jso { this.root = -styles.speedButton }
                attrs.selected = speed == ".5s"; +"½s"
            }
            ToggleButton {
                attrs.classes = jso { this.root = -styles.speedButton }
                attrs.selected = speed == "1s"; +"1s"
            }
            ToggleButton {
                attrs.classes = jso { this.root = -styles.speedButton }
                attrs.selected = speed == "2s"; +"2s"
            }
        }

        div {
            inlineStyles { gridArea = "shape" }
            +"Shape: "
            ToggleButton { attrs.selected = shape == "linear"; +"Linear" }
            ToggleButton { attrs.selected = shape == "ease"; +"Ease" }
        }

        div {
            inlineStyles { gridArea = "effect" }
            +"Effect: "
            ToggleButton { attrs.selected = effect == "fade"; +"Fade" }
            ToggleButton { attrs.selected = effect == "dissolve"; +"Dissolve" }
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