package baaahs.ui.gridlayout

import baaahs.app.settings.Provider
import baaahs.app.ui.editor.Editor
import baaahs.geom.Vector2I
import baaahs.show.GridItem
import baaahs.show.GridLayout
import baaahs.show.GridTab
import baaahs.show.IGridLayout
import baaahs.show.live.OpenIGridLayout
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutableShow
import baaahs.ui.Observable

class ViewRoot(
    private val gridLayout: OpenIGridLayout,
    gap: Int = 0,
    margins: Int = 0,
    private val openShow: Provider<OpenShow>,
    val editor: Editor<MutableIGridLayout>?,
    private val onLayoutChange: (IGridLayout, Boolean) -> Unit = { _, _ -> }
) : Observable() {
    internal val rootViewable = gridLayout.createViewable(this)

    val classes: Set<String> = emptySet()
    var bounds: Rect? = null
    val children: List<Viewable> = listOf(rootViewable)

    var gap: Int = gap
        set(value) { field = value; notifyChanged() }
    var margins: Int = margins
        set(value) { field = value; notifyChanged() }

    fun findViewable(id: String): Viewable =
        findOrNull(id) ?: error("No viewable found with id $id.")

    fun findOrNull(id: String): Viewable? =
        rootViewable.find(id)

    fun visit(callback: (Viewable) -> Unit) {
        rootViewable.visit(callback)
    }

    fun layout(bounds: Rect) {
        this.bounds = bounds
        rootViewable.layout(bounds)
        notifyChanged()
    }

//    fun editLayout(newLayout: GridLayout) {
//        onLayoutChange(newLayout, false)
//    }
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

    fun moveElement(movingId: String, toLayoutId: String?, toPosition: Vector2I) {
        val movingViewable = findViewable(movingId)
        val movingGridItem = gridLayout.gridLayout.find(movingId)!!
        val fromContainer = movingViewable.parent ?: rootViewable

        val rootViewableId = rootViewable.id
        val allItemLayouts = buildMap {
            put(rootViewableId, gridLayout.gridLayout)
            gridLayout.gridLayout.visit { gridItem ->
                put(gridItem.controlId, gridItem.layout)
            }
        }.toMutableMap()
        val destLayoutId = toLayoutId ?: rootViewableId
        val fromLayout = allItemLayouts[fromContainer.id]!!
        val toLayout = allItemLayouts[destLayoutId]!!

//        val fromRoot = fromContainer == rootViewable
//        val toRoot = toLayoutId == rootViewable.id || toLayoutId == null
        if (fromLayout == toLayout) {
            // Within the same layout.
            allItemLayouts[destLayoutId] = allItemLayouts[destLayoutId]!!
                .moveElement(movingId, toPosition.x, toPosition.y) as GridLayout
//            rootLayout = gridLayout.gridLayout.moveElement(movingId, toPosition.x, toPosition.y) as GridLayout
//        } else if (fromRoot) {
//            // From the root layout to another container.
//            rootLayout = rootLayout.removeElement(movingId) as GridLayout
//            allItemLayouts[toLayoutId!!] = allItemLayouts[toLayoutId]!!
//                .let { it.copy(layout = it.layout!!.removeElement(movingId) as GridLayout) }
//        } else if (toRoot) {
//            // From another container to the root layout.
//            allItemLayouts[fromContainer.id] = allItemLayouts[fromContainer.id]!!
//                .let { it.copy(layout = it.layout!!.removeElement(movingId) as GridLayout) }
//            rootLayout = gridLayout.gridLayout.moveElement(movingId, toPosition.x, toPosition.y) as GridLayout
        } else {
            // From one container to another container.
            allItemLayouts[fromContainer.id] = allItemLayouts[fromContainer.id]!!
                .removeElement(movingId) as GridLayout
            allItemLayouts[destLayoutId] = allItemLayouts[destLayoutId]!!
                .moveElement(movingId, toPosition.x, toPosition.y) as GridLayout
        }
        fun GridItem.substitute(): GridItem {
            allItemLayouts[controlId]?.let {
//                println("Replace ${this.controlId}'s layout with $it")
            }

            return copy(layout = allItemLayouts[controlId] as GridLayout?)
        }

        val updatedRootLayout = allItemLayouts[rootViewableId] as GridTab
        val updatedLayout = updatedRootLayout
            .copy(items = updatedRootLayout.items.map { it.substitute() })
        onLayoutChange(updatedLayout, true)
    }
}