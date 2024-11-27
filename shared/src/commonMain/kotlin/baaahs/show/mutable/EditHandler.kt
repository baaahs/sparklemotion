package baaahs.show.mutable

interface EditHandler<T, TState> {
    fun onEdit(mutableDocument: MutableDocument<T>, pushToUndoStack: Boolean = true)
    fun onEdit(document: T, pushToUndoStack: Boolean = true)
    fun onEdit(document: T, documentState: TState, pushToUndoStack: Boolean = true)
}