package baaahs.app.ui

import baaahs.Logger
import baaahs.util.UniqueIds
import external.DraggableLocation
import external.DropReason
import external.DropResult
import external.ResponderProvided

class DragNDrop {
    private val dropTargets = UniqueIds<DropTarget>()

    fun onDragEnd(dropResult: DropResult, responderProvided: ResponderProvided) {
        if (dropResult.reason == DropReason.DROP.name) {
            val source = findTarget(dropResult.source) ?: return
            val dest = findTarget(dropResult.destination!!) ?: return
            val sourceIndex = dropResult.source.index
            val destIndex = dropResult.destination!!.index

            if (source == dest) {
                dest.moveDraggable(sourceIndex, destIndex)
            } else {
                val draggable = source.getDraggable(sourceIndex)
                if (dest.willAccept(draggable) && draggable.willMoveTo(dest)) {
                    source.removeDraggable(draggable)
                    dest.insertDraggable(draggable, destIndex)
                    draggable.onMove()
                }
            }
        }
    }

    fun findTarget(location: DraggableLocation): DropTarget? {
        val dropTarget = dropTargets[location.droppableId]
        if (dropTarget == null) {
            logger.warn { "No such drop target ${location.droppableId}" }
        }
        return dropTarget
    }

    fun addDropTarget(dropTarget: DropTarget): String {
        return dropTargets.idFor(dropTarget) { dropTarget.type }
    }

    fun removeDropTarget(dropTarget: DropTarget) {
        dropTargets.remove(dropTarget) || throw IllegalStateException("Unregistered drop target.")
    }

    companion object {
        private val logger = Logger("DragNDrop")
    }
}

interface DropTarget{
    val type: String

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