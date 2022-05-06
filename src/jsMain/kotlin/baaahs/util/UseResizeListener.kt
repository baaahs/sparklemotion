package baaahs.util

import external.ResizeObserver
import external.lodash.throttle
import org.w3c.dom.Element
import react.*

@Suppress("unused")
fun RBuilder.useResizeListener(elementRef: RefObject<out Element>, onResized: () -> Unit) {
    val onResizedThrottled = useCallback(elementRef, callback = throttle(onResized, 40))

    val previousSize = useRef<Pair<Int, Int>>(null)

    // Fire the callback anytime the ref is resized
    useEffect {
        val element = elementRef.current ?: return@useEffect

        val ro = ResizeObserver { _, _ ->
            val size = element.clientWidth to element.clientHeight
            if (previousSize.current != size) {
                previousSize.current = size
                onResizedThrottled()
            }
        }
        ro.observe(element)

        cleanup { ro.unobserve(element) }
    }

    // Fire once when the component first mounts
    useEffect(elementRef, onResizedThrottled) {
        val intervalId = baaahs.window.setTimeout(timeout = 500, handler = {
            onResizedThrottled()
        })

        cleanup { baaahs.window.clearTimeout(intervalId) }
    }
}