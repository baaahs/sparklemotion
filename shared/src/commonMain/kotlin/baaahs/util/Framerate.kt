package baaahs.util

import baaahs.ui.Observable
import kotlin.math.roundToInt

class Framerate : Observable() {
    var elapsedMs: Int = 0
        private set
    val fps: Int get() = (1000f / elapsedMs).roundToInt()

    var averageElapsedMs: Float = 0f
        private set
    val averageFps: Int get() = (1000f / averageElapsedMs).roundToInt()

    fun elapsed(ms: Int) {
        elapsedMs = ms

        averageElapsedMs = if (averageElapsedMs == 0f) {
            // Probably means this is the first datapoint we've received.
            ms.toFloat()
        } else {
            (averageElapsedMs * 99 + elapsedMs) / 100
        }

        notifyChanged()
    }

    fun summarize(): String =
        "Average FPS=${averageFps}; average elapsed=${averageElapsedMs}ms"
}