package baaahs.app.ui.layout

import react.createContext

val dragNDropContext = createContext<DragNDropContext>()

external interface DragNDropContext {
    var isLegacy: Boolean
}