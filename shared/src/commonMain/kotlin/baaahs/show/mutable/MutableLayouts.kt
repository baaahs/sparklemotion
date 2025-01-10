package baaahs.show.mutable

import baaahs.control.*
import baaahs.getBang
import baaahs.show.*
import baaahs.show.live.OpenGridItem

class MutableLayouts(
    val panels: MutableMap<String, MutablePanel> = mutableMapOf(),
    val formats: MutableMap<String, MutableLayout> = mutableMapOf()
) {
    constructor(baseLayouts: Layouts, mutableShow: MutableShow) : this(
        panels = baseLayouts.panels.mapValuesTo(HashMap()) { (_, v) -> MutablePanel(v) }
    ) {
        baseLayouts.formats.forEach { (id, v) ->
            formats[id] = MutableLayout(v, panels, mutableShow)
        }
    }

    fun findLayout(id: String) =
        formats.getOrPut(id) { MutableLayout(null) }

    fun editLayout(id: String, block: MutableLayout.() -> Unit): MutableLayouts {
        findLayout(id).apply(block)
        return this
    }

    fun findOrCreatePanel(title: String): MutablePanel {
        return panels.values.find { it.title == title }
            ?: run {
                val newPanel = Panel(title)
                return MutablePanel(newPanel).also {
                    panels[newPanel.suggestId()] = it
                }
            }
    }

    fun addPanel(panel: MutablePanel) {
        val newPanel = panel.build()
        panels[newPanel.suggestId()] = panel
    }

    fun copyFrom(layouts: MutableLayouts) {
        panels.clear()
        panels.putAll(layouts.panels)

        formats.clear()
        formats.putAll(layouts.formats)
    }

    fun build(showBuilder: ShowBuilder): Layouts {
        return Layouts(
            panels.map { (_, v) ->
                val builtPanel = v.build()
                showBuilder.idFor(builtPanel) to builtPanel
            }.toMap(),
            formats.mapValues { (_, v) -> v.build(showBuilder) }
        )
    }
}

class MutablePanel(
    var title: String,
    private val basePanel: Panel? = null
) {
    constructor(basePanel: Panel) : this(
        title = basePanel.title,
        basePanel = basePanel
    )

    fun isFor(panel: Panel) = basePanel == panel

    fun build(): Panel {
        return Panel(title)
    }
}

class MutableLayout(
    var mediaQuery: String?,
    var tabs: MutableList<MutableTab> = mutableListOf()
) {
    constructor(baseLayout: Layout, panels: Map<String, MutablePanel>, mutableShow: MutableShow) : this(
        mediaQuery = baseLayout.mediaQuery,
        tabs = baseLayout.tabs.mapTo(ArrayList()) { it.edit(panels, mutableShow) }
    )

    fun addTab(title: String, block: MutableGridTab.() -> Unit) {
        tabs.add(MutableGridTab(title).apply(block))
    }

    fun findTab(title: String) =
        tabs.find { it.title == title }
            ?: error("No tab with title \"$title\" found in [${tabs.joinToString(", ") { it.title }}]")

    fun editTab(title: String, block: MutableGridTab.() -> Unit) {
        val tab = findTab(title)
        (tab as? MutableGridTab?)?.apply(block)
    }

    fun build(showBuilder: ShowBuilder): Layout {
        return Layout(mediaQuery, tabs.map { it.build(showBuilder) })
    }

    fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        tabs.forEach { it.accept(visitor, log) }
    }
}

interface MutableTab {
    var title: String

    fun build(showBuilder: ShowBuilder): Tab
    fun accept(visitor: MutableShowVisitor, log: VisitationLog)
}

class MutableLegacyTab(
    override var title: String,
    val columns: MutableList<MutableLayoutDimen> = mutableListOf(),
    val rows: MutableList<MutableLayoutDimen> = mutableListOf(),
    val areas: MutableList<MutablePanel> = mutableListOf()
) : MutableTab {
    constructor(baseTab: LegacyTab, panels: Map<String, MutablePanel>) : this(
        title = baseTab.title,
        columns = baseTab.columns.mapTo(ArrayList()) { MutableLayoutDimen.decode(it) },
        rows = baseTab.rows.mapTo(ArrayList()) { MutableLayoutDimen.decode(it) },
        areas = baseTab.areas.mapTo(ArrayList()) { panels.getBang(it, "panel") }
    )

    override fun build(showBuilder: ShowBuilder): Tab =
        LegacyTab(
            title,
            columns.map { it.build() },
            rows.map { it.build() },
            areas.map { showBuilder.idFor(it.build()) }
        )

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        // No op.
    }

    fun appendColumn() {
        duplicateColumn(columns.size - 1)
    }

    fun duplicateColumn(index: Int) {
        val previousColCount = columns.size
        columns.add(index, columns[index].copy())

        val newAreas = mutableListOf<MutablePanel>()
        areas.forEachIndexed { i, area ->
            newAreas.add(area)
            if (i % previousColCount == index)
                newAreas.add(area)
        }
        areas.clear()
        areas.addAll(newAreas)
    }

    fun deleteColumn(index: Int) {
        val previousColCount = columns.size
        columns.removeAt(index)

        val newAreas = mutableListOf<MutablePanel>()
        areas.forEachIndexed { i, area ->
            if (i % previousColCount != index)
                newAreas.add(area)
        }
        areas.clear()
        areas.addAll(newAreas)
    }

    fun appendRow() {
        duplicateRow(rows.size - 1)
    }

    fun duplicateRow(index: Int) {
        val newAreas = mutableListOf<MutablePanel>()
        val colCount = columns.size
        repeat(rows.size) { row ->
            repeat(colCount) { column -> newAreas.add(areas[row * colCount + column]) }
            if (row == index)
                repeat(colCount) { column -> newAreas.add(areas[row * colCount + column]) }
        }
        areas.clear()
        areas.addAll(newAreas)
        rows.add(index, rows[index].copy())
    }

    fun deleteRow(index: Int) {
        val newAreas = mutableListOf<MutablePanel>()
        val colCount = columns.size
        repeat(rows.size) { row ->
            if (row != index)
                repeat(colCount) { column -> newAreas.add(areas[row * colCount + column]) }
        }
        areas.clear()
        areas.addAll(newAreas)
        rows.removeAt(index)
    }
}

class MutableGridTab(
    override var title: String,
    override var columns: Int = 12,
    override var rows: Int = 8,
    override val items: MutableList<MutableGridItem> = mutableListOf()
) : MutableTab, MutableIGridLayout {
    constructor(
        baseTab: GridTab,
        mutableShow: MutableShow
    ) : this(
        baseTab.title,
        baseTab.columns,
        baseTab.rows,
        baseTab.items.mapTo(ArrayList()) { MutableGridItem(it, mutableShow) }
    )

    override fun build(showBuilder: ShowBuilder): Tab =
        GridTab(title, columns, rows, items.map { it.build(showBuilder) })

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        items.forEach { it.accept(visitor, log) }
    }
}

class MutableGridLayout(
    override var columns: Int,
    override var rows: Int,
    var matchParent: Boolean = true,
    override val items: MutableList<MutableGridItem> = mutableListOf()
) : MutableIGridLayout {
    constructor(baseGridLayout: GridLayout, mutableShow: MutableShow) : this(
        baseGridLayout.columns,
        baseGridLayout.rows,
        baseGridLayout.matchParent,
        baseGridLayout.items.mapTo(ArrayList()) { it.edit(mutableShow) }
    )

    fun build(showBuilder: ShowBuilder): GridLayout =
        GridLayout(columns, rows, matchParent, items.map { it.build(showBuilder) })

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        items.forEach { it.accept(visitor, log) }
    }

}

class MutableGridItem(
    var control: MutableControl,
    var column: Int,
    var row: Int,
    var width: Int,
    var height: Int,
    val layout: MutableGridLayout? = null
) {
    constructor(baseGridItem: GridItem, mutableShow: MutableShow) : this(
        mutableShow.findControl(baseGridItem.controlId),
        baseGridItem.column, baseGridItem.row,
        baseGridItem.width, baseGridItem.height,
        baseGridItem.layout?.edit(mutableShow)
    )

    fun build(showBuilder: ShowBuilder) =
        GridItem(
            showBuilder.idFor(control.build(showBuilder)),
            column, row, width, height,
            layout?.build(showBuilder)
        )

    fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        control.accept(visitor, log)
        layout?.accept(visitor, log)
    }
}

interface MutableILayout {
    fun accept(visitor: MutableShowVisitor, log: VisitationLog)
}

interface MutableIGridLayout : MutableILayout {
    var columns: Int
    var rows: Int
    val items: MutableList<MutableGridItem>

    fun addControl(
        control: MutableControl, column: Int, row: Int, width: Int = 1, height: Int = 1,
        layout: (MutableGridLayout.() -> Unit)? = null
    ) {
        items.add(MutableGridItem(control, column, row, width, height, layout?.let {
            MutableGridLayout(width, height, true).apply(it)
        }))
    }

    fun addButton(
        title: String, column: Int, row: Int, width: Int = 1, height: Int = 1, mutableShow: MutableShow,
        block: MutableButtonControl.() -> Unit
    ) {
        addControl(
            MutableButtonControl(ButtonControl(title), mutableShow).apply(block),
            column, row, width, height
        )
    }

    fun addButtonGroup(
        title: String, column: Int, row: Int, width: Int = 1, height: Int = 1, mutableShow: MutableShow,
        layout: (MutableGridLayout.() -> Unit)? = null
    ) {
        addControl(
            MutableButtonGroupControl(title, mutableShow = mutableShow),
            column, row, width, height, layout
        )
    }

    fun addVisualizer(column: Int, row: Int, width: Int = 1, height: Int = 1) {
        addControl(
            MutableVisualizerControl(),
            column, row, width, height
        )
    }

    fun addVacuity(column: Int, row: Int, width: Int = 1, height: Int = 1) {
        addControl(
            MutableVacuityControl("Vacuity"),
            column, row, width, height
        )
    }

    fun find(title: String): MutableGridItem = items.find { it.control.title == title }
        ?: error("No control with title \"$title\" among [${items.joinToString(", ") { it.control.title }}]")

    fun find(control: MutableControl): MutableGridItem = items.find { it.control == control }
        ?: error("No control \"${control.title}\" among [${items.joinToString(", ") { it.control.title }}]")

    fun visit(visitor: (MutableGridItem) -> Unit) {
        items.forEach {
            visitor(it)
            it.layout?.visit(visitor)
        }
    }

    fun visitLayouts(
        parent: MutableGridItem?,
        visitor: (layout: MutableIGridLayout, parent: MutableGridItem?) -> Unit
    ) {
        visitor(this, parent)
        ArrayList(items).forEach { it.layout?.visitLayouts(it, visitor) }
    }

    /**
     * Apply layout from [updatedGridLayout] in place.
     *
     * We assume that `updatedGridLayout` is a valid [IGridLayout], and that
     * all controls within it are also in this [MutableIGridLayout].
     */
    fun applyChanges(
        updatedGridLayout: IGridLayout
    ) {
        val allUpdatedControlsByParent = buildMap {
            updatedGridLayout.visit(null) { item, parent ->
                println("item ${item.controlId} is in ${parent?.controlId}")
                getOrPut(parent?.controlId) { mutableListOf<GridItem>() }
                    .add(item)
            }
        }

        val allMutableItemsById = buildMap {
            visitLayouts(null) { layout, parent ->
                layout.items.forEach {
                    put(it.control.asBuiltId, it)
                }
            }
        }
        visitLayouts(null) { layout, parent ->
            println("Modifying ${parent?.control?.asBuiltId ?: "Root grid"}:")
            println("Items were: ${layout.items.joinToString { it.control.asBuiltId ?: "?" }}:")
            layout.items.clear()
            val updatedItems = allUpdatedControlsByParent[parent?.control?.asBuiltId]
            updatedItems?.forEach { updatedItem ->
                val mutableItem = allMutableItemsById[updatedItem.controlId]
                    ?: error("No control with id ${updatedItem.controlId}.")
                mutableItem.apply {
                    column = updatedItem.column
                    row = updatedItem.row
                    width = updatedItem.width
                    height = updatedItem.height
                }
                layout.items.add(mutableItem)
            }
            println("Items now:  ${layout.items.joinToString { it.control.asBuiltId ?: "?" }}:")
        }
    }

    fun applyChanges(
        originalItems: List<OpenGridItem>,
        newLayout: IGridLayout,
        mutableShow: MutableShow
    ) {
        val oldItems = ArrayList(this.items)
        this.items.clear()
        newLayout.items.forEach { newLayoutItem ->
            val oldItemIndex = originalItems.indexOfFirst { it.control.id == newLayoutItem.controlId }
            this.items.add(
                if (oldItemIndex == -1) {
                    val mutableControl = mutableShow.findControl(newLayoutItem.controlId)
                    MutableGridItem(
                        mutableControl,
                        newLayoutItem.column, newLayoutItem.row,
                        newLayoutItem.width , newLayoutItem.height,
                        if (mutableControl.hasInternalLayout) createSubLayout() else null
                    )
                } else {
                    oldItems[oldItemIndex].apply {
                        column = newLayoutItem.column
                        row = newLayoutItem.row
                        width = newLayoutItem.width
                        height = newLayoutItem.height
                    }
                }
            )
        }
    }

    fun createSubLayout(): MutableGridLayout =
        MutableGridLayout(1, 1)
}

data class MutableLayoutDimen(var scalar: Number, var unit: String) {
    fun build() = "${scalar.toString().replace(".0", "")}$unit"

    override fun toString(): String = build()

    companion object {
        fun decode(value: String): MutableLayoutDimen {
            return Regex("^(\\d+)([^\\d]+)$")
                .matchEntire(value)
                ?.let {
                    val (scalar, unit) = it.destructured
                    MutableLayoutDimen(scalar.toFloat(), unit)
                }
                ?: MutableLayoutDimen(1, "fr")
        }
    }
}
