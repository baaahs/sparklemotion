package baaahs.util

import external.ResizeObserver
import external.lodash.throttle
import org.w3c.dom.Element
import react.*

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
        val intervalId = baaahs.window.setTimeout(timeout = 500, handler = {
            val element = elementRef.current ?: return@setTimeout
            onResizedThrottled(element.clientWidth, element.clientHeight)
        })

        cleanup { baaahs.window.clearTimeout(intervalId) }
    }
}