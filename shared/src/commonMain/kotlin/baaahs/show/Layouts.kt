package baaahs.show

import baaahs.camelize
import baaahs.getBang
import baaahs.replaceAll
import baaahs.show.live.*
import baaahs.show.mutable.*
import baaahs.ui.gridlayout.Direction
import baaahs.util.Logger
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Layouts(
    val panels: Map<String, Panel> = emptyMap(),
    val formats: Map<String, Layout> = emptyMap()
) {
    fun open(openContext: OpenContext): OpenLayouts =
        OpenLayouts(panels, formats.mapValues { (_, format) -> format.open(openContext) })

    fun findLayout(id: String): Layout = formats.getBang(id, "formats")
}

@Serializable
data class Panel(val title: String) {
    fun suggestId(): String = title.camelize()
}

@Serializable
data class Layout(
    val mediaQuery: String?,
    val tabs: List<Tab>
) {
    fun open(openContext: OpenContext): OpenLayout =
        OpenLayout(mediaQuery, tabs.map { it.open(openContext) })

    fun findTab(title: String): Tab {
        return tabs.find { it.title == title }
            ?: error("No tab with title \"$title\" found in [${tabs.joinToString(", ") { it.title }}]")
    }
}

@Polymorphic
sealed interface Tab {
    val title: String

    fun edit(panels: Map<String, MutablePanel>, mutableShow: MutableShow): MutableTab
    fun open(openContext: OpenContext): OpenTab
}

@Serializable @SerialName("Legacy")
data class LegacyTab(
    override val title: String,
    val columns: List<String>,
    val rows: List<String>,
    val areas: List<String>
) : Tab, OpenTab {
    override fun edit(panels: Map<String, MutablePanel>, mutableShow: MutableShow): MutableTab =
        MutableLegacyTab(this, panels)

    override fun open(openContext: OpenContext): OpenTab =
        this

    override fun addTo(builder: ActivePatchSet.Builder, depth: Int) {
        builder.show.controlLayout.forEach { (panel, openControls) ->
            openControls.forEach { openControl -> openControl.legacyAddTo(builder, panel, depth + 1) }
        }
    }
}

@Serializable @SerialName("Grid")
data class GridTab(
    override val title: String,
    override val columns: Int,
    override val rows: Int,
    override val items: List<GridItem> = emptyList()
) : Tab, IGridLayout {
    override fun edit(panels: Map<String, MutablePanel>, mutableShow: MutableShow): MutableGridTab =
        MutableGridTab(this, mutableShow)

    override fun open(openContext: OpenContext): OpenGridTab =
        OpenGridTab(this, title, columns, rows, items.map { it.open(openContext) })

    override fun updatedLayout(columns: Int, rows: Int, items: List<GridItem>): IGridLayout =
        GridTab(title, columns, rows, items)
}

@Serializable
data class GridLayout(
    override val columns: Int,
    override val rows: Int,
    val matchParent: Boolean = false,
    override val items: List<GridItem> = emptyList()
) : IGridLayout {
    fun edit(mutableShow: MutableShow): MutableGridLayout =
        MutableGridLayout(this, mutableShow)

    override fun open(openContext: OpenContext): OpenGridLayout =
        OpenGridLayout(this, columns, rows, matchParent, items.map { it.open(openContext) })

    override fun updatedLayout(columns: Int, rows: Int, items: List<GridItem>): IGridLayout =
        GridLayout(columns, rows, matchParent, items)
}

interface IGridLayout {
    val columns: Int
    val rows: Int
    val items: List<GridItem>

    fun open(openContext: OpenContext): OpenIGridLayout

    fun visit(visitor: IGridLayout.(GridItem) -> Unit) {
        items.forEach {
            visitor(it)
            it.layout?.visit(visitor)
        }
    }

    fun visit(parent: GridItem?, visitor: (item: GridItem, parent: GridItem?) -> Unit) {
        items.forEach {
            visitor(it, parent)
            it.layout?.visit(it, visitor)
        }
    }

    fun updatedLayout(columns: Int, rows: Int, items: List<GridItem>): IGridLayout

    fun updatedLayout(block: (GridItem) -> GridItem): IGridLayout =
        updatedLayout(columns, rows, items.map { block(it) })

    private fun updatedLayout(updateItem: GridItem): IGridLayout =
        updatedLayout(columns, rows, items.map {
            if (it.controlId == updateItem.controlId) updateItem else it
        })

    fun resetMovedFlag(): IGridLayout =
        updatedLayout(columns, rows, items.map { it.copy(moved = false) })

    /**
     * Given a layout, make sure all elements fit within its bounds.
     *
     * Modifies layout items.
     *
     * @param  {Array} layout Layout array.
     * @param  {Number} cols Number of columns.
     */
    fun correctBounds(): IGridLayout {
        val collidesWith = ArrayList<GridItem>()
        val newLayoutItems = ArrayList(items)
        newLayoutItems.replaceAll { l ->
            var newItem = l

            // Overflows right
            if (newItem.column + newItem.width > columns) newItem = newItem.copy(width = columns - newItem.width)
            // Overflows left
            if (newItem.column < 0) {
                newItem = newItem.copy(column = 0, width = columns)
            }
            collidesWith.add(newItem)
            newItem
        }
        return updatedLayout(columns, rows, newLayoutItems)
    }

    /**
     * Get a layout item by ID. Used so we can override later on if necessary.
     *
     * @param  {Array}  layout Layout array.
     * @param  {String} id     ID
     * @return {LayoutItem}    Item at ID.
     */
    fun find(id: String): GridItem? {
        items.forEach {
            if (it.controlId == id) return it
            val found = it.layout?.find(id)
            if (found != null) return found
        }
        return null
    }

    private fun anyCollisions(layoutItem: GridItem): Boolean =
        items.any { it.collidesWith(layoutItem) }

    fun moveElement(itemId: String, x: Int, y: Int): IGridLayout =
        moveElement(find(itemId)!!, x, y)

    fun findCollisions(layoutItem: GridItem): List<GridItem> =
        items.filter { l -> l.collidesWith(layoutItem) }

    /**
     * Move an element. Responsible for doing cascading movements of other elements.
     *
     * @param item Element to move.
     * @param x X position in grid units.
     * @param y Y position in grid units.
     * @return A new layout with moved layout items.
     * @throws ImpossibleLayoutException if the move isn't possible because of collisions or constraints.
     */
    fun moveElement(item: GridItem, x: Int, y: Int): IGridLayout {
        for (direction in Direction.rankedPushOptions(x - item.column, y - item.row)) {
            try {
                return moveElementInternal(item, x, y, direction)
            } catch (e: ImpossibleLayoutException) {
                e.printStackTrace()
                // Try again.
            }
        }
        throw ImpossibleLayoutException("Item ${item.controlId} can't be moved to $x,$y.")
    }

    private fun moveElementInternal(l: GridItem, x: Int, y: Int, pushDirection: Direction): IGridLayout {
        // Short-circuit if nothing to do.
        if (l.row == y && l.column == x) return this

        logger.debug {
            "Moving element ${l.controlId} to [$x,$y] from [${l.column},${l.row}]"
        }

        val movedItem = l.movedTo(x, y)
        return fitElement(movedItem, pushDirection)
    }

    private fun fitElement(changedItem: GridItem, pushDirection: Direction): IGridLayout {
        if (outOfBounds(changedItem))
            throw ImpossibleLayoutException("out of bounds, $pushDirection")

        var updatedLayout = updatedLayout(changedItem)
        val collisions = findCollisions(changedItem)

        // If it collides with anything, move it (recursively).
        if (collisions.isNotEmpty()) {
            // When doing this comparison, we have to sort the items we compare with
            // to ensure, in the case of multiple collisions, that we're getting the
            // nearest collision.
//            for (collision in pushDirection.sort(collisions)) {
//                logger.info {
//                    "Resolving collision between ${changedItem.controlId} at [${changedItem.column},${changedItem.row}] and ${collision.controlId} at [${collision.column},${collision.row}]"
//                }
//
//                // Short circuit so we can't infinitely loop
//                if (collision.moved) throw ImpossibleLayoutException("collision ${collision.id} $pushDirection")
//
//                updatedLayout =
//                    updatedLayout.pushCollidingElement(changedItem, collision, pushDirection)
//            }
        }

        return updatedLayout
    }

    fun resizeElement(itemId: String, w: Int, h: Int) =
        resizeElement(find(itemId)!!, w,h )


    /**
     * Resize an element. Responsible for doing cascading movements of other elements.
     *
     * @param item Element to move.
     * @param width X position in grid units.
     * @param y Y position in grid units.
     * @return A new layout with resized and possibly moved layout items.
     * @throws ImpossibleLayoutException if the move isn't possible because of collisions or constraints.
     */
    fun resizeElement(item: GridItem, width: Int, height: Int): IGridLayout {
        val resizedItem = item.copy(width = width, height = height)
        for (direction in arrayOf(Direction.East, Direction.South)) {
            try {
                return fitElement(resizedItem, direction)
            } catch (e: ImpossibleLayoutException) {
                // Try again.
            }
        }
        throw ImpossibleLayoutException()
    }

    private fun outOfBounds(movedItem: GridItem) =
        movedItem.column < 0 || movedItem.row < 0 || movedItem.right > columns || movedItem.bottom > rows

    fun removeElement(id: String): IGridLayout =
        updatedLayout(columns, rows, items.filter { it.controlId != id })

    /**
     * This is where the magic needs to happen - given a collision, move an element away from the collision.
     * We attempt to move it up if there's room, otherwise it goes below.
     *
     * @param  {Array} layout            Full layout to modify.
     * @param  {GridItem} collidesWith Layout item we're colliding with.
     * @param  {GridItem} itemToMove   Layout item we're moving.
     */
    private fun pushCollidingElement(
        collidesWith: GridItem,
        itemToMove: GridItem,
        direction: Direction
    ): IGridLayout =
        moveElementInternal(
            itemToMove,
            itemToMove.column + direction.xIncr,
            itemToMove.row + direction.yIncr,
            direction
        )

    fun canonicalize(): IGridLayout =
        updatedLayout(
            columns, rows,
            items.sortedWith { a, b ->
                if (a.row > b.row ||
                    (a.row == b.row && a.column > b.column) ||
                    (a.row == b.row && a.column == b.column && a.controlId > b.controlId)
                ) 1 else -1
            }
        )

    companion object {
        private val logger = Logger<GridLayout>()
    }
}

@Serializable
data class GridItem(
    val controlId: String,
    val column: Int,
    val row: Int,
    val width: Int = 1,
    val height: Int = 1,
    val layout: GridLayout? = null,
    @Transient
    val moved: Boolean = false
) {
    val id = controlId
    val right: Int get() = column + width - 1
    val bottom: Int get() = row + height - 1

    fun open(openContext: OpenContext): OpenGridItem =
        OpenGridItem(
            this,
            openContext.getControl(controlId),
            column, row, width, height,
            layout?.open(openContext)
        )

    fun edit(mutableShow: MutableShow): MutableGridItem =
        MutableGridItem(this, mutableShow)

    fun movedTo(column: Int, row: Int) = copy(column = column, row = row)

    fun collidesWith(other: GridItem): Boolean {
        if (controlId === other.controlId) return false // same element
        if (column + width <= other.column) return false // this is left of other
        if (column >= other.column + other.width) return false // this is right of other
        if (row + height <= other.row) return false // this is above other
        if (row >= other.row + other.height) return false // this is below other
        return true // boxes overlap
    }
}

class ImpossibleLayoutException(message: String? = null) : Exception(message)
class NoChangesException(message: String? = null) : Exception(message)