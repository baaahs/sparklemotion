package baaahs.ui

import web.events.Event
import web.events.EventTarget
import web.html.HTMLElement
import web.html.HTMLInputElement
import web.html.HTMLTextAreaElement
import web.uievents.KeyboardEvent

class KeyboardShortcutHandler(val target: EventTarget? = null) {
    private val handlers = arrayListOf<Handler>()

    init {
        if (target != null) {
            listen(target)
            (target as? HTMLElement)?.tabIndex = -1
        }
    }

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    private val handleKeyDown = { e: Event ->
        e as KeyboardEvent

        when (e.target) {
//            is HTMLButtonElement,
            is HTMLInputElement,
//            is HTMLSelectElement,
//            is HTMLOptionElement,
            is HTMLTextAreaElement -> {
            } // Ignore

            else -> {
                val keypress = with(e) { Keypress(code as String, metaKey, ctrlKey, shiftKey) }
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
    }

    fun listen(target: EventTarget): EventTarget {
        target.addEventListener(KeyboardEvent.KEY_DOWN, handleKeyDown)
        return target
    }

    fun unlisten(target: EventTarget) {
        target.removeEventListener(KeyboardEvent.KEY_DOWN, handleKeyDown)
    }

    fun release() {
        if (target != null) {
            unlisten(target)
        }
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