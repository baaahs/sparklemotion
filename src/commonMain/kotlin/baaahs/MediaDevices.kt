package baaahs

import baaahs.imaging.Bitmap
import baaahs.imaging.Dimen
import baaahs.imaging.Image
import baaahs.mapper.Uv
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

interface MediaDevices {
    suspend fun enumerate(): List<Device>
    fun getCamera(selectedDevice: Device? = null): Camera

    interface Camera {
        var onImage: (image: Image) -> Unit

        fun close()
    }

    data class Region(val x0: Int, val y0: Int, val x1: Int, val y1: Int, val sourceDimen: Dimen) {
        val width = x1 - x0
        val height = y1 - y0

        val centerX = (x1 - x0) / 2 + x0
        val centerY = (y1 - y0) / 2 + y0

        val xRange = x0 until x1
        val yRange = y0 until y1

        val centerUv get() = Uv.fromXY(centerX, centerY, sourceDimen)

        val changedAmount by lazy {
            val changePixels = width * height
            val sourcePixels = sourceDimen.width * sourceDimen.height
            changePixels / sourcePixels.toFloat()
        }

        fun distanceTo(other: Region): Float {
            if (sourceDimen != other.sourceDimen)
                error("mismatched region dimensions ($sourceDimen != ${other.sourceDimen}")

            val dX = centerX - other.centerX
            val dY = centerY - other.centerY

            return sqrt((dX * dX + dY * dY).toDouble()).toFloat()
        }

        fun intersectionWith(other: Region): Region {
            val leftX = max(x0, other.x0)
            val rightX = min(x1, x1)
            val topY = max(y0, other.y0)
            val bottomY = min(y1, other.y1)

            return if (leftX < rightX && topY < bottomY) {
                Region(leftX, topY, rightX, bottomY, sourceDimen)
            } else {
                EMPTY
            }
        }

        fun sqPix(): Float = sqrt((x1 - x0.toDouble()).pow(2) + (y1 - y0.toDouble()).pow(2)).toFloat()
        fun scaled(fromX: Int, fromY: Int, toX: Int, toY: Int): Region {
            return Region(
                (x0.toFloat() / fromX * toX).toInt(),
                (y0.toFloat() / fromX * toX).toInt(),
                (x1.toFloat() / fromY * toY).toInt(),
                (y1.toFloat() / fromY * toY).toInt(),
                sourceDimen
            )
        }

        fun isEmpty(): Boolean = width <= 0 || height <= 0

        companion object {
            val EMPTY = Region(-1, -1, -1, -1, Dimen(1, 1))

            fun containing(bitmap: Bitmap) = Region(0, 0, bitmap.width, bitmap.height, bitmap.dimen)
            fun containing(image: Image) = Region(0, 0, image.width, image.height, image.dimen)
        }
    }

    data class Device(
        val deviceId: String,
        val kind: String,
        val label: String,
        val groupId: String,
    )
}
