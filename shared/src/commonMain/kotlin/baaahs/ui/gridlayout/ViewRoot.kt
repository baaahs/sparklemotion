package baaahs.ui.gridlayout

import baaahs.show.live.OpenIGridLayout
import baaahs.ui.Observable
import baaahs.ui.View

class ViewRoot(
    val gridLayout: OpenIGridLayout,
    gap: Int = 0,
    margins: Int = 0
) : Observable() {
    private val rootViewable = gridLayout.createViewable(this)

    val classes: Set<String> = emptySet()
    var bounds: Rect? = null
    val children: List<Viewable> = listOf(rootViewable)
    val view: View get() = TODO("not implemented")

    var gap: Int = gap
        set(value) { field = value; notifyChanged() }
    var margins: Int = margins
        set(value) { field = value; notifyChanged() }

    fun find(id: String): Viewable? =
        rootViewable.find(id)

    fun visit(callback: (Viewable) -> Unit) {
        rootViewable.visit(callback)
    }

    fun layout(bounds: Rect) {
        this.bounds = bounds
        rootViewable.layout(bounds)
        notifyChanged()
    }
}