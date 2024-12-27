package baaahs.ui.gridlayout

import baaahs.app.settings.Provider
import baaahs.app.ui.editor.Editor
import baaahs.show.GridLayout
import baaahs.show.live.OpenIGridLayout
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutableShow
import baaahs.ui.Observable
import baaahs.ui.View

class ViewRoot(
    gridLayout: OpenIGridLayout,
    gap: Int = 0,
    margins: Int = 0,
    private val openShow: Provider<OpenShow>,
    val editor: Editor<MutableIGridLayout>,
    private val onLayoutChange: (GridLayout, Boolean) -> Unit
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

    fun editLayout(newLayout: GridLayout) {
        onLayoutChange(newLayout, false)
    }
    //        // Move the element to the dragged location.
//                        layout!!.moveElement
//        val newLayout = try {
//            oldLayout.moveElement(l, x, y)
//        } catch (e: ImpossibleLayoutException) {
//            setState {
//                this.notDroppableHere = true
//            }
//            return
//        }

    fun editWith(editor: Editor<MutableIGridLayout>, block: MutableIGridLayout.() -> Unit): MutableShow =
        openShow.get().edit { editor.edit(this, block) }
}