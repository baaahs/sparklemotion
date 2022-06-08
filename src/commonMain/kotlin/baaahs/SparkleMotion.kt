package baaahs

import baaahs.util.Logger

object SparkleMotion {
    const val MAX_PIXEL_COUNT = 2048
    const val DEFAULT_PIXEL_COUNT = 1024
    const val PIXEL_COUNT_UNKNOWN = -1

    const val EXTRA_ASSERTIONS = true
    const val TRACE_GLSL = false

    // This theoretically _may_ (but doesn't appear to, on Chrome at least) slow down client framerate.
    // See https://www.paulirish.com/2012/why-moving-elements-with-translate-is-better-than-posabs-topleft/
    const val USE_CSS_TRANSFORM = false

    val logger = Logger<SparkleMotion>()
}