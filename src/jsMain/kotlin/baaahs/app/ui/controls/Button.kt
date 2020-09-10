package baaahs.app.ui.controls

import baaahs.show.live.OpenButtonControl
import baaahs.ui.*
import react.*
import react.dom.div

val Button = xComponent<ButtonProps>("Button") { props ->

    div {
    }
}

external interface ButtonProps : SpecialControlProps {
    var button: OpenButtonControl
}

fun RBuilder.button(handler: RHandler<ButtonProps>) =
    child(Button, handler = handler)