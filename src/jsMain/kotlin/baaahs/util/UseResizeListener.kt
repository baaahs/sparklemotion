package baaahs.util

import baaahs.imaging.Dimen
import external.lodash.throttle
import react.*
import web.dom.Element
import web.dom.observers.ResizeObserver
import web.html.HTMLElement
import web.timers.clearTimeout
import web.timers.setTimeout
import kotlin.time.Duration.Companion.milliseconds

@Suppress("unused")
fun RBuilder.useResizeListener(elementRef: RefObject<out Element>, onResized: (width: Int, height: Int) -> Unit) {
    val onResizedThrottled = useCallback(elementRef, callback = throttle(onResized, 40))

    val previousSize = useRef<Pair<Int, Int>>(null)

    // Fire the callback anytime the ref is resized
    useEffect {
        val element = elementRef.current ?: return@useEffect

        val ro = ResizeObserver { _, _ ->
            val size = element.clientWidth to element.clientHeight
            if (previousSize.current != size) {
                previousSize.current = size
                onResizedThrottled(element.clientWidth, element.clientHeight)
            }
        }
        ro.observe(element)

        cleanup { ro.unobserve(element) }
    }

    // Fire once when the component first mounts
    useEffect(elementRef, onResizedThrottled) {
        val intervalId = setTimeout(500.milliseconds) {
            val element = elementRef.current ?: return@setTimeout
            onResizedThrottled(element.clientWidth, element.clientHeight)
        }

        cleanup { clearTimeout(intervalId) }
    }
}

fun RBuilder.onResize(onResized: ((width: Int, height: Int) -> Unit)? = null): Resizer {
    val ref = useRef<HTMLElement>()
    var dimens by useState(Dimen(0, 0))
    useResizeListener(ref) { width, height ->
        dimens = Dimen(width, height)
        onResized?.invoke(width, height)
    }
    return Resizer(ref) { dimens }
}

class Resizer(val ref: MutableRefObject<HTMLElement>, private val getDimens: () -> Dimen) {
    val dimens get() = getDimens()
    val width get() = getDimens().width
    val height get() = getDimens().height
}