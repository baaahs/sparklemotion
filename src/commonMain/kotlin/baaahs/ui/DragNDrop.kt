package baaahs.ui

import baaahs.camelize
import baaahs.util.UniqueIds

abstract class DragNDrop<P> {
    protected val dropTargets = UniqueIds<DropTarget<P>>()

    protected fun onMove(source: DropTarget<P>, sourcePosition: P, dest: DropTarget<P>, destPosition: P) {
        if (source == dest) {
            if (sourcePosition != destPosition) {
                dest.moveDraggable(sourcePosition, destPosition)
            }
        } else {
            val draggable = source.getDraggable(sourcePosition)
            if (dest.willAccept(draggable) && draggable.willMoveTo(dest)) {
                source.removeDraggable(draggable)
                dest.insertDraggable(draggable, destPosition)
                draggable.onMove()
            }
        }
    }

    fun addDropTarget(dropTarget: DropTarget<P>): String {
        return dropTargets.idFor(dropTarget) { dropTarget.suggestId() }
    }

    fun removeDropTarget(dropTarget: DropTarget<P>) {
        dropTargets.remove(dropTarget) || throw IllegalStateException("Unregistered drop target.")
    }

    fun removeDropTarget(id: String) {
        dropTargets.removeId(id) || throw IllegalStateException("Unregistered drop target.")
    }

    fun reset() {
        dropTargets.clear()
    }
}

interface DropTarget<P>{
    val type: String
    val dropTargetId: String

    fun suggestId(): String = type.camelize()
    fun moveDraggable(fromPosition: P, toPosition: P)
    fun willAccept(draggable: Draggable<P>): Boolean
    fun getDraggable(position: P): Draggable<P>
    fun insertDraggable(draggable: Draggable<P>, position: P)
    fun removeDraggable(draggable: Draggable<P>)
}

interface Draggable<P> {
    fun willMoveTo(destination: DropTarget<P>): Boolean = true
    fun onMove() {}
}