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

        // Probably means this is the first datapoint we've received.
        if (averageElapsedMs == 0f) {
            averageElapsedMs = ms.toFloat()
        }

        notifyChanged()
    }
}