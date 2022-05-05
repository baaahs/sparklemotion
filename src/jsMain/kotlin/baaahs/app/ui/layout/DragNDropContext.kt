package baaahs.app.ui.layout

import baaahs.ui.Observable
import baaahs.ui.gridlayout.GridLayoutState
import react.createContext

val dragNDropContext = createContext<DragNDropContext>()

external interface DragNDropContext {
    var isLegacy: Boolean
    var gridLayoutContext: GridLayoutContext
}

class GridLayoutContext : Observable() {
    private val layouts = hashMapOf<String, GridLayoutState>()
    private val id = nextId++

    var dragging = false
        set(value) {
            field = value
            notifyChanged()
        }

    init {
        console.log("new GridLayoutContext id $id.")
    }

    fun findLayout(id: String): GridLayoutState =
        layouts[id]
            ?: error("Unknown layout \"$id\" in GridLayoutContext ${this.id}.")

    fun registerLayout(id: String, state: GridLayoutState) {
        console.log("GridLayoutContext ${this.id}: Register $id")
        if (layouts.put(id, state) != null) {
            error("Layout \"$id\" already registered.")
        }
    }

    fun unregisterLayout(id: String) {
        console.log("GridLayoutContext ${this.id}: Unregister $id")
        if (layouts.remove(id) == null) {
            error("Layout \"$id\" not registered.")
        }
    }

    companion object {
        private var nextId: Int = 0
    }
}