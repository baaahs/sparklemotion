package baaahs.show.mutable

import baaahs.getBang
import baaahs.show.Layout
import baaahs.show.Layouts
import baaahs.show.Panel
import baaahs.show.Tab

class MutableLayouts(
    val panels: MutableMap<String, MutablePanel> = mutableMapOf(),
    val formats: MutableMap<String, MutableLayout> = mutableMapOf()
) {
    constructor(baseLayouts: Layouts) : this(
        panels = baseLayouts.panels.mapValues { (_, v) -> MutablePanel(v) }.toMutableMap()
    ) {
        baseLayouts.formats.forEach { (id, v) ->
            formats[id] = MutableLayout(v, panels)
        }
    }

    fun editLayout(id: String, block: MutableLayout.() -> Unit): MutableLayouts {
        formats.getOrPut(id) { MutableLayout(null) }.apply(block)
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
    constructor(baseLayout: Layout, panels: Map<String, MutablePanel>) : this(
        mediaQuery = baseLayout.mediaQuery,
        tabs = baseLayout.tabs.map { MutableTab(it, panels) }.toMutableList()
    )

    fun build(showBuilder: ShowBuilder): Layout {
        return Layout(mediaQuery, tabs.map { it.build(showBuilder) })
    }

    fun editTab(title: String, block: MutableTab.() -> Unit): MutableLayout {
        val tab = tabs.find { it.title == title }
            ?: run { MutableTab(title).also { tabs.add(it) } }
        tab.apply(block)
        return this
    }
}

class MutableTab(
    var title: String,
    val columns: MutableList<MutableLayoutDimen> = mutableListOf(),
    val rows: MutableList<MutableLayoutDimen> = mutableListOf(),
    val areas: MutableList<MutablePanel> = mutableListOf()
) {
    constructor(baseTab: Tab, panels: Map<String, MutablePanel>) : this(
        title = baseTab.title,
        columns = baseTab.columns.map { MutableLayoutDimen.decode(it) }.toMutableList(),
        rows = baseTab.rows.map { MutableLayoutDimen.decode(it) }.toMutableList(),
        areas = baseTab.areas.map { panels.getBang(it, "panel") }.toMutableList()
    )

    fun build(showBuilder: ShowBuilder): Tab {
        return Tab(
            title,
            columns.map { it.build() },
            rows.map { it.build() },
            areas.map { showBuilder.idFor(it.build()) }
        )
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
