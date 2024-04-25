package baaahs.ui

import baaahs.document
import js.objects.jso
import kotlinx.css.BorderStyle
import web.events.Event
import web.events.addEventListener
import web.events.removeEventListener
import web.uievents.TouchEvent

val preventDefault: (Event) -> Unit = { event -> event.preventDefault() }
val disableScroll = {
    document.body.addEventListener(TouchEvent.touchMove(), preventDefault, jso { passive = false })
}
val enableScroll = {
    document.body.removeEventListener(TouchEvent.touchMove(), preventDefault)
}

val groove = "groove".unsafeCast<BorderStyle>()
val inset = "inset".unsafeCast<BorderStyle>()
val outset = "outset".unsafeCast<BorderStyle>()