package baaahs.util

import react.RBuilder
import react.useEffect
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

    useEffect(delay) {
        val id = setInterval(delay) {
            savedCallback.current!!()
        }

        cleanup { clearInterval(id) }
    }
}