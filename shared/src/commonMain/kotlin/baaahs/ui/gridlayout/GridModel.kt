package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.show.ImpossibleLayoutException
import baaahs.show.NoChangesException
import baaahs.show.OutOfBoundsException
import kotlin.math.min

data class GridModel(
    val rootNode: Node
) {
//    val parents = buildMap {
//        visit(null) { node, parent -> put(node, parent) }
//    }

    fun visit(visitor: (Node) -> Unit) {
        rootNode.visit(visitor)
    }

    fun visit(parent: Node?, visitor: (Node, parent: Node?) -> Unit) {
        rootNode.visit(parent, visitor)
    }

    fun moveElement(
        node: Node, toContainer: Node, cell: Vector2I, directions: Array<Direction>
    ): GridModel {
        if (toContainer.contains(node)
            && node.left == cell.x
            && node.top == cell.y
        ) {
            // No change, bail.
            throw NoChangesException()
        }

        return GridModel(
            rootNode.moveElement(node, toContainer, cell, directions)
                .canonicalize()
        )

//        return try {
//            return GridModel(rootNode.moveElement(node, toContainer, cell))
//        } catch (_: ImpossibleLayoutException) {
//            this
//        }
    }

    fun canonicalize(): GridModel =
        copy(rootNode = rootNode.canonicalize())
}

data class Node(
    val id: String,
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int,
    val layout: Layout?
) {
    var moved: Boolean = false
    var dragging: Boolean = false

    val right get() = left + width - 1
    val bottom get() = top + height - 1
    val size: GridSize get() = GridSize(width, height)

    val gridCells
        get() = (top until top + height).flatMap { y ->
            (left until left + width).map { x -> Vector2I(x, y) }
        }

    val isContainer get() = layout != null

    fun contains(node: Node) =
        layout?.children?.contains(node) == true

    fun collidesWith(other: Node): Boolean {
        if (id == other.id) return false // same element
        if (left + width <= other.left) return false // this is left of other
        if (left >= other.left + other.width) return false // this is right of other
        if (top + height <= other.top) return false // this is above other
        if (top >= other.top + other.height) return false // this is below other
        return true // boxes overlap
    }

    fun visit(visitor: (Node) -> Unit) {
        visitor(this)
        layout?.let { layout ->
            layout.children.forEach { child -> child.visit(visitor) }
        }
    }

    fun visit(parent: Node?, visitor: (Node, parent: Node?) -> Unit) {
        visitor(this, parent)
        layout?.let { layout ->
            layout.children.forEach { child -> child.visit(this, visitor) }
        }
    }

    fun findCollisions(node: Node): List<Node> =
        layout?.children?.filter { child -> !child.dragging && child.collidesWith(node) }
            ?: emptyList()

    /**
     * Move an element within this grid. Responsible for doing cascading movements of other elements.
     *
     * @param movingNode Element to move.
     * @param x X position in grid units.
     * @param y Y position in grid units.
     * @return A new layout with moved layout items.
     * @throws ImpossibleLayoutException if the move isn't possible because of collisions or constraints.
     */
    fun attemptToPlace(movingNode: Node, x: Int, y: Int, directions: Array<Direction>): Node {
        val movedNode = movingNode.copy(left = x, top = y).apply { dragging = true }
        for (direction in directions) {
            try {
                print("-> Try moving node ${movedNode.id} ${direction}ward $id[${movedNode.left},${movedNode.top}] -> $id[$x,$y]: ")
                return fitElement(movedNode, direction)
                    .also { println(" worked!") }
            } catch (e: ImpossibleLayoutException) {
                println(" failed!")
                // Try again.
            }
        }
        throw ImpossibleLayoutException("Item ${movingNode.id} can't be moved to $x,$y.")
    }

    private fun moveElementInternal(movingNode: Node, x: Int, y: Int, pushDirection: Direction): Node {
        // Short-circuit if nothing to do.
//        if (movingNode.left == x && movingNode.top == y) return this
        val movedItem = movingNode.copy(left = x, top = y).apply { moved = true }
        return fitElement(movedItem, pushDirection)
    }

    private fun fitElement(movingNode: Node, pushDirection: Direction): Node {
        layout!!
//        val movingNode = movingNode.constrainSize(layout.columns, layout.rows)

        if (outOfBounds(movingNode))
            throw OutOfBoundsException("out of bounds, $pushDirection")

        var updatedLayout = updatedLayout(movingNode)
        var collisions = findCollisions(movingNode)

        // If it collides with anything, move it (recursively).
        while (collisions.isNotEmpty()) {
            // When doing this comparison, we have to sort the items we compare with
            // to ensure, in the case of multiple collisions, that we're getting the
            // nearest collision.
            for (collision in pushDirection.sort(collisions)) {
                println("Resolving collision between ${movingNode.id} at [${movingNode.left},${movingNode.top}] and ${collision.id} at [${collision.left},${collision.top}]")

                // Short circuit so we can't infinitely loop
//                if (collision.moved) throw ImpossibleLayoutException("collision ${collision.id} $pushDirection")

                updatedLayout =
                    updatedLayout.pushCollidingElement(movingNode, collision, pushDirection)
                collisions = updatedLayout.findCollisions(movingNode)
            }
        }

        return updatedLayout
    }

    private fun constrainSize(columns: Int, rows: Int): Node =
        if (width > columns || height > rows) {
            copy(width = min(width, columns), height = min(height, rows))
        } else this

    private fun updatedLayout(updateNode: Node): Node =
        copy(
            layout = layout?.copy(
                children = layout.children.map {
                    if (it.id == updateNode.id) updateNode else it
                }
            )
        )

    /**
     * This is where the magic needs to happen - given a collision, move an element away from the collision.
     * We attempt to move it up if there's room, otherwise it goes below.
     *
     * @param  {Array} layout            Full layout to modify.
     * @param  {GridItem} collidesWith Layout item we're colliding with.
     * @param  {GridItem} itemToMove   Layout item we're moving.
     */
    private fun pushCollidingElement(
        collidesWith: Node,
        nodeToMove: Node,
        direction: Direction
    ): Node =
        moveElementInternal(
            nodeToMove,
            nodeToMove.left + direction.xIncr,
            nodeToMove.top + direction.yIncr,
            direction
        )

    fun canonicalize(): Node = copy(
        layout = layout?.copy(
            children = layout.children.sortedWith { a, b ->
                if (a.top > b.top ||
                    (a.top == b.top && a.left > b.left) ||
                    (a.top == b.top && a.left == b.left && a.id > b.id)
                ) 1 else -1
            }.map { it.canonicalize() }
        )
    )

    private fun outOfBounds(movedNode: Node) =
        layout?.let {
            movedNode.left < 0
                    || movedNode.top < 0
                    || movedNode.right >= layout.columns
                    || movedNode.bottom >= layout.rows
        } == true

    fun removeNode(node: Node): Node =
        copy(
            layout = layout?.copy(
                children = layout.children.filter { it != node }
            )
        )

    fun moveElement(
        movingNode: Node, toContainer: Node, topLeft: Vector2I, directions: Array<Direction>
    ): Node {
        if (layout == null) return this
        val containsItem = contains(movingNode)
        val movingHere = toContainer == this
        var newNode = this
        println("moveElement(${movingNode.id}, ${toContainer.id}, [${topLeft.x}, ${topLeft.y}])")
        if (movingHere) {
            if (!containsItem) {
                newNode = newNode.addNode(movingNode)
            }
            newNode = newNode.attemptToPlace(movingNode, topLeft.x, topLeft.y, directions).let { it ->
                it.copy(
                    layout = it.layout?.copy(
                        children = it.layout.children
                            .map { it.moveElement(movingNode, toContainer, topLeft, directions) }
                    )
                )
            }
            println("Moved ${movingNode.id} to $id:")
            newNode.layout?.children?.forEach { child ->
                println("- ${child.id} ${child.left},${child.top} ${child.width}x${child.height}")
            }
        } else {
            newNode = newNode.copy(
                layout = newNode.layout.copy(
                    children = newNode.layout.children
                        .filter { it != movingNode }
                        .map { it.moveElement(movingNode, toContainer, topLeft, directions) }
                )
            )
        }
        return newNode /*.copy(
            layout = layout.copy(
                children = layout.children.map {
                    it.moveElement(node, toContainer, cell)
                }
            )
        )*/
    }

    fun addNode(movingNode: Node): Node =
        copy(
            layout = layout?.copy(
                children = layout.children + listOf(movingNode)
            )
        )
}

/**
 * Sort layout items by row ascending then column ascending.
 */
fun List<Node>.sortLayoutItemsByRowCol(reverse: Boolean = false): List<Node> =
    sortedWith { a, b ->
        when {
            a.top == b.top && a.left == b.left -> 0
            a.top == b.top && a.left > b.left -> 1
            if (reverse) b.top > a.top else a.top > b.top -> 1
            else -> -1
        }
    }

/**
 * Sort layout items by column ascending then row ascending.
 */
fun List<Node>.sortLayoutItemsByColRow(reverse: Boolean = false): List<Node> =
    sortedWith { a, b ->
        when {
            a.left == b.left && a.top == b.top -> 0
            a.left == b.left && a.top > b.top -> 1
            if (reverse) a.left > b.left else b.left > a.left -> 1
            else -> -1
        }
    }

data class LayoutItemSize(
    val width: Int,
    val height: Int
)

enum class Direction(
    val xIncr: Int, val yIncr: Int
) {
    North(0, -1) {
        override val opposite: Direction get() = South

        override fun sort(collisions: List<Node>): List<Node> =
            collisions.sortLayoutItemsByRowCol(reverse = true)
    },

    South(0, 1) {
        override val opposite: Direction get() = North

        override fun sort(collisions: List<Node>): List<Node> =
            collisions.sortLayoutItemsByRowCol()

    },

    East(1, 0) {
        override val opposite: Direction get() = West

        override fun sort(collisions: List<Node>): List<Node> =
            collisions.sortLayoutItemsByRowCol()

    },

    West(-1, 0) {
        override val opposite: Direction get() = East

        override fun sort(collisions: List<Node>): List<Node> =
            collisions.sortLayoutItemsByRowCol(reverse = true)

    };

    val isHorizontal get() = xIncr != 0
    val isVertical get() = yIncr != 0
    abstract val opposite: Direction

    abstract fun sort(collisions: List<Node>): List<Node>

    companion object {
        fun rankedPushOptions(v: Vector2I): Array<Direction>
                = rankedPushOptions(v.x, v.y)

        /** Returns the direction the node is coming from. */
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

data class Layout(
    val columns: Int,
    val rows: Int,
    val children: List<Node>
)