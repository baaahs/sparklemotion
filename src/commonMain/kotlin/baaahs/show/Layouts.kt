package baaahs.show

import baaahs.camelize
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
}

@Serializable @SerialName("Grid")
data class GridTab(
    override val title: String,
    val items: List<GridItem>
) : Tab {
    override fun edit(panels: Map<String, MutablePanel>, mutableShow: MutableShow): MutableTab =
        MutableGridTab(this, mutableShow)

    override fun open(openContext: OpenContext): OpenTab =
        OpenGridTab(title, items.map { it.open(openContext) })
}

@Serializable
data class GridItem(
    val controlId: String,
    val column: Int,
    val row: Int,
    val width: Int = 1,
    val height: Int = 1,
    val isEmpty: Boolean = false
) {
    fun open(openContext: OpenContext): OpenGridItem =
        OpenGridItem(openContext.getControl(controlId), column, row, width, height, controlId)
}