package baaahs.util

import external.lodash.throttle
import react.*
import web.dom.Element
import web.dom.observers.ResizeObserver
import web.timers.clearTimeout
import web.timers.setTimeout
import kotlin.time.Duration.Companion.milliseconds

@Suppress("UnusedReceiverParameter")
fun RBuilder.useResizeListener(elementRef: RefObject<out Element>, onResized: (width: Int, height: Int) -> Unit) {
    val onResizedThrottled = useCallback(elementRef, callback = throttle(onResized, 40))

    val previousSize = useRef<Pair<Int, Int>>(null)

    // Fire the callback anytime the ref is resized
    useEffectWithCleanup {
        val element = elementRef.current ?: return@useEffectWithCleanup

        val ro = ResizeObserver { _, _ ->
            console.log("resized", element, element.clientWidth, element.clientHeight)
            val size = element.clientWidth to element.clientHeight
            if (previousSize.current != size) {
                previousSize.current = size
                onResizedThrottled(element.clientWidth, element.clientHeight)
            }
        }
        ro.observe(element)

        onCleanup { ro.unobserve(element) }
    }

    // Fire once when the component first mounts
    useEffectWithCleanup(elementRef, onResizedThrottled) {
        val intervalId = setTimeout(500.milliseconds) {
            val element = elementRef.current ?: return@setTimeout
            onResizedThrottled(element.clientWidth, element.clientHeight)
        }

        onCleanup { clearTimeout(intervalId) }
    }
}