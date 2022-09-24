package baaahs.ui.gridlayout

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
    fun toStatic() =
        LayoutItem(x, y, w, h, i, isStatic = true)

    fun collidesWith(other: LayoutItem): Boolean {
        if (i === other.i) return false // same element
        if (x + w <= other.x) return false // this is left of other
        if (x >= other.x + other.w) return false // this is right of other
        if (y + h <= other.y) return false // this is above other
        if (y >= other.y + other.h) return false // this is below other
        return true // boxes overlap
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

enum class CompactType {
    horizontal,
    vertical,
    none
}

enum class Axis {
    x {
        override fun incr(item: LayoutItem) = item.copy(x = item.x + 1)
        override fun set(item: LayoutItem, value: Int): LayoutItem = item.copy(x = value)

    },
    y {
        override fun incr(item: LayoutItem) = item.copy(y = item.y + 1)
        override fun set(item: LayoutItem, value: Int): LayoutItem = item.copy(y = value)
    };

    abstract fun incr(item: LayoutItem): LayoutItem
    abstract fun set(item: LayoutItem, value: Int): LayoutItem
}
