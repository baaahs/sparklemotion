package baaahs.ui.gridlayout

import baaahs.show.live.GridDimens

data class LayoutItem(
    val x: Int,
    val y: Int,
    val w: Int,
    val h: Int,
    val i: String,
    val minW: Int? = null,
    val minH: Int? = null,
    val maxW: Int? = null,
    val maxH: Int? = null,
    val moved: Boolean = false,
    val isStatic: Boolean = false,
    val isDraggable: Boolean = true,
    val isResizable: Boolean = true,
    val isPlaceholder: Boolean = false
) {
    val right: Int get() = x + w - 1
    val bottom: Int get() = y + h - 1
    val gridDimens: GridDimens = GridDimens(w, h)

    fun toStatic() =
        LayoutItem(x, y, w, h, i, isStatic = true)

    fun movedTo(
        x: Int?,
        y: Int?
    ) = copy(
        x = x ?: this.x,
        y = y ?: this.y,
        moved = true
    )

    fun collidesWith(other: LayoutItem): Boolean {
        if (i === other.i) return false // same element
        if (x + w <= other.x) return false // this is left of other
        if (x >= other.x + other.w) return false // this is right of other
        if (y + h <= other.y) return false // this is above other
        if (y >= other.y + other.h) return false // this is below other
        return true // boxes overlap
    }
}

/**
 * Sort layout items by row ascending then column ascending.
 */
fun List<LayoutItem>.sortLayoutItemsByRowCol(reverse: Boolean = false): List<LayoutItem> =
    sortedWith { a, b ->
        when {
            a.y == b.y && a.x == b.x -> 0
            a.y == b.y && a.x > b.x -> 1
            if (reverse) b.y > a.y else a.y > b.y -> 1
            else -> -1
        }
    }

/**
 * Sort layout items by column ascending then row ascending.
 */
fun List<LayoutItem>.sortLayoutItemsByColRow(reverse: Boolean = false): List<LayoutItem> =
    sortedWith { a, b ->
        when {
            a.x == b.x && a.y == b.y -> 0
            a.x == b.x && a.y > b.y -> 1
            if (reverse) a.x > b.x else b.x > a.x -> 1
            else -> -1
        }
    }

data class LayoutItemSize(
    val width: Int,
    val height: Int
)

data class Position(
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int
)

enum class Direction(
    val xIncr: Int, val yIncr: Int
) {
    North(0, -1) {
        override val opposite: Direction get() = South

        override fun sort(collisions: List<LayoutItem>): List<LayoutItem> =
            collisions.sortLayoutItemsByRowCol(reverse = true)
    },

    South(0, 1) {
        override val opposite: Direction get() = North

        override fun sort(collisions: List<LayoutItem>): List<LayoutItem> =
            collisions.sortLayoutItemsByRowCol()

    },

    East(1, 0) {
        override val opposite: Direction get() = West

        override fun sort(collisions: List<LayoutItem>): List<LayoutItem> =
            collisions.sortLayoutItemsByRowCol()

    },

    West(-1, 0) {
        override val opposite: Direction get() = East

        override fun sort(collisions: List<LayoutItem>): List<LayoutItem> =
            collisions.sortLayoutItemsByRowCol(reverse = true)

    };

    val isHorizontal get() = xIncr != 0
    val isVertical get() = yIncr != 0
    abstract val opposite: Direction

    abstract fun sort(collisions: List<LayoutItem>): List<LayoutItem>

    companion object {
        fun rankedPushOptions(x: Int, y: Int): Array<Direction> {
            return when {
                x > 0 && y == 0 -> arrayOf(West, East, South, North)
                x < 0 && y == 0 -> arrayOf(East, West, South, North)
                x == 0 && y > 0 -> arrayOf(North, South, East, West)
                x == 0 && y < 0 -> arrayOf(South, North, East, West)
                else -> arrayOf(South, North, East, West)
            }
        }
    }
}

class ImpossibleLayoutException : Exception()
