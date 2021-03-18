package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.preview.ClientPreview
import baaahs.plugin.core.OpenTransitionControl
import baaahs.show.live.ControlProps
import baaahs.ui.xComponent
import kotlinx.html.js.onClickFunction
import kotlinx.html.select
import materialui.components.button.button
import materialui.components.button.enums.ButtonColor
import materialui.components.card.card
import materialui.lab.components.togglebutton.toggleButton
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.*
import react.dom.h1
import react.dom.h5

val Transition = xComponent<TransitionProps>("Transition") { props ->
    val appContext = useContext(appContext)

    var holdEngaged by state { false }
    val handleHoldButtonClick = handler("handleHoldButtonClick") { _: Event ->
        holdEngaged = !holdEngaged
        logger.warn { "holdEngaged: $holdEngaged" }
    }


    val rootEl = ref<Element>()
//    clientPreview.transition.rotate = props.transitionControl.rotate

//    val transition = clientPreview.transition
    onMount {
//        transition.container = rootEl.current as HTMLDivElement

        withCleanup {
//            transition.container = null
//            clientPreview.detach()
        }
    }

    card(/*Styles.transitionCard on PaperStyle.root*/) {
        ref = rootEl

        h5 { +"Transitions! \uD83D\uDE1E" }

        toggleButton() {
            attrs.value = "[Hold]"
            // Yep, for some reason you need to set it directly or it doesn't work.
            attrs["selected"] = holdEngaged
            attrs["color"] = ButtonColor.primary.name
            attrs.onClickFunction = handleHoldButtonClick
            +"Hold"
        }

        toggleButton {
            attrs.value = "[Hold]"
            // Yep, for some reason you need to set it directly or it doesn't work.
            attrs["selected"] = holdEngaged
            attrs["color"] = ButtonColor.secondary.name
            attrs.onClickFunction = handleHoldButtonClick
            +"Hold2"
        }

        button {
//            attrs.value = "[Hold]"
            // Yep, for some reason you need to set it directly or it doesn't work.
//            attrs["selected"] = holdEngaged
            attrs.color = ButtonColor.primary
            attrs.onClickFunction = handleHoldButtonClick
            +"Hold"
        }

        button {
            attrs.value = "[Hold]"
            // Yep, for some reason you need to set it directly or it doesn't work.
            attrs["selected"] = holdEngaged
            attrs.color = ButtonColor.secondary
            attrs.onClickFunction = handleHoldButtonClick
            +"Hold2"
        }
    }
}

external interface TransitionProps : RProps {
    var controlProps: ControlProps
    var transitionControl: OpenTransitionControl
}

fun RBuilder.transition(handler: RHandler<TransitionProps>) =
    child(Transition, handler = handler)