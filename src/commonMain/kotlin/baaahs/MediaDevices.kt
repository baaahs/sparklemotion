package baaahs

import baaahs.imaging.Image
import kotlin.math.sqrt

interface MediaDevices {
    fun getCamera(width: Int, height: Int): Camera

    interface Camera {
        var onImage: (image: Image) -> Unit

        fun close()
    }

    data class Region(val x0: Int, val y0: Int, val x1: Int, val y1: Int) {
        val width = x1 - x0
        val height = y1 - y0

        val centerX = x1 - x0
        val centerY = y1 - y0

        fun distanceTo(other: Region): Float {
            val dX = centerX - other.centerX
            val dY = centerY - other.centerY

            return sqrt((dX * dX + dY * dY).toDouble()).toFloat()
        }
    }
}
