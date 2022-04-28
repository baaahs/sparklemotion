package baaahs.ui.gridlayout

import baaahs.util.Logger
import kotlin.math.max
import kotlin.math.min

data class Layout(val items: List<LayoutItem> = emptyList()) {
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

    private fun cloneLayout(): Layout =
        Layout(items.map { it.copy() })

    /**
     * Modify a layoutItem inside a layout. Returns a new Layout,
     * does not mutate. Carries over all other LayoutItems unmodified.
     */
    private fun modifyLayout(layoutItem: LayoutItem): Layout =
        Layout(items.map {
            if (layoutItem.i === it.i) layoutItem else it
        })

    /**
     * Modify a layout item, returning a new layout.
     * Does defensive clones to ensure the layout is not modified.
     */
    fun withLayoutItem(
        itemKey: String,
        cb: (LayoutItem) -> LayoutItem
    ): Pair<Layout, LayoutItem?> {
        var item = find(itemKey)
            ?: return this to null

        item = cb(item.copy()) // defensive clone then modify
        // FIXME could do this faster if we already knew the index
        return modifyLayout(item) to item
    }

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
    fun compact(compactType: CompactType, cols: Int): Layout {
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
            if (!l.isStatic) {
                l = compactItem(Layout(compareWith), l, compactType, cols, sorted)

                // Add to comparison array. We only collide with items before this one.
                // Statics are already in this array.
                compareWith.add(l)
            }

            // Add to output array to make sure they still come out in the right order.
            out[items.indexOf(sorted[i])] = l

            // Clear moved flag, if it exists.
            l.moved = false
        }

        return Layout(out.filterNotNull())
    }

    /**
     * Before moving item down, it will check if the movement will cause collisions and move those items down before.
     */
    private fun resolveCompactionCollision(
        item: LayoutItem,
        moveToCoord: Int,
        axis: Axis
    ) {
        val sizeProp = heightWidth[axis]!!
        item[axis] += 1
        val itemIndex = items.indexOfFirst { layoutItem -> layoutItem.i == item.i }

        // Go through each item we collide with.
        for (i in itemIndex + 1 until items.size) {
            val otherItem = items[i]
            // Ignore static items
            if (otherItem.isStatic) continue

            // Optimization: we can break early if we know we're past this el
            // We can do this b/c it's a sorted layout
            if (otherItem.y > item.y + item.h) break

            if (collides(item, otherItem)) {
                resolveCompactionCollision(
                    otherItem,
                    moveToCoord + sizeProp.invoke(item),
                    axis
                )
            }
        }

        item[axis] = moveToCoord
    }

    private val heightWidth = mapOf(
        Axis.x to { layoutItem: LayoutItem -> layoutItem.w },
        Axis.y to { layoutItem: LayoutItem -> layoutItem.h }
    )

    /**
     * Given a layout, make sure all elements fit within its bounds.
     *
     * Modifies layout items.
     *
     * @param  {Array} layout Layout array.
     * @param  {Number} cols Number of columns.
     */
    fun correctBounds(cols: Int): Layout {
        val collidesWith = getStatics().toMutableList()
        val len = items.size
        for (i in 0 until len) {
            val l = items[i]
            // Overflows right
            if (l.x + l.w > cols) l.x = cols - l.w
            // Overflows left
            if (l.x < 0) {
                l.x = 0
                l.w = cols
            }
            if (!l.isStatic) collidesWith.add(l)
            else {
                // If this is static and collides with other statics, we must move it down.
                // We have to do something nicer than just letting them overlap.
                while (Layout(collidesWith).getFirstCollision(l) != null) {
                    l.y++
                }
            }
        }
        return this
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
        items.firstOrNull { collides(it, layoutItem) }

    fun getAllCollisions(layoutItem: LayoutItem): Array<LayoutItem> =
        items.filter { l -> collides(l, layoutItem) }.toTypedArray()

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
        x: Int?,
        y: Int?,
        isUserAction: Boolean?,
        preventCollision: Boolean?,
        compactType: CompactType,
        cols: Int,
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

        // This is quite a bit faster than extending the object
        x?.let { l.x = it }
        y?.let { l.y = it }
        l.moved = true

        // If this collides with anything, move it.
        // When doing this comparison, we have to sort the items we compare with
        // to ensure, in the case of multiple collisions, that we're getting the
        // nearest collision.
        var sorted = sortLayoutItems(compactType)
        val movingUp =
            if (compactType == CompactType.vertical && y != null)
                oldY >= y
            else if (compactType == CompactType.horizontal && x != null)
                oldX >= x
            else false
        // $FlowIgnore acceptable modification of read-only array as it was recently cloned
        if (movingUp) sorted = Layout(sorted.items.reversed())
        val collisions = sorted.getAllCollisions(l)
        val hasCollisions = collisions.isNotEmpty()

        // We may have collisions. We can short-circuit if we've turned off collisions or
        // allowed overlap.
        if (hasCollisions && allowOverlap) {
            // Easy, we don't need to resolve collisions. But we *did* change the layout,
            // so clone it on the way out.
            return cloneLayout()
        } else if (hasCollisions && preventCollision == true) {
            // If we are preventing collision but not allowing overlap, we need to
            // revert the position of this element so it goes to where it came from, rather
            // than the user's desired location.
            logger.info { "Collision prevented on ${l.i}, reverting." }
            l.x = oldX
            l.y = oldY
            l.moved = false
            return this // did not change so don't clone
        }

        var updatedLayout = this
        // Move each item that collides away from this element.
        for (collision in collisions) {
            logger.info {
                "Resolving collision between ${l.i} at [${l.x},${l.y}] and ${collision.i} at [${collision.x},${collision.y}]"
            }

            // Short circuit so we can't infinite loop
            if (collision.moved) continue

            // Don't move static items - we have to move *this* element away
            if (collision.isStatic) {
                updatedLayout = moveElementAwayFromCollision(
                    collision,
                    l,
                    isUserAction,
                    compactType,
                    cols
                )
            } else {
                updatedLayout = moveElementAwayFromCollision(
                    l,
                    collision,
                    isUserAction,
                    compactType,
                    cols
                )
            }
        }

        return updatedLayout
    }

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
        isUserAction_: Boolean?,
        compactType: CompactType,
        cols: Int
    ): Layout {
        var isUserAction = isUserAction_
        val compactH = compactType == CompactType.horizontal
        // Compact vertically if not set to horizontal
        val compactV = compactType != CompactType.horizontal
        val preventCollision = collidesWith.isStatic // we're already colliding (not for static items)

        // If there is enough space above the collision to put this element, move it there.
        // We only do this on the main collision as this can get funky in cascades and cause
        // unwanted swapping behavior.
        if (isUserAction == true) {
            // Reset isUserAction flag because we're not in the main collision anymore.
            isUserAction = false

            // Make a mock item so we don't modify the item here, only modify in moveElement.
            val fakeItem = LayoutItem(
                if (compactH) max(collidesWith.x - itemToMove.w, 0) else itemToMove.x,
                if (compactV) max(collidesWith.y - itemToMove.h, 0) else itemToMove.y,
                itemToMove.w,
                itemToMove.h,
                i = "-1"
            )

            // No collision? If so, we can go up there; otherwise, we'll end up moving down as normal
            if (getFirstCollision(fakeItem) != null) {
                logger.debug {
                    "Doing reverse collision on ${itemToMove.i} up to [${fakeItem.x},${fakeItem.y}]."
                }
                return moveElement(
                    itemToMove,
                    if (compactH) fakeItem.x else null,
                    if (compactV) fakeItem.y else null,
                    isUserAction,
                    preventCollision,
                    compactType,
                    cols
                )
            }
        }

        return moveElement(
            itemToMove,
            if (compactH) itemToMove.x + 1 else null,
            if (compactV) itemToMove.y + 1 else null,
            isUserAction,
            preventCollision,
            compactType,
            cols
        )
    }

    /**
     * Compact an item in the layout.
     *
     * Modifies item.
     *
     */
    private fun compactItem(
        compareWith: Layout,
        l: LayoutItem,
        compactType: CompactType,
        cols: Int,
        fullLayout: Layout
    ): LayoutItem {
        val compactV = compactType == CompactType.vertical
        val compactH = compactType == CompactType.horizontal
        if (compactV) {
            // Bottom 'y' possible is the bottom of the layout.
            // This allows you to do nice stuff like specify {y: Infinity}
            // This is here because the layout must be sorted in order to get the correct bottom `y`.
            l.y = min(compareWith.bottom(), l.y)
            // Move the element up as far as it can go without colliding.
            while (l.y > 0 && compareWith.getFirstCollision(l) == null) {
                l.y--
            }
        } else if (compactH) {
            // Move the element left as far as it can go without colliding.
            while (l.x > 0 && compareWith.getFirstCollision(l) == null) {
                l.x--
            }
        }

        // Move it down, and keep moving it down if it's colliding.
        var collides: LayoutItem? = compareWith.getFirstCollision(l)
        while (collides != null) {
            if (compactH) {
                fullLayout.resolveCompactionCollision(l, collides.x + collides.w, Axis.x)
            } else {
                fullLayout.resolveCompactionCollision(l, collides.y + collides.h, Axis.y)
            }
            // Since we can't grow without bounds horizontally, if we've overflown, var's move it down and try again.
            if (compactH && l.x + l.w > cols) {
                l.x = cols - l.w
                l.y++
            }

            collides = compareWith.getFirstCollision(l)
        }

        // Ensure that there are no negative positions
        l.y = max(l.y, 0)
        l.x = max(l.x, 0)

        return l
    }

    /**
     * Get layout items sorted from top left to right and down.
     *
     * @return {Array} Array of layout objects.
     * @return {Array}        Layout, sorted static items first.
     */
    private fun sortLayoutItems(compactType: CompactType): Layout =
        when (compactType) {
            CompactType.horizontal -> sortLayoutItemsByColRow()
            CompactType.vertical -> sortLayoutItemsByRowCol()
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
        })

    /**
     * Sort layout items by column ascending then row ascending.
     *
     * Does not modify Layout.
     */
    private fun sortLayoutItemsByColRow(): Layout =
        Layout(items.sortedWith { a, b ->
            if (a.x > b.x || (a.x == b.x && a.y > b.y)) 1 else -1
        })

    /**
     * Given two layoutitems, check if they collide.
     */
    private fun collides(l1: LayoutItem, l2: LayoutItem): Boolean {
        if (l1.i === l2.i) return false // same element
        if (l1.x + l1.w <= l2.x) return false // l1 is left of l2
        if (l1.x >= l2.x + l2.w) return false // l1 is right of l2
        if (l1.y + l1.h <= l2.y) return false // l1 is above l2
        if (l1.y >= l2.y + l2.h) return false // l1 is below l2
        return true // boxes overlap
    }

    companion object {
        private val logger = Logger<Layout>()
    }
}