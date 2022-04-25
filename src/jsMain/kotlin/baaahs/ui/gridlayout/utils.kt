package baaahs.ui.gridlayout

import baaahs.util.Logger
import external.lodash.isEqual
import external.react_resizable.ResizeHandleAxis
import external.react_resizable.Size
import kotlinx.js.Object
import kotlinx.js.jso
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent
import react.ReactElement
import react.ReactNode
import react.dom.events.DragEventHandler
import react.isValidElement
import kotlin.math.max
import kotlin.math.min

external interface LayoutItem {
    var w: Int
    var h: Int
    var x: Int
    var y: Int
    var i: String
    var minW: Int?
    var minH: Int?
    var maxW: Int?
    var maxH: Int?
    var moved: Boolean?
    var static: Boolean?
    var isDraggable: Boolean?
    var isResizable: Boolean?
    var resizeHandles: List<ResizeHandleAxis>? // <"s" | "w" | "e" | "n" | "sw" | "nw" | "se" | "ne">?
    var isBounded: Boolean?
}

external interface LayoutItemPosition {
    var x: Int
    var y: Int
}

external interface LayoutItemSize {
    var w: Int
    var h: Int
}

external interface PlaceholderLayoutItem : LayoutItem {
    var placeholder: Boolean?
}

typealias Layout = List<LayoutItem>

external interface Position {
    var left: Int
    var top: Int
    var width: Int
    var height: Int
}

external interface PartialPosition {
    var left: Int
    var top: Int
}

external interface DroppingPosition {
    var left: Int
    var top: Int
    var e: MouseEvent
}

external interface GridDragEvent {
    var e: MouseEvent
    var node: HTMLElement
    var newPosition: PartialPosition
}

external interface GridResizeEvent {
    var e: MouseEvent
    var node: HTMLElement
    var size: Size
}

typealias ReactChildren = Array<ReactElement<*>>

enum class CompactType {
    horizontal,
    vertical,
    none
}

const val isProduction = false // process.env.NODE_ENV === "production";
const val DEBUG = true

/**
 * Return the bottom coordinate of the layout.
 *
 * @param  {Array} layout Layout array.
 * @return {Number}       Bottom coordinate.
 */
fun bottom(layout: Layout): Int {
    var max = 0
    var bottomY: Int
    val len = layout.size
    for (i in 0 until len) {
        bottomY = layout[i].y + layout[i].h
        if (bottomY > max) max = bottomY
    }
    return max
}

fun cloneLayout(layout: Layout): Layout =
    layout.map { cloneLayoutItem(it) }

// Modify a layoutItem inside a layout. Returns a new Layout,
// does not mutate. Carries over all other LayoutItems unmodified.
fun modifyLayout(layout: Layout, layoutItem: LayoutItem): Layout =
    layout.map {
        if (layoutItem.i === it.i) {
            layoutItem
        } else {
            it
        }
    }

// Function to be called to modify a layout item.
// Does defensive clones to ensure the layout is not modified.
fun withLayoutItem(
    layout: Layout,
    itemKey: String,
    cb: (LayoutItem) -> LayoutItem
): Pair<Layout, LayoutItem?> {
    var item = getLayoutItem(layout, itemKey)
        ?: return layout to null

    item = cb(cloneLayoutItem(item)) // defensive clone then modify
    // FIXME could do this faster if we already knew the index
    return modifyLayout(layout, item) to item
}

// Fast path to cloning, since this is monomorphic
fun cloneLayoutItem(layoutItem: LayoutItem): LayoutItem {
    return jso {
        w = layoutItem.w
        h = layoutItem.h
        x = layoutItem.x
        y = layoutItem.y
        i = layoutItem.i
        minW = layoutItem.minW
        maxW = layoutItem.maxW
        minH = layoutItem.minH
        maxH = layoutItem.maxH
        moved = layoutItem.moved
        static = layoutItem.static
        // These can be null/undefined
        isDraggable = layoutItem.isDraggable
        isResizable = layoutItem.isResizable
        resizeHandles = layoutItem.resizeHandles
        isBounded = layoutItem.isBounded
    }
}

/**
 * Comparing React `children` is a bit difficult. This is a good way to compare them.
 * This will catch differences in keys, order, and length.
 */
fun childrenEqual(a: ReactChildren?, b: ReactChildren?): Boolean {
    return isEqual(
        a?.map { c -> c.key },
        b?.map { c -> c.key }
    )
}

/**
 * See `fastRGLPropsEqual.js`.
 * We want this to run as fast as possible - it is called often - and to be
 * resilient to new props that we add. So rather than call lodash.isEqual,
 * which isn't suited to comparing props very well, we use this specialized
 * function in conjunction with preval to generate the fastest possible comparison
 * function, tuned for exactly our props.
 */
//typealias FastRGLPropsEqual = (Any, Any, Function<*>) -> Boolean
//val fastRGLPropsEqual: FastRGLPropsEqual = require("./fastRGLPropsEqual")

// Like the above, but a lot simpler.
fun fastPositionEqual(a: Position, b: Position): Boolean {
    return (
            a.left == b.left &&
                    a.top == b.top &&
                    a.width == b.width &&
                    a.height == b.height
            )
}

/**
 * Given two layoutitems, check if they collide.
 */
fun collides(l1: LayoutItem, l2: LayoutItem): Boolean {
    if (l1.i === l2.i) return false // same element
    if (l1.x + l1.w <= l2.x) return false // l1 is left of l2
    if (l1.x >= l2.x + l2.w) return false // l1 is right of l2
    if (l1.y + l1.h <= l2.y) return false // l1 is above l2
    if (l1.y >= l2.y + l2.h) return false // l1 is below l2
    return true // boxes overlap
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
fun compact(
    layout: Layout,
    compactType: CompactType,
    cols: Int
): Layout {
    // Statics go in the compareWith array right away so items flow around them.
    val compareWith = getStatics(layout).toMutableList()
    // We go through the items by row and column.
    val sorted = sortLayoutItems(layout, compactType)
    // Holding for new items.
    val out = MutableList<LayoutItem?>(layout.size) { null }

    val len = sorted.size
    for (i in 0 until len) {
        var l = cloneLayoutItem(sorted[i])

        // Don't move static elements
        if (l.static != true) {
            l = compactItem(compareWith, l, compactType, cols, sorted)

            // Add to comparison array. We only collide with items before this one.
            // Statics are already in this array.
            compareWith.add(l)
        }

        // Add to output array to make sure they still come out in the right order.
        out[layout.indexOf(sorted[i])] = l

        // Clear moved flag, if it exists.
        l.moved = false
    }

    return out.filterNotNull()
}

val heightWidth = mapOf(
    Axis.x to { layoutItem: LayoutItem -> layoutItem.w },
    Axis.y to { layoutItem: LayoutItem -> layoutItem.h }
)

operator fun LayoutItem.get(axis: Axis) = axis[this]
operator fun LayoutItem.set(axis: Axis, value: Int) { axis[this] = value }

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

/**
 * Before moving item down, it will check if the movement will cause collisions and move those items down before.
 */
fun resolveCompactionCollision(
    layout: Layout,
    item: LayoutItem,
    moveToCoord: Int,
    axis: Axis
) {
    val sizeProp = heightWidth[axis]!!
    item[axis] += 1
    val itemIndex = layout.indexOfFirst { layoutItem -> layoutItem.i == item.i }

    // Go through each item we collide with.
    for (i in itemIndex + 1 until layout.size) {
        val otherItem = layout[i]
        // Ignore static items
        if (otherItem.static == true) continue

        // Optimization: we can break early if we know we're past this el
        // We can do this b/c it's a sorted layout
        if (otherItem.y > item.y + item.h) break

        if (collides(item, otherItem)) {
            resolveCompactionCollision(
                layout,
                otherItem,
                moveToCoord + sizeProp.invoke(item),
                axis
            )
        }
    }

    item[axis] = moveToCoord
}

/**
 * Compact an item in the layout.
 *
 * Modifies item.
 *
 */
fun compactItem(
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
        l.y = min(bottom(compareWith), l.y)
        // Move the element up as far as it can go without colliding.
        while (l.y > 0 && getFirstCollision(compareWith, l) == null) {
            l.y--
        }
    } else if (compactH) {
        // Move the element left as far as it can go without colliding.
        while (l.x > 0 && getFirstCollision(compareWith, l) == null) {
            l.x--
        }
    }

    // Move it down, and keep moving it down if it's colliding.
    var collides: LayoutItem? = getFirstCollision(compareWith, l)
    while (collides != null) {
        if (compactH) {
            resolveCompactionCollision(fullLayout, l, collides.x + collides.w, Axis.x)
        } else {
            resolveCompactionCollision(fullLayout, l, collides.y + collides.h, Axis.y)
        }
        // Since we can't grow without bounds horizontally, if we've overflown, var's move it down and try again.
        if (compactH && l.x + l.w > cols) {
            l.x = cols - l.w
            l.y++
        }
        
        collides = getFirstCollision(compareWith, l)
    }

    // Ensure that there are no negative positions
    l.y = max(l.y, 0)
    l.x = max(l.x, 0)

    return l
}

/**
 * Given a layout, make sure all elements fit within its bounds.
 *
 * Modifies layout items.
 *
 * @param  {Array} layout Layout array.
 * @param  {Number} bounds Number of columns.
 */
fun correctBounds(
    layout: Layout,
    bounds: PositionParams // { cols: Int }
): Layout {
    val collidesWith = getStatics(layout).toMutableList()
    val len = layout.size
    for (i in 0 until len) {
        val l = layout[i]
        // Overflows right
        if (l.x + l.w > bounds.cols) l.x = bounds.cols - l.w
        // Overflows left
        if (l.x < 0) {
            l.x = 0
            l.w = bounds.cols
        }
        if (l.static != true) collidesWith.add(l)
        else {
            // If this is static and collides with other statics, we must move it down.
            // We have to do something nicer than just letting them overlap.
            while (getFirstCollision(collidesWith, l) != null) {
                l.y++
            }
        }
    }
    return layout
}

/**
 * Get a layout item by ID. Used so we can override later on if necessary.
 *
 * @param  {Array}  layout Layout array.
 * @param  {String} id     ID
 * @return {LayoutItem}    Item at ID.
 */
fun getLayoutItem(layout: Layout, id: String): LayoutItem? =
    layout.firstOrNull { it.i == id }

/**
 * Returns the first item this layout collides with.
 * It doesn't appear to matter which order we approach this from, although
 * perhaps that is the wrong thing to do.
 *
 * @param  {Object} layoutItem Layout item.
 * @return {Object|undefined}  A colliding layout item, or undefined.
 */
fun getFirstCollision(layout: Layout, layoutItem: LayoutItem): LayoutItem? =
    layout.firstOrNull { collides(it, layoutItem) }

fun getAllCollisions(layout: Layout, layoutItem: LayoutItem): Array<LayoutItem> =
    layout.filter { l -> collides(l, layoutItem) }.toTypedArray()

/**
 * Get all static elements.
 * @param  {Array} layout Array of layout objects.
 * @return {Array}        Array of static layout items..
 */
fun getStatics(layout: Layout): List<LayoutItem> =
    layout.filter { l -> l.static == true }

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
    layout_: Layout,
    l: LayoutItem,
    x: Int?,
    y: Int?,
    isUserAction: Boolean?,
    preventCollision: Boolean?,
    compactType: CompactType,
    cols: Int,
    allowOverlap: Boolean = false
): Layout {
    var layout = layout_

    // If this is static and not explicitly enabled as draggable,
    // no move is possible, so we can short-circuit this immediately.
    if (l.static == true && !(l.isDraggable == true)) return layout

    // Short-circuit if nothing to do.
    if (l.y == y && l.x == x) return layout

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
    var sorted = sortLayoutItems(layout, compactType)
    val movingUp =
        if (compactType == CompactType.vertical && y != null)
            oldY >= y
        else if (compactType == CompactType.horizontal && x != null)
            oldX >= x
        else false
    // $FlowIgnore acceptable modification of read-only array as it was recently cloned
    if (movingUp) sorted = sorted.reversed()
    val collisions = getAllCollisions(sorted, l)
    val hasCollisions = collisions.size > 0

    // We may have collisions. We can short-circuit if we've turned off collisions or
    // allowed overlap.
    if (hasCollisions && allowOverlap) {
        // Easy, we don't need to resolve collisions. But we *did* change the layout,
        // so clone it on the way out.
        return cloneLayout(layout)
    } else if (hasCollisions && preventCollision == true) {
        // If we are preventing collision but not allowing overlap, we need to
        // revert the position of this element so it goes to where it came from, rather
        // than the user's desired location.
        log("Collision prevented on ${l.i}, reverting.")
        l.x = oldX
        l.y = oldY
        l.moved = false
        return layout // did not change so don't clone
    }

    // Move each item that collides away from this element.
    for (i in 0 until collisions.size) {
        val collision = collisions[i]
        log(
            "Resolving collision between ${l.i} at [${l.x},${l.y}] and ${collision.i} at [${collision.x},${collision.y}]"
        )

        // Short circuit so we can't infinite loop
        if (collision.moved == true) continue

        // Don't move static items - we have to move *this* element away
        if (collision.static == true) {
            layout = moveElementAwayFromCollision(
                layout,
                collision,
                l,
                isUserAction,
                compactType,
                cols
            )
        } else {
            layout = moveElementAwayFromCollision(
                layout,
                l,
                collision,
                isUserAction,
                compactType,
                cols
            )
        }
    }

    return layout
}

/**
 * This is where the magic needs to happen - given a collision, move an element away from the collision.
 * We attempt to move it up if there's room, otherwise it goes below.
 *
 * @param  {Array} layout            Full layout to modify.
 * @param  {LayoutItem} collidesWith Layout item we're colliding with.
 * @param  {LayoutItem} itemToMove   Layout item we're moving.
 */
fun moveElementAwayFromCollision(
    layout: Layout,
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
    val preventCollision = collidesWith.static // we're already colliding (not for static items)

    // If there is enough space above the collision to put this element, move it there.
    // We only do this on the main collision as this can get funky in cascades and cause
    // unwanted swapping behavior.
    if (isUserAction == true) {
        // Reset isUserAction flag because we're not in the main collision anymore.
        isUserAction = false

        // Make a mock item so we don't modify the item here, only modify in moveElement.
        val fakeItem: LayoutItem = jso {
            x = if (compactH) max(collidesWith.x - itemToMove.w, 0) else itemToMove.x
            y = if (compactV) max(collidesWith.y - itemToMove.h, 0) else itemToMove.y
            w = itemToMove.w
            h = itemToMove.h
            i = "-1"
        }

        // No collision? If so, we can go up there; otherwise, we'll end up moving down as normal
        if (getFirstCollision(layout, fakeItem) != null) {
            logger.debug {
                "Doing reverse collision on ${itemToMove.i} up to [${fakeItem.x},${fakeItem.y}]."
            }
            return moveElement(
                layout,
                itemToMove,
                if (compactH) fakeItem.x else undefined,
                if (compactV) fakeItem.y else undefined,
                isUserAction,
                preventCollision,
                compactType,
                cols
            )
        }
    }

    return moveElement(
        layout,
        itemToMove,
        if (compactH) itemToMove.x + 1 else undefined,
        if (compactV) itemToMove.y + 1 else undefined,
        isUserAction,
        preventCollision,
        compactType,
        cols
    )
}

/**
 * Helper to convert a number to a percentage String.
 *
 * @param  {Number} num Any number
 * @return {String}     That number as a percentage.
 */
fun perc(num: Number): String {
    return "${num.toDouble() * 100}%"
}

fun setTransform(position: Position): dynamic {
    // Replace unitless items with px
    val translate = "translate(${position.left}px,${position.top}px)"
    return jso {
        transform = translate
        WebkitTransform = translate
        MozTransform = translate
        msTransform = translate
        OTransform = translate
        width = "${position.width}px"
        height = "${position.height}px"
        this.position = "absolute"
    }
}

fun setTopLeft(position: Position): dynamic {
    return jso {
        top = "${position.top}px"
        left = "${position.left}px"
        width = "${position.width}px"
        height = "${position.height}px"
        this.position = "absolute"
    }
}

/**
 * Get layout items sorted from top left to right and down.
 *
 * @return {Array} Array of layout objects.
 * @return {Array}        Layout, sorted static items first.
 */
fun sortLayoutItems(
    layout: Layout,
    compactType: CompactType
): Layout = when (compactType) {
    CompactType.horizontal -> sortLayoutItemsByColRow(layout)
    CompactType.vertical -> sortLayoutItemsByRowCol(layout)
    else -> layout
}

/**
 * Sort layout items by row ascending and column ascending.
 *
 * Does not modify Layout.
 */
fun sortLayoutItemsByRowCol(layout: Layout): Layout =
    layout.sortedWith { a, b ->
        if (a.y > b.y || (a.y == b.y && a.x > b.x)) {
            1
        } else if (a.y == b.y && a.x == b.x) {
            // Without this, we can get different sort results in IE vs. Chrome/FF
            0
        } else -1
    }

/**
 * Sort layout items by column ascending then row ascending.
 *
 * Does not modify Layout.
 */
fun sortLayoutItemsByColRow(layout: Layout): Layout =
    layout.sortedWith { a, b ->
        if (a.x > b.x || (a.x == b.x && a.y > b.y)) 1 else -1
    }

fun synchronizeLayoutWithChildren(
    initialLayout_: Layout?,
    children: ReactChildren,
    cols: Int,
    compactType: CompactType,
    allowOverlap: Boolean?
): Layout {
    val initialLayout = initialLayout_ ?: emptyList()

    // Generate one layout item per child.
    val layout = mutableListOf<LayoutItem>()

    children.forEach { child: ReactElement<*> ->
        // Child may not exist
        val key = child.key
            ?: return@forEach

        // Don't overwrite if it already exists.
        val exists = getLayoutItem(initialLayout, key)
        if (exists != null) {
            layout.add(cloneLayoutItem(exists))
        } else {
            if (!isProduction && child.props.asDynamic()._grid) {
                console.warn(
                    "`_grid` properties on children have been deprecated as of React 15.2. " +
                            "Please use `data-grid` or add your properties directly to the `layout`."
                )
            }
            val g = (child.props.asDynamic()["data-grid"] ?: child.props.asDynamic()._grid)
                .unsafeCast<LayoutItem?>()

            // Hey, this item has a data-grid property, use it.
            if (g != null) {
                if (!isProduction) {
                    validateLayout(listOf(g), "ReactGridLayout.children")
                }
                // FIXME clone not really necessary here
                layout.add(cloneLayoutItem(g.extend { this.i = child.key!! }))
            } else {
                // Nothing provided: ensure this is added to the bottom
                // FIXME clone not really necessary here
                layout.add(
                    cloneLayoutItem(jso {
                        w = 1
                        h = 1
                        x = 0
                        y = bottom(layout)
                        i = key
                    })
                )
            }
        }
    }

    // Correct the layout.
    val correctedLayout = correctBounds(layout, jso { this.cols = cols })
    return if (allowOverlap == true)
        correctedLayout
    else
        compact(correctedLayout, compactType, cols)
}

/**
 * Validate a layout. Throws errors.
 *
 * @param  {Array}  layout        Array of layout items.
 * @param  {String} [contextName] Context name for errors.
 * @throw  {Error}                Validation error.
 */
fun validateLayout(
    layout: Layout,
    contextName: String = "Layout"
) {
    val subProps = arrayOf("x", "y", "w", "h")
//    if (!Array.isArray(layout))
//        throw new Error(contextName + " must be an array!")

    layout.forEachIndexed { i, item ->
        for (subProp in subProps) {
            if (item.asDynamic()[subProp] is Number) {
                throw Error("ReactGridLayout: $contextName[$i].$subProp must be a number!")
            }
        }
    }
}

// Legacy support for verticalCompact: false
fun compactType(
    props: GridLayoutProps // ?{ verticalCompact: boolean, compactType: CompactType }
): CompactType {
    return /*props.verticalCompact === false ? null :*/ props.compactType ?: CompactType.vertical
}

fun log(vararg args: Any) {
    if (!DEBUG) return
    // eslint-disable-next-line no-console
    console.log(*args)
}

val noop: DragEventHandler<*> = { }

fun <T: Any> T.extend(block: T.() -> Unit): T =
    Object.assign(jso(), this, jso { block() })

fun ReactNode.asArray(): ReactChildren =
    if (this is Array<*>) {
        unsafeCast<ReactChildren>()
    } else if (isValidElement(this)) {
        arrayOf(this.unsafeCast<ReactElement<*>>())
    } else emptyArray()

private val logger = Logger<GridLayout>()

fun Element.isParentOf(other: Element): Boolean {
    var current: Element? = other
    while (current != null) {
        val parent = current.parentElement
        if (parent === this) return true
        current = parent
    }
    return false
}