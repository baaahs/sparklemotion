package external

import dom.Element
import org.w3c.dom.DOMRectReadOnly

external class ResizeObserver(
    callback: (Array<ResizeObserverEntry>, ResizeObserver) -> Unit
) {
    fun observe(element: Element)
    fun unobserve(element: Element)
    fun disconnect()
}

external interface ResizeObserverOptions {
    var box: String // "border-box", "content-box", "device-pixel-content-box"
}

external interface ResizeObserverEntry {
    val target: Element
    val contentRect: DOMRectReadOnly
    val borderBoxSize: Array<ResizeObserverSize>
    val contentBoxSize: Array<ResizeObserverSize>
    val devicePixelContentBoxSize: Array<ResizeObserverSize>
}

external interface ResizeObserverSize {
    val inlineSize: Double
    val blockSize: Double
}