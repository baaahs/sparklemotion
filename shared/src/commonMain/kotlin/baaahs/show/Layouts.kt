package baaahs.show

import baaahs.camelize
import baaahs.getBang
import baaahs.show.live.*
import baaahs.show.mutable.*
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    override var columns: Int,
    override var rows: Int,
    override val items: List<GridItem> = emptyList()
) : Tab, IGridLayout {
    override fun edit(panels: Map<String, MutablePanel>, mutableShow: MutableShow): MutableGridTab =
        MutableGridTab(this, mutableShow)

    override fun open(openContext: OpenContext): OpenGridTab =
        OpenGridTab(title, columns, rows, items.map { it.open(openContext) })
}

@Serializable
data class GridLayout(
    override var columns: Int,
    override var rows: Int,
    var matchParent: Boolean = false,
    override val items: List<GridItem> = emptyList()
) : IGridLayout {
    fun edit(mutableShow: MutableShow): MutableGridLayout =
        MutableGridLayout(this, mutableShow)

    override fun open(openContext: OpenContext): OpenGridLayout =
        OpenGridLayout(columns, rows, matchParent, items.map { it.open(openContext) })
}

interface IGridLayout {
    var columns: Int
    var rows: Int
    val items: List<GridItem>

    fun open(openContext: OpenContext): OpenIGridLayout
}

@Serializable
data class GridItem(
    val controlId: String,
    val column: Int,
    val row: Int,
    val width: Int = 1,
    val height: Int = 1,
    val layout: GridLayout? = null
) {
    fun open(openContext: OpenContext): OpenGridItem =
        OpenGridItem(
            openContext.getControl(controlId),
            column, row, width, height,
            layout?.open(openContext)
        )

    fun edit(mutableShow: MutableShow): MutableGridItem =
        MutableGridItem(this, mutableShow)
}