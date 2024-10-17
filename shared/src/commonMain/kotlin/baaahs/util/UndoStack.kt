package baaahs.util

open class UndoStack<T>(private val capacity: Int = 100) {
    val stack = ArrayList<T>()
    var position = -1

    fun reset(initialState: T?) {
        stack.clear()
        initialState?.let { stack.add(initialState) }
        position = 0
    }

    fun changed(newState: T) {
        truncate(position + 1)

        stack.add(newState)
        position++
    }

    open fun undo(): T = stack[--position]
    fun canUndo(): Boolean = position > 0

    open fun redo(): T = stack[++position]
    fun canRedo(): Boolean = position < stack.size - 1

    private fun truncate(size: Int) {
        while (position >= capacity) {
            stack.removeAt(0)
            position--
        }

        while (stack.size > size) {
            stack.removeAt(stack.size - 1)
        }
    }
}
