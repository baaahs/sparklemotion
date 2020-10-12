package baaahs.ui

import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.KeyboardEvent

class KeyboardShortcutHandler(private val handler: (event: KeyboardEvent) -> Unit) {
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    private val handleKeyDown = { event: KeyboardEvent ->
        when (event.target) {
//            is HTMLButtonElement,
            is HTMLInputElement,
//            is HTMLSelectElement,
//            is HTMLOptionElement,
            is HTMLTextAreaElement -> {} // Ignore

            else -> handler(event)
        }
    } as EventListener

    fun listen(target: EventTarget) {
        target.addEventListener("keydown", handleKeyDown)
    }

    fun unlisten(target: EventTarget) {
        target.removeEventListener("keydown", handleKeyDown)
    }
}