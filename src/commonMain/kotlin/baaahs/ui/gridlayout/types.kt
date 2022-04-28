package baaahs.ui.gridlayout

data class LayoutItem(
    var x: Int,
    var y: Int,
    var w: Int,
    var h: Int,
    var i: String,
    var minW: Int? = null,
    var minH: Int? = null,
    var maxW: Int? = null,
    var maxH: Int? = null,
    var moved: Boolean = false,
    var isStatic: Boolean = false,
    var isDraggable: Boolean = true,
    var isResizable: Boolean = true,
    var isPlaceholder: Boolean = false
) {
    operator fun get(axis: Axis) = axis[this]

    operator fun set(axis: Axis, value: Int) { axis[this] = value }

    fun toStatic() =
        LayoutItem(x, y, w, h, i, isStatic = true)
}

data class LayoutItemSize(
    val w: Int,
    val h: Int
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
        override fun increment(layoutItem: LayoutItem) {
            layoutItem.x++
        }

        override fun get(layoutItem: LayoutItem): Int =
            layoutItem.x

        override fun set(layoutItem: LayoutItem, value: Int) {
            layoutItem.x = value
        }
    },
    y {
        override fun increment(layoutItem: LayoutItem) {
            layoutItem.y++
        }

        override fun get(layoutItem: LayoutItem): Int =
            layoutItem.y

        override fun set(layoutItem: LayoutItem, value: Int) {
            layoutItem.y = value
        }
    };

    abstract fun increment(layoutItem: LayoutItem)

    abstract operator fun get(layoutItem: LayoutItem): Int
    abstract operator fun set(layoutItem: LayoutItem, value: Int)
}
