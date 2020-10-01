package baaahs.app.ui.controls

import baaahs.show.live.ControlProps
import baaahs.show.live.ControlView
import baaahs.show.live.OpenControl
import baaahs.ui.xComponent
import react.FunctionalComponent
import react.RBuilder
import react.RHandler
import react.child
import react.dom.div

class UnknownControlView(val openControl: OpenControl) : ControlView {
    override fun <P : ControlProps<in OpenControl>> getReactElement(): FunctionalComponent<P> {
        return Unknown.unsafeCast<FunctionalComponent<P>>()
    }
}

val Unknown = xComponent<UnknownProps>("Unknown") { props ->
    div {
            +"Huh? What's a ${props.control}?"
    }
}

external interface UnknownProps : ControlProps<OpenControl>

fun RBuilder.unknown(handler: RHandler<UnknownProps>) =
    child(Unknown, handler = handler)