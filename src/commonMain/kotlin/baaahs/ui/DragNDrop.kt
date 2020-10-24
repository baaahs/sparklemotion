package baaahs.ui

import baaahs.camelize
import baaahs.util.UniqueIds

abstract class DragNDrop {
    protected val dropTargets = UniqueIds<DropTarget>()

    protected fun onMove(source: DropTarget, sourceIndex: Int, dest: DropTarget, destIndex: Int) {
        if (source == dest) {
            if (sourceIndex != destIndex) {
                dest.moveDraggable(sourceIndex, destIndex)
            }
        } else {
            val draggable = source.getDraggable(sourceIndex)
            if (dest.willAccept(draggable) && draggable.willMoveTo(dest)) {
                source.removeDraggable(draggable)
                dest.insertDraggable(draggable, destIndex)
                draggable.onMove()
            }
        }
    }

    fun addDropTarget(dropTarget: DropTarget): String {
        return dropTargets.idFor(dropTarget) { dropTarget.suggestId() }
    }

    fun removeDropTarget(dropTarget: DropTarget) {
        dropTargets.remove(dropTarget) || throw IllegalStateException("Unregistered drop target.")
    }

    fun removeDropTarget(id: String) {
        dropTargets.removeId(id) || throw IllegalStateException("Unregistered drop target.")
    }

    fun reset() {
        dropTargets.clear()
    }
}

interface DropTarget{
    val type: String
    val dropTargetId: String

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