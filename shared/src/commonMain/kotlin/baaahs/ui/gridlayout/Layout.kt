package baaahs.ui.gridlayout

import baaahs.replaceAll
import baaahs.util.Logger

data class Layout(
    val items: List<LayoutItem>,
    val cols: Int,
    val rows: Int
) {
    constructor() : this(emptyList(), 0, 0)
    constructor(cols: Int, rows: Int, vararg items: LayoutItem) : this(items.toList(), cols, rows)

    val size: Int get() = items.size

    operator fun get(index: Int): LayoutItem = items[index]

    /**
     * Return the bottom coordinate of the layout.
     *
     * @param  {Array} layout Layout array.
     * @return {Number}       Bottom coordinate.
     */
    fun bottom(): Int {
        var max = 0
        var bottomY: Int
        val len = items.size
        for (i in 0 until len) {
            bottomY = items[i].y + items[i].h
            if (bottomY > max) max = bottomY
        }
        return max
    }

    private fun updateLayout(updateItem: LayoutItem): Layout =
        Layout(items.map { if (it.i == updateItem.i) updateItem else it }, cols, rows)

    fun resetMovedFlag(): Layout =
        Layout(items.map { it.copy(moved = false) }, cols, rows)

    /**
     * Given a layout, make sure all elements fit within its bounds.
     *
     * Modifies layout items.
     *
     * @param  {Array} layout Layout array.
     * @param  {Number} cols Number of columns.
     */
    fun correctBounds(): Layout {
        val collidesWith = getStatics().toMutableList()
        val newLayoutItems = ArrayList(items)
        newLayoutItems.replaceAll { l ->
            var newItem = l

            // Overflows right
            if (newItem.x + newItem.w > cols) newItem = newItem.copy(x = cols - newItem.w)
            // Overflows left
            if (newItem.x < 0) {
                newItem = newItem.copy(x = 0, w = cols)
            }
            if (!newItem.isStatic)
                collidesWith.add(newItem)
            else {
                // If this is static and collides with other statics, we must move it down.
                // We have to do something nicer than just letting them overlap.
                while (Layout(collidesWith, cols, rows).anyCollisions(newItem)) {
                    newItem = newItem.copy(y = newItem.y + 1)
                }
            }
            newItem
        }
        return Layout(newLayoutItems, cols, rows)
    }

    /**
     * Get a layout item by ID. Used so we can override later on if necessary.
     *
     * @param  {Array}  layout Layout array.
     * @param  {String} id     ID
     * @return {LayoutItem}    Item at ID.
     */
    fun find(id: String): LayoutItem? =
        items.firstOrNull { it.i == id }

    /**
     * Returns the first item this layout collides with.
     * It doesn't appear to matter which order we approach this from, although
     * perhaps that is the wrong thing to do.
     *
     * @param  {Object} layoutItem Layout item.
     * @return {Object|undefined}  A colliding layout item, or undefined.
     */
    private fun getFirstCollision(layoutItem: LayoutItem): LayoutItem? =
        items.firstOrNull { it.collidesWith(layoutItem) }

    private fun anyCollisions(layoutItem: LayoutItem): Boolean =
        items.any { it.collidesWith(layoutItem) }

    fun findCollisions(layoutItem: LayoutItem): List<LayoutItem> =
        items.filter { l -> l.collidesWith(layoutItem) }

    /**
     * Get all static elements.
     *
     * @return Array of static layout items.
     */
    private fun getStatics(): List<LayoutItem> =
        items.filter { l -> l.isStatic }

    fun moveElement(itemId: String, x: Int, y: Int): Layout =
        moveElement(find(itemId)!!, x, y)

    /**
     * Move an element. Responsible for doing cascading movements of other elements.
     *
     * @param item Element to move.
     * @param x X position in grid units.
     * @param y Y position in grid units.
     * @return A new layout with moved layout items.
     * @throws ImpossibleLayoutException if the move isn't possible because of collisions or constraints.
     */
    fun moveElement(item: LayoutItem, x: Int, y: Int): Layout {
        for (direction in Direction.rankedPushOptions(x - item.x, y - item.y)) {
            try {
                return moveElementInternal(item, x, y, direction)
            } catch (e: ImpossibleLayoutException) {
                // Try again.
            }
        }
        throw ImpossibleLayoutException()
    }

    private fun moveElementInternal(l: LayoutItem, x: Int, y: Int, pushDirection: Direction): Layout {
        // If this is static and not explicitly enabled as draggable,
        // no move is possible, so we can short-circuit this immediately.
        if (l.isStatic && !l.isDraggable) throw ImpossibleLayoutException()

        // Short-circuit if nothing to do.
        if (l.y == y && l.x == x) return this

        logger.debug {
            "Moving element ${l.i} to [$x,$y] from [${l.x},${l.y}]"
        }

        val movedItem = l.movedTo(x, y)
        return fitElement(movedItem, pushDirection)
    }

    private fun fitElement(changedItem: LayoutItem, pushDirection: Direction): Layout {
        if (outOfBounds(changedItem))
            throw ImpossibleLayoutException()

        var updatedLayout = updateLayout(changedItem)
        val collisions = findCollisions(changedItem)

        // If it collides with anything, move it (recursively).
        if (collisions.isNotEmpty()) {
            // When doing this comparison, we have to sort the items we compare with
            // to ensure, in the case of multiple collisions, that we're getting the
            // nearest collision.
            for (collision in pushDirection.sort(collisions)) {
                logger.info {
                    "Resolving collision between ${changedItem.i} at [${changedItem.x},${changedItem.y}] and ${collision.i} at [${collision.x},${collision.y}]"
                }

                // Short circuit so we can't infinitely loop
                if (collision.moved) throw ImpossibleLayoutException()

                updatedLayout =
                    updatedLayout.pushCollidingElement(changedItem, collision, pushDirection)
            }
        }

        return updatedLayout
    }

    fun resizeElement(itemId: String, w: Int, h: Int) =
        resizeElement(find(itemId)!!, w,h )


    /**
     * Resize an element. Responsible for doing cascading movements of other elements.
     *
     * @param item Element to move.
     * @param w X position in grid units.
     * @param y Y position in grid units.
     * @return A new layout with resized and possibly moved layout items.
     * @throws ImpossibleLayoutException if the move isn't possible because of collisions or constraints.
     */
    fun resizeElement(item: LayoutItem, w: Int, h: Int): Layout {
        val resizedItem = item.copy(w = w, h = h)
        for (direction in arrayOf(Direction.East, Direction.South)) {
            try {
                return fitElement(resizedItem, direction)
            } catch (e: ImpossibleLayoutException) {
                // Try again.
            }
        }
        throw ImpossibleLayoutException()
    }

    private fun outOfBounds(movedItem: LayoutItem) =
        movedItem.x < 0 || movedItem.y < 0 || movedItem.right >= cols || movedItem.bottom >= rows

    fun removeItem(id: String): Layout =
        Layout(items.filter { it.i != id }, cols, rows)

    /**
     * This is where the magic needs to happen - given a collision, move an element away from the collision.
     * We attempt to move it up if there's room, otherwise it goes below.
     *
     * @param  {Array} layout            Full layout to modify.
     * @param  {LayoutItem} collidesWith Layout item we're colliding with.
     * @param  {LayoutItem} itemToMove   Layout item we're moving.
     */
    private fun pushCollidingElement(
        collidesWith: LayoutItem,
        itemToMove: LayoutItem,
        direction: Direction
    ): Layout {
        // Don't move static items - we have to move the other element away.
        if (itemToMove.isStatic) {
            if (collidesWith.isStatic) throw ImpossibleLayoutException()

            return moveElementInternal(
                collidesWith,
                collidesWith.x + direction.xIncr,
                collidesWith.y + direction.yIncr,
                direction
            )
        }

        return moveElementInternal(
            itemToMove,
            itemToMove.x + direction.xIncr,
            itemToMove.y + direction.yIncr,
            direction
        )
    }

    fun canonicalize(): Layout =
        Layout(items.sortedWith { a, b ->
            if (a.y > b.y ||
                (a.y == b.y && a.x > b.x) ||
                (a.y == b.y && a.x == b.x && a.i > b.i)
            ) 1 else -1
        }, cols, rows)


    companion object {
        private val logger = Logger<Layout>()
    }
}