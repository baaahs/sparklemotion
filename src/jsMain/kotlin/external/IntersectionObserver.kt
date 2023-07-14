package external

import org.w3c.dom.DOMRectReadOnly
import web.dom.Element
import web.html.HTMLElement

external class IntersectionObserver(
    callback: (Array<IntersectionObserverEntry>) -> Unit,
    options: IntersectionObserverOptions? = definedExternally
) {
    fun observe(htmlElement: HTMLElement)
    fun unobserve(htmlElement: HTMLElement)
    fun disconnect()
}

external interface IntersectionObserverOptions {
    var root: Element?
    var rootMargin: String?
    var threshold: Any? // Number or Array<Number>
}

external interface IntersectionObserverEntry {
    val boundingClientRect: DOMRectReadOnly
    val intersectionRatio: Double
    val intersectionRect: DOMRectReadOnly
    val isIntersecting: Boolean
    val rootBounds: DOMRectReadOnly
    val target: Element
//    val time: DOMHighResTimeStamp
}
