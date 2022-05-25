package baaahs.ui

import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.EventTarget
import org.w3c.dom.events.KeyboardEvent

class KeyboardShortcutHandler {
    private val handlers = arrayListOf<Handler>()

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    private val handleKeyDown = { e: KeyboardEvent ->
        when (e.target) {
//            is HTMLButtonElement,
            is HTMLInputElement,
//            is HTMLSelectElement,
//            is HTMLOptionElement,
            is HTMLTextAreaElement -> {
            } // Ignore

            else -> {
                val keypress = with(e) { Keypress(key, metaKey, ctrlKey, shiftKey) }
                val handled = handlers.reversed().any { handler ->
                    handler.callback(keypress, e) == KeypressResult.Handled
                }
                if (handled) {
                    e.stopPropagation()
                    e.preventDefault()
                } else {
                    console.log("Unhandled keypress:", keypress)
                }
            }
        }
    } as EventListener

    fun listen(target: EventTarget) {
        target.addEventListener("keydown", handleKeyDown)
    }

    fun unlisten(target: EventTarget) {
        target.removeEventListener("keydown", handleKeyDown)
    }

    fun handle(handler: (keypress: Keypress, event: KeyboardEvent) -> KeypressResult): Handler =
        Handler(handler).also { handlers.add(it) }

    inner class Handler(
        internal val callback: (keypress: Keypress, event: KeyboardEvent) -> KeypressResult
    ) {
        fun remove() {
            handlers.remove(this)
        }
    }
}

enum class KeypressResult {
    Handled,
    NotHandled
}

data class Keypress(
    val key: String,
    val metaKey: Boolean = false,
    val ctrlKey: Boolean = false,
    val shiftKey: Boolean = false
) {
    val modifiers get() = listOf(
        mapOf(true to "meta")[metaKey],
        mapOf(true to "ctrl")[ctrlKey],
        mapOf(true to "shift")[shiftKey]
    ).filterNotNull().joinToString("-")
}