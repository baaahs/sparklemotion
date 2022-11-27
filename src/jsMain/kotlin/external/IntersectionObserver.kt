package external

import dom.Element
import dom.html.HTMLElement
import org.w3c.dom.DOMRectReadOnly

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
