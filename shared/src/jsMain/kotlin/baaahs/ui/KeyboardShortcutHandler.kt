package baaahs.ui

import js.objects.jso
import web.dom.document
import web.events.AddEventListenerOptions
import web.events.Event
import web.events.EventTarget
import web.events.addEventListener
import web.events.removeEventListener
import web.html.HTMLElement
import web.html.HTMLInputElement
import web.html.HTMLTextAreaElement
import web.uievents.KeyboardEvent
import web.uievents.PointerEvent

class KeyboardShortcutHandler(val target: EventTarget? = null) {
    private val handlers = arrayListOf<Handler>()
    private var pointersDown = 0

    init {
        if (target != null) {
            listen(target)
            (target as? HTMLElement)?.tabIndex = -1
        }
    }

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    private val handleKeyDown = { e: Event ->
        e as KeyboardEvent

        when {
//            is HTMLButtonElement,
            e.target is HTMLInputElement
//                  || e.target is HTMLSelectElement,
//                  || e.target is HTMLOptionElement,
                    || e.target is HTMLTextAreaElement -> {
            } // Ignore.

            e.key == "Escape" && pointersDown > 0 -> {
                document.dispatchEvent(PointerEvent(PointerEvent.POINTER_CANCEL))
            } // Ignore.

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
        Unit
    }

    private val handlePointerDown = { e: PointerEvent -> pointersDown++; Unit }
    private val handlePointerUp = { e: PointerEvent -> pointersDown--; Unit }
    private val capture = jso<AddEventListenerOptions> { capture = true}

    fun listen(target: EventTarget): EventTarget {
        target.addEventListener(KeyboardEvent.KEY_DOWN, handleKeyDown)
        target.addEventListener(PointerEvent.POINTER_DOWN, handlePointerDown, capture)
        target.addEventListener(PointerEvent.POINTER_UP, handlePointerUp, capture)
        return target
    }

    fun unlisten(target: EventTarget) {
        target.removeEventListener(KeyboardEvent.KEY_DOWN, handleKeyDown)
        target.removeEventListener(PointerEvent.POINTER_DOWN, handlePointerDown, capture)
        target.removeEventListener(PointerEvent.POINTER_UP, handlePointerUp, capture)
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