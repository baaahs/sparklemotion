package baaahs.app.ui

import baaahs.camelize

interface DragNDrop {
    fun addDropTarget(dropTarget: DropTarget): String
    fun removeDropTarget(dropTarget: DropTarget)
}

interface DropTarget{
    val type: String

    fun suggestId(): String = type.camelize()
    fun moveDraggable(fromIndex: Int, toIndex: Int)
    fun willAccept(draggable: Draggable): Boolean
    fun getDraggable(index: Int): Draggable
    fun insertDraggable(draggable: Draggable, index: Int)
    fun removeDraggable(draggable: Draggable)
}

interface Draggable {
    fun willMoveTo(destination: DropTarget): Boolean = true
    fun onMove() {}
}