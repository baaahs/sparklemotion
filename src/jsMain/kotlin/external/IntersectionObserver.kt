package external

import org.w3c.dom.HTMLElement

external interface IntersectionObserverEntry {
    val isIntersecting: Boolean
}

external class IntersectionObserver(callback: (Array<IntersectionObserverEntry>) -> Unit) {
    fun observe(htmlElement: HTMLElement)
    fun disconnect()
}
