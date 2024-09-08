package baaahs.util

import react.RBuilder
import react.useEffect
import react.useEffectWithCleanup
import react.useRef
import web.timers.clearInterval
import web.timers.setInterval
import kotlin.time.Duration

@Suppress("unused")
fun RBuilder.useInterval(delay: Duration, callback: () -> Unit) {
    val savedCallback = useRef(callback)

    useEffect(callback) {
        savedCallback.current = callback
    }

    useEffectWithCleanup(delay) {
        val id = setInterval(delay) {
            savedCallback.current!!()
        }

        onCleanup { clearInterval(id) }
    }
}