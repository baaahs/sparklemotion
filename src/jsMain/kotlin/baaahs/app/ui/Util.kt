package baaahs.app.ui

import org.w3c.dom.events.Event

val preventDefault: (Event) -> Unit = { event -> event.preventDefault() }
val disableScroll = {
    baaahs.document.body?.addEventListener("touchmove", preventDefault, js("{ passive: false }"))
}
val enableScroll = {
    baaahs.document.body?.removeEventListener("touchmove", preventDefault)
}