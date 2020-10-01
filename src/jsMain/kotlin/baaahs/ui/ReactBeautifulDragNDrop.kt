package baaahs.ui

import baaahs.Logger
import external.DraggableLocation
import external.DropReason
import external.DropResult
import external.ResponderProvided

class ReactBeautifulDragNDrop : DragNDrop() {
    fun onDragEnd(dropResult: DropResult, responderProvided: ResponderProvided) {
        if (dropResult.reason == DropReason.DROP.name) {
            val dropSourceLoc = dropResult.source
            val dropDestLoc = dropResult.destination ?: return

            val source = findTarget(dropSourceLoc) ?: return
            val dest = findTarget(dropDestLoc) ?: return
            val sourceIndex = dropSourceLoc.index
            val destIndex = dropDestLoc.index

            onMove(source, sourceIndex, dest, destIndex)
        }
    }

    private fun findTarget(location: DraggableLocation): DropTarget? {
        val dropTarget = dropTargets[location.droppableId]
        if (dropTarget == null) {
            logger.warn { "No such drop target ${location.droppableId}" }
        }
        return dropTarget
    }

    companion object {
        private val logger = Logger("DragNDrop")
    }
}