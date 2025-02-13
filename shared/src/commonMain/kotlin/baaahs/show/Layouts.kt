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
    @Transient
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
}

class OutOfBoundsException(message: String? = null) : ImpossibleLayoutException(message)
open class ImpossibleLayoutException(message: String? = null) : Exception(message)
class NoChangesException(message: String? = null) : Exception(message)