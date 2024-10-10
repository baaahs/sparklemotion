package baaahs.imaging

import baaahs.geom.Vector2

data class Dimen(val width: Int, val height: Int) {
    fun toVector2(): Vector2 = Vector2(width.toDouble(), height.toDouble())

    fun aspect() = width.toDouble() / height

    operator fun times(i: Number): Dimen =
        Dimen(
            (width * i.toDouble()).toInt(),
            (height * i.toDouble()).toInt()
        )

    operator fun div(i: Number): Dimen =
        Dimen(
            (width / i.toDouble()).toInt(),
            (height / i.toDouble()).toInt()
        )

    fun bestFit(dimen: Dimen): Dimen {
        return if (dimen.aspect() > aspect()) {
            dimen * (width.toDouble() / dimen.width)
        } else {
            dimen * (height.toDouble() / dimen.height)
        }
    }
}
