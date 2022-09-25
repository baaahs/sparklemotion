package baaahs.ui.gridlayout

import baaahs.replaceAll
import baaahs.util.Logger
import kotlin.math.max
import kotlin.math.min

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

    /**
     * Given a layout, compact it. This involves going down each y coordinate and removing gaps
     * between items.
     *
     * Does not modify layout items (clones). Creates a new layout array.
     *
     * @param  {Array} layout Layout.
     * @param  {Boolean} verticalCompact Whether or not to compact the layout
     *   vertically.
     * @return {Array}       Compacted Layout.
     */
    fun compact(compactType: CompactType): Layout {
        // Statics go in the compareWith array right away so items flow around them.
        val compareWith = getStatics().toMutableList()
        // We go through the items by row and column.
        val sorted = sortLayoutItems(compactType)
        // Holding for new items.
        val out = MutableList<LayoutItem?>(items.size) { null }

        val len = sorted.size
        for (i in 0 until len) {
            var l = sorted[i].copy()

            // Don't move static elements
            if (!l.isStatic && compactType != CompactType.None) {
                l = compactItem(Layout(compareWith, cols, rows), l, compactType, cols, sorted)

                // Add to comparison array. We only collide with items before this one.
                // Statics are already in this array.
                compareWith.add(l)
            }

            // Add to output array to make sure they still come out in the right order.
            out[items.indexOf(sorted[i])] = l

            // Clear moved flag, if it exists.
            if (l.moved) {
                out.replaceAll { if (it?.i == l.i) l.copy(moved = false) else it }
            }
        }

        return Layout(out.filterNotNull(), cols, rows)
    }

    /**
     * Before moving item down, it will check if the movement will cause collisions and move those items down before.
     */
    private fun resolveCompactionCollision(
        item: LayoutItem,
        moveToCoord: Int,
        axis: Axis
    ): LayoutItem {
        var newItem = axis.incr(item)

        val sizeProp = heightWidth[axis]!!
        val itemIndex = items.indexOfFirst { layoutItem -> layoutItem.i == newItem.i }

        // Go through each item we collide with.
        for (i in itemIndex + 1 until items.size) {
            val otherItem = items[i]
            // Ignore static items
            if (otherItem.isStatic) continue

            // Optimization: we can break early if we know we're past this el
            // We can do this b/c it's a sorted layout
            if (otherItem.y > newItem.y + newItem.h) break

            if (newItem.collidesWith(otherItem)) {
                newItem =
                    resolveCompactionCollision(
                        otherItem,
                        moveToCoord + sizeProp.invoke(newItem),
                        axis
                    )
            }
        }

        return axis.set(newItem, moveToCoord)
    }

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

    fun getAllCollisions(layoutItem: LayoutItem): List<LayoutItem> =
        items.filter { l -> l.collidesWith(layoutItem) }

    /**
     * Get all static elements.
     * @param  {Array} layout Array of layout objects.
     * @return {Array}        Array of static layout items..
     */
    private fun getStatics(): List<LayoutItem> =
        items.filter { l -> l.isStatic }

    /**
     * Move an element. Responsible for doing cascading movements of other elements.
     *
     * Modifies layout items.
     *
     * @param  {Array}      layout            Full layout to modify.
     * @param  {LayoutItem} l                 element to move.
     * @param  {Number}     [x]               X position in grid units.
     * @param  {Number}     [y]               Y position in grid units.
     */
    fun moveElement(
        l: LayoutItem,
        x: Int,
        y: Int,
        preventCollision: Boolean,
        compactType: CompactType,
        allowOverlap: Boolean = false
    ): Layout =
        try {
            moveElementInternal(l, x, y, true, preventCollision, compactType, allowOverlap)
        } catch (e: OutOfBoundsException) { this }

    private fun moveElementInternal(
        l: LayoutItem,
        x: Int?,
        y: Int?,
        isDirectUserAction: Boolean,
        preventCollision: Boolean,
        compactType: CompactType,
        allowOverlap: Boolean = false
    ): Layout {
        // If this is static and not explicitly enabled as draggable,
        // no move is possible, so we can short-circuit this immediately.
        if (l.isStatic && !l.isDraggable) return this

        // Short-circuit if nothing to do.
        if (l.y == y && l.x == x) return this

        logger.debug {
            "Moving element ${l.i} to [$x,$y] from [${l.x},${l.y}]"
        }
        val oldX = l.x
        val oldY = l.y

        val newItem = l.movedTo(x, y)
        if (newItem.right + 1 > cols || newItem.bottom + 1 > rows)
            throw OutOfBoundsException()

        var updatedLayout = updateLayout(newItem)

        // If this collides with anything, move it.
        // When doing this comparison, we have to sort the items we compare with
        // to ensure, in the case of multiple collisions, that we're getting the
        // nearest collision.
        var sorted = updatedLayout.sortLayoutItems(compactType)
        val movingUp =
            if (compactType.isVertical && y != null)
                oldY >= y
            else if (compactType.isHorizontal && x != null)
                oldX >= x
            else false
        // $FlowIgnore acceptable modification of read-only array as it was recently cloned
        if (movingUp) sorted = Layout(sorted.items.reversed(), cols, rows)
        val collisions = sorted.getAllCollisions(newItem)
        val hasCollisions = collisions.isNotEmpty()

        // We may have collisions. We can short-circuit if we've turned off collisions or
        // allowed overlap.
        if (hasCollisions && allowOverlap) {
            // Easy, we don't need to resolve collisions. But we *did* change the layout,
            // so clone it on the way out.
            return updatedLayout
        } else if (hasCollisions && preventCollision) {
            // If we are preventing collision but not allowing overlap, we need to
            // revert the position of this element so it goes to where it came from, rather
            // than the user's desired location.
            logger.info { "Collision prevented on ${l.i}, reverting." }
            return this // did not change so don't clone
        }

        // Move each item that collides away from this element.
        for (collision in collisions) {
            logger.info {
                "Resolving collision between ${newItem.i} at [${newItem.x},${newItem.y}] and ${collision.i} at [${collision.x},${collision.y}]"
            }

            // Short circuit so we can't infinitely loop
            if (collision.moved) continue

            // Don't move static items - we have to move *this* element away
            updatedLayout =
                if (collision.isStatic) {
                    updatedLayout.moveElementAwayFromCollision(collision, newItem, isDirectUserAction, compactType)
                } else {
                    updatedLayout.moveElementAwayFromCollision(newItem, collision, isDirectUserAction, compactType)
                }
        }

        return updatedLayout
    }

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
    private fun moveElementAwayFromCollision(
        collidesWith: LayoutItem,
        itemToMove: LayoutItem,
        isDirectUserAction: Boolean,
        compactType: CompactType
    ): Layout {
        return try {
            moveElementInternal(
                itemToMove,
                if (compactType.isHorizontal) itemToMove.x + 1 else null,
                if (compactType.isVertical) itemToMove.y + 1 else null,
                false,
                collidesWith.isStatic, // we're already colliding (not for static items)
                compactType
            )
        } catch (e: OutOfBoundsException) {
            // If there is enough space above the collision to put this element, move it there.
            // We only do this on the main collision as this can get funky in cascades and cause
            // unwanted swapping behavior.
            if (isDirectUserAction) {
                tryMovingUp(collidesWith, itemToMove, compactType)
            } else throw e
        }
    }

    private fun tryMovingUp(
        collidesWith: LayoutItem,
        itemToMove: LayoutItem,
        compactType: CompactType
    ): Layout {
        // Make a mock item so we don't modify the item here, only modify in moveElement.
        val fakeItem = LayoutItem(
            if (compactType.isHorizontal) maxOf(collidesWith.x - itemToMove.w, 0) else itemToMove.x,
            if (compactType.isVertical) maxOf(collidesWith.y - itemToMove.h, 0) else itemToMove.y,
            itemToMove.w,
            itemToMove.h,
            i = "-1"
        )

        // No collision? If so, we can go up there; otherwise, we'll end up moving down as normal
        return if (!anyCollisions(fakeItem)) {
            logger.debug {
                "Doing reverse collision on ${itemToMove.i} up to [${fakeItem.x},${fakeItem.y}]."
            }
            moveElementInternal(
                itemToMove,
                if (compactType.isHorizontal) fakeItem.x else null,
                if (compactType.isVertical) fakeItem.y else null,
                false, // Reset isUserAction flag because we're not in the main collision anymore.
                collidesWith.isStatic, // we're already colliding (not for static items)
                compactType
            )
        } else this
    }

    /**
     * Compact an item in the layout.
     *
     * Returns modified item.
     *
     */
    private fun compactItem(
        compareWith: Layout,
        l: LayoutItem,
        compactType: CompactType,
        cols: Int,
        fullLayout: Layout
    ): LayoutItem {
        var newItem = l
        if (compactType.isVertical) {
            // Bottom 'y' possible is the bottom of the layout.
            // This allows you to do nice stuff like specify {y: Infinity}
            // This is here because the layout must be sorted in order to get the correct bottom `y`.
            newItem = newItem.copy(y = min(compareWith.bottom(), newItem.y))
            // Move the element up as far as it can go without colliding.
            while (newItem.y > 0 && !compareWith.anyCollisions(newItem)) {
                newItem = newItem.copy(y = newItem.y - 1)
            }
        } else if (compactType.isHorizontal) {
            // Move the element left as far as it can go without colliding.
            while (newItem.x > 0 && !compareWith.anyCollisions(newItem)) {
                newItem = newItem.copy(x = newItem.x - 1)
            }
        }

        // Move it down, and keep moving it down if it's colliding.
        var collides: LayoutItem? = compareWith.getFirstCollision(newItem)
        while (collides != null) {
            newItem = if (compactType.isHorizontal) {
                fullLayout.resolveCompactionCollision(newItem, collides.x + collides.w, Axis.x)
            } else if (compactType.isVertical) {
                fullLayout.resolveCompactionCollision(newItem, collides.y + collides.h, Axis.y)
            } else newItem
            // Since we can't grow without bounds horizontally, if we've overflown, let's move it down and try again.
            if (compactType.isHorizontal && newItem.x + newItem.w > cols) {
                newItem = newItem.copy(
                    x = cols - newItem.w,
                    y = newItem.y + 1
                )
            }

            collides = compareWith.getFirstCollision(newItem)
        }

        // Ensure that there are no negative positions
        return newItem.copy(
            x = max(newItem.x, 0),
            y = max(newItem.y, 0)
        )
    }

    /**
     * Get layout items sorted from top left to right and down.
     *
     * @return {Array} Array of layout objects.
     * @return {Array}        Layout, sorted static items first.
     */
    private fun sortLayoutItems(compactType: CompactType): Layout =
        when (compactType) {
            CompactType.Horizontal -> sortLayoutItemsByColRow()
            CompactType.Vertical -> sortLayoutItemsByRowCol()
            else -> this
        }

    /**
     * Sort layout items by row ascending and column ascending.
     *
     * Does not modify Layout.
     */
    private fun sortLayoutItemsByRowCol(): Layout =
        Layout(items.sortedWith { a, b ->
            if (a.y > b.y || (a.y == b.y && a.x > b.x)) {
                1
            } else if (a.y == b.y && a.x == b.x) {
                // Without this, we can get different sort results in IE vs. Chrome/FF
                0
            } else -1
        }, cols, rows)

    /**
     * Sort layout items by column ascending then row ascending.
     *
     * Does not modify Layout.
     */
    private fun sortLayoutItemsByColRow(): Layout =
        Layout(items.sortedWith { a, b ->
            if (a.x > b.x || (a.x == b.x && a.y > b.y)) 1 else -1
        }, cols, rows)

    fun canonicalize(): Layout =
        Layout(items.sortedWith { a, b ->
            if (a.y > b.y ||
                (a.y == b.y && a.x > b.x) ||
                (a.y == b.y && a.x == b.x && a.i > b.i)
            ) 1 else -1
        }, cols, rows)

    private class OutOfBoundsException : Exception()

    companion object {
        private val logger = Logger<Layout>()

        private val heightWidth = mapOf(
            Axis.x to { layoutItem: LayoutItem -> layoutItem.w },
            Axis.y to { layoutItem: LayoutItem -> layoutItem.h }
        )
    }
}