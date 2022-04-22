package baaahs.util

import external.ResizeObserver
import external.lodash.throttle
import org.w3c.dom.Element
import react.RBuilder
import react.RefObject
import react.useCallback
import react.useEffect

fun RBuilder.useResizeListener(elementRef: RefObject<out Element>, onResized: () -> Unit) {
    val onResizedThrottled = useCallback(elementRef, callback = throttle(onResized, 40))

    // Fire the callback anytime the ref is resized
    useEffect {
        val element = elementRef.current ?: return@useEffect

        val ro = ResizeObserver { _, _ ->
            onResizedThrottled()
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