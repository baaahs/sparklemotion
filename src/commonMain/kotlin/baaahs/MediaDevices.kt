package baaahs

import baaahs.imaging.Image
import kotlinx.serialization.Transient

interface MediaDevices {
    fun getCamera(width: Int, height: Int): Camera

    interface Camera {
        var onImage: (image: Image) -> Unit

        fun close()
    }

    data class Region(val x0: Int, val y0: Int, val x1: Int, val y1: Int) {
        @Transient
        val width = x1 - x0

        @Transient
        val height = x1 - x0
    }
}
