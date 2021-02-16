package baaahs.show.mutable

import baaahs.show.Layout
import baaahs.show.Layouts
import baaahs.show.Tab

class MutableLayouts(baseLayouts: Layouts) {
    var panelNames = baseLayouts.panelNames.toMutableList()
    val formats = baseLayouts.formats.mapValues { (_, v) -> MutableLayout(v) }.toMutableMap()

    fun copyFrom(layouts: MutableLayouts) {
        panelNames.clear()
        panelNames.addAll(layouts.panelNames)

        formats.clear()
        formats.putAll(layouts.formats)
    }

    fun build(): Layouts {
        return Layouts(panelNames, formats.mapValues { (_, v) -> v.build() })
    }
}

class MutableLayout(baseLayout: Layout) {
    var mediaQuery: String? = baseLayout.mediaQuery
    var tabs: MutableList<MutableTab> = baseLayout.tabs.map { MutableTab(it) }.toMutableList()

    fun build(): Layout {
        return Layout(mediaQuery, tabs.map { it.build() })
    }
}

class MutableTab(baseTab: Tab) {
    val title: String = baseTab.title
    val columns: MutableList<MutableLayoutDimen> = baseTab.columns.map { MutableLayoutDimen.decode(it) }.toMutableList()
    val rows: MutableList<MutableLayoutDimen> = baseTab.rows.map { MutableLayoutDimen.decode(it) }.toMutableList()
    val areas: MutableList<String> = baseTab.areas.toMutableList()

    fun build(): Tab {
        return Tab(
            title,
            columns.map { it.build() },
            rows.map { it.build() },
            areas
        )
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
