package baaahs.ui.gridlayout

import baaahs.geom.Vector2
import baaahs.geom.Vector2I

data class Rect(
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int
) {
    val right get() = left + width
    val bottom get() = top + height
    val topLeft get() = Vector2I(left, top)
    val size get() = Vector2I(width, height)
    val center get() = Vector2I(left + width / 2, top + height / 2)

    val edges get() = "Rect($left,$top — $right,$bottom)"
    fun contains(point: Vector2I): Boolean =
        point.x >= left && point.y >= top && point.x < right && point.y < bottom

    fun inset(length: Int): Rect =
        Rect(left + length, top + length, width - length * 2, height - length * 2)

    fun resizeFromCenter(newSize: Rect): Rect {
        val deltaX = (newSize.width - width) / 2
        val deltaY = (newSize.height - height) / 2
        return Rect(left - deltaX, top - deltaY, newSize.width, newSize.height)
            .also {
                println("Resizing $this to ${newSize.width}x${newSize.height}: $it")
            }
    }

    operator fun plus(other: Rect): Rect =
        Rect(left + other.left, top + other.top, width + other.width, top + other.top)

    operator fun plus(by: Vector2I): Rect =
        Rect(left + by.x, top + by.y, width, height)

    operator fun minus(other: Rect): Rect =
        Rect(left - other.left, top - other.top, width - other.width, top - other.top)

    operator fun div(divisor: Int): Rect =
        Rect(left / divisor, top / divisor, width / divisor, top / divisor)
}