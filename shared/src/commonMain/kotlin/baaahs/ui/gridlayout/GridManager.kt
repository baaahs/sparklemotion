package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.getBang
import baaahs.show.ImpossibleLayoutException
import baaahs.show.NoChangesException
import kotlin.math.max

abstract class GridManager(
    model: GridModel,
    private val onChange: (GridModel) -> Unit
) {
    val baseModel: GridModel = model
    var model: GridModel = model
        private set
    var isEditable: Boolean = false
        private set

    var nodeWrappers = buildMap { model.visit { put(it.id, createNodeWrapper(it)) } }
        private set
    abstract val placeholder: Placeholder

    private var rootNodeWrapper = nodeWrappers[model.rootNode.id]!!

    internal var draggingNode: NodeWrapper? = null
    internal var resizingNode: NodeWrapper? = null
    internal var draggingState: DraggingState? = null

    var margin = 5
    var gap = 5

    abstract fun createNodeWrapper(node: Node): NodeWrapper

    fun editable(isEditable: Boolean) {
        this.isEditable = isEditable
        for (nodeWrapper in nodeWrappers.values) {
            nodeWrapper.updateEditable()
        }
    }

    fun onNodeResize(width: Int, height: Int) {
        rootNodeWrapper.layout(Rect(0, 0, width, height))
    }

    fun dragging(draggingNodeWrapper: NodeWrapper, originCellCenter: Vector2I?, previousPosition: Vector2I?): GridModel {
        debug("Dragging ${draggingNodeWrapper.node.id} center=$originCellCenter")
        if (originCellCenter == null) {
            draggingNode = null
            // No longer dragging.
            return model
        }

        val targetNodeWrapper = findDeepestContainingNode(originCellCenter)
        if (draggingNodeWrapper.node.id == targetNodeWrapper.node.id) {
            error("Dragging node onto itself? That's unpossible!")
        }
        val targetPosition = targetNodeWrapper.findPositionForPx(originCellCenter)!!
        println("-> to ${targetNodeWrapper.node.id} $targetPosition")
        val directions = Direction.rankedPushOptions(originCellCenter - (previousPosition ?: originCellCenter))
        return move(draggingNodeWrapper.node, targetNodeWrapper.node, targetPosition.cell, draggingNodeWrapper.node.size, directions)
            .also {
                debug(it.stringify())
            }
    }

    fun resizing(draggingNodeWrapper: NodeWrapper, handlePosition: Vector2I, previousPosition: Vector2I?): GridModel {
        val parentNodeWrapper = draggingNodeWrapper.parent
            ?: error("Parent for ${draggingNodeWrapper.node.id} not found.")
        val overCell = parentNodeWrapper.findCornerForPx(handlePosition)
        if (overCell == null) error("Whut?")
        val size = (overCell - draggingNodeWrapper.node.topLeft).let {
            Vector2I(max(it.x, 1), max(it.y, 1))
        }
        val directions = Direction.rankedPushOptions(handlePosition - (previousPosition ?: Vector2I.origin))
        return move(draggingNodeWrapper.node, parentNodeWrapper.node, draggingNodeWrapper.node.topLeft, size, directions)
    }

    fun move(movingNode: Node, intoNode: Node, cell: Vector2I, size: Vector2I, directions: Array<Direction>): GridModel {
        return try {
            model.moveElement(movingNode, intoNode, cell, size, directions)
        } catch (e: ImpossibleLayoutException) {
            debug("move failed: ${e.message}")
            null
        } ?: try {
            baseModel.moveElement(movingNode, intoNode, cell, size, directions)
        } catch (e: ImpossibleLayoutException) {
            debug("move failed: ${e.message}")
            null
        } ?: throw NoChangesException()
    }

    fun updateFromModel(updatedModel: GridModel) {
        println("XXX updateFromModel!")
        println(updatedModel.stringify())
        nodeWrappers = buildMap {
            updatedModel.visit { node ->
                val wrapper = nodeWrappers[node.id]?.apply { updateNode(node) }
                    ?: createNodeWrapper(node)
                put(node.id, wrapper)
            }
        }
        rootNodeWrapper = nodeWrappers.getBang(updatedModel.rootNode.id, "node wrapper")
        model = updatedModel

//        println(rootNodeWrapper.dump())
//        debug(rootNodeWrapper.dump())
        rootNodeWrapper.relayout()
    }

    fun cancelChanges() {
        updateFromModel(baseModel)
    }

    class Target(
        val container: NodeWrapper,
        val position: GridPosition
    )

    fun findDeepestContainingNode(center: Vector2I): NodeWrapper {
        var currentContainer: NodeWrapper = rootNodeWrapper
        var position: GridPosition? = null
        var overItem: NodeWrapper? = null

        while (currentContainer.node.isContainer) {
             position = currentContainer.findPositionForPx(center)
                ?: error("Can't find target in ${currentContainer.node.id}.")
//            if (position == null) {
//                debug("Found no cell in ${currentContainer.node.id}? bounds: ${currentContainer.layoutBounds}")
//                return currentContainer
//            }

            overItem = currentContainer.findChildAt(position.cell)
            if (overItem?.node?.isContainer == true
                && overItem != draggingNode
                && draggingNode?.let { overItem.mayContain(it) } ?: false
            ) {
                currentContainer = overItem
            } else break
        }
        return currentContainer
    }

    private fun wrapperFor(childNode: Node): NodeWrapper? =
        nodeWrappers[childNode.id]

    abstract fun debug(s: String)

    abstract inner class NodeWrapper(
        node: Node
    ) {
        var node: Node = node
            private set
        val parent: NodeWrapper?
            get() = model.parents[node]?.id?.let { nodeWrappers[it] }

        internal var layoutBounds: Rect? = null
        internal var layer: Int = 0
        protected var containerInset: Rect? = null
        private var gridContainer: GridContainer? = null
        protected var pointerDown: Vector2I? = null
        protected var dragOffset: Vector2I? = null
        val isDragging get() = dragOffset != null
        protected var resizeOriginalSize: Vector2I? = null
        protected var resizeDelta: Vector2I? = null
        var isResizing: Boolean = false
        val isMultiCell get() = node.width > 1 || node.height > 1
        val cellSize get() = Vector2I(node.width, node.height)

        val originCellCenter get() =
            if (isMultiCell) {
                layoutBounds!!.size / cellSize / 2 + layoutBounds!!.topLeft
            } else layoutBounds!!.center

        val effectiveBounds
            get() =
                layoutBounds?.let { layoutBounds ->
                    val dragged = (dragOffset?.let { dragOffset ->
                        layoutBounds + dragOffset
                    } ?: layoutBounds)

                    resizeDelta?.let { resizeDelta ->
                        Rect(dragged.left, dragged.top, resizeOriginalSize!!.x, resizeOriginalSize!!.y)
                            .resizeBy(resizeDelta)
                    } ?: dragged
                }

        private val childrenByCell: Map<Vector2I, Node> = buildMap {
            node.layout?.let { layout ->
                for (childNode in layout.children) {
                    for (cell in childNode.gridCells) {
                        put(cell, childNode)
                    }
                }
            }
        }

        fun updateNode(newNode: Node) {
            this.node = newNode
        }

        abstract fun updateEditable()

        fun relayout() {
            layoutBounds?.let { layout(it) }
        }

        abstract fun applyStyle()

        fun layout(bounds: Rect) {
            if (isDragging) {
                placeholder.layout(bounds)
                return
            }

            val oldLayoutBounds = layoutBounds
            this.layoutBounds = bounds
            applyStyle()

            if (node.isContainer) {
                val sizeIsUnchanged =
                    layoutBounds?.size == bounds.size
                val gridContainer = gridContainer
                if (sizeIsUnchanged == false && gridContainer != null) {
                    val shift = bounds.topLeft - (oldLayoutBounds?.topLeft ?: Vector2I.origin)
                    val newInnerBounds = layoutBounds!! + containerInset!!
                    println("ZZZ layout(${node.id}, $sizeIsUnchanged, ")
                    this.gridContainer = gridContainer.copy(
                        bounds = newInnerBounds
//                            Rect(
//                                oldInnerBounds.left + shift.x,
//                                oldInnerBounds.top + shift.y,
//                                oldInnerBounds.width,
//                                oldInnerBounds.height
//                            )
                    )
                }

                this.gridContainer?.let { gridContainer ->
                    layoutContainer(gridContainer)
                }
            }
        }

        open fun layoutContainer(gridContainer: GridContainer) {
            this.gridContainer = gridContainer

            forChildren { child ->
                val childBounds = try {
                    with(child.node) {
                        gridContainer.calculateRegionBounds(left, top, width, height)
                    }
                } catch (e: Exception) {
                    throw Exception("Failed to calculate region bounds for ${child.node.id} in ${node.id}: ${e.message}", e)
                }
                child.layout(childBounds + (dragOffset ?: Vector2I.origin))
            }
        }

        fun findPositionForPx(pxCoord: Vector2I): GridPosition? =
            gridContainer?.findCell(pxCoord.x, pxCoord.y)
        fun findCornerForPx(pxCoord: Vector2I): Vector2I? =
            findPositionForPx(pxCoord)?.let { it.quadrant.round(it.cell) }

        fun onPointerDown(point: Vector2I, draggingState: DraggingState = DraggingState.Move): Boolean {
            if (!isEditable) return false
            this@GridManager.draggingState = draggingState
            resizeOriginalSize = layoutBounds?.size ?: Vector2I.origin
            pointerDown = point
            placeholder.layout(layoutBounds)
            return true
        }

        fun onPointerMove(point: Vector2I) {
            pointerDown?.let { pointerDown ->
                if (draggingState == DraggingState.Move) {
                    debug("\npointer move on ${node.id}")
                    draggingNode = this
                    draggedBy(point - pointerDown)
                } else {
                    resizingNode = this
                    resizedBy(point - pointerDown)
                }
            }
        }

        fun onPointerUp(point: Vector2I) {
            pointerDown?.let { pointerDown ->
                if (draggingState == DraggingState.Move) {
                    if (draggingNode != null)
                        droppedAt(point - pointerDown)
                    draggingNode = null
                } else {
                    if (resizingNode != null)
                        resizedTo(point - pointerDown)
                    resizingNode = null
                }
            }
            pointerDown = null
            placeholder.layout(null)
        }

        open fun onPointerCancel() {
            draggedBy(null)
            pointerDown = null
            placeholder.layout(null)
            cancelChanges()
        }

        open fun dragChildren(offset: Vector2I?) {
            dragOffset = offset
            if (node.layout != null) {
                forChildren { child ->
                    child.dragChildren(offset)
                }
            }
            applyStyle()
        }

        open fun visualResize(offset: Vector2I?) {
            resizeDelta = offset
            visualResizeChildren(offset != null)
        }

        fun visualResizeChildren(isResizing: Boolean) {
            this.isResizing = isResizing
            if (node.layout != null) {
                forChildren { child -> child.visualResizeChildren(isResizing) }
            }
            applyStyle()
        }

        /** @param offset Distance from the node's original position when dragging started. */
        fun draggedBy(offset: Vector2I?) {
            val previousPosition = originCellCenter.plus(dragOffset ?: Vector2I.origin)
            dragChildren(offset)
            try {
                val updatedModel = dragging(
                    draggingNodeWrapper = this,
                    originCellCenter.plus(dragOffset ?: Vector2I.origin),
                    previousPosition
                )
                updateFromModel(updatedModel)
            } catch (_: NoChangesException) {
                // No changes, ignore.
            }
            applyStyle()
        }

        /** @param offset Distance from original position. */
        fun droppedAt(offset: Vector2I) {
            draggedBy(offset)
            if (model != baseModel) {
                onChange(model)
            }
            dragChildren(null)
            applyStyle()
        }

        /** @param offset Distance from the pointer-down position when resizing started. */
        fun resizedBy(offset: Vector2I?) {
            val previousPosition = dragOffset
            visualResize(offset)
            try {
                val updatedModel = resizing(
                    draggingNodeWrapper = this,
                    pointerDown!! + (offset ?: Vector2I.origin),
                    previousPosition
                )
                updateFromModel(updatedModel)
            } catch (_: NoChangesException) {
                // No changes, ignore.
            }
            applyStyle()
        }

        fun resizedTo(offset: Vector2I) {
            resizedBy(offset)
            if (model != baseModel) {
                onChange(model)
            }
            visualResize(null)
            applyStyle()
        }

        fun findChildAt(cell: Vector2I): NodeWrapper? =
            childrenByCell[cell]?.let { wrapperFor(it) }

        open fun mayContain(otherNodeWrapper: NodeWrapper) = true

        private fun forChildren(callback: (NodeWrapper) -> Unit) {
            val layout = node.layout ?: return
            for (childNode in layout.children) {
                wrapperFor(childNode)
                    ?.let { callback(it) }
            }
        }

        fun dump() = buildString { dump(this) }

        private fun dump(buf: StringBuilder, level: Int = 0) {
            with (node) {
                debug("${"  ".repeat(level)}$id: $left,$top:$width,$height ($effectiveBounds)")
                layout?.children?.forEach { child ->
                    val childWrapper = nodeWrappers.getBang(child.id, "node wrappers")
                    childWrapper.dump(buf, level + 1)
                }
            }
        }
    }

    abstract inner class Placeholder {
        var bounds: Rect? = null
            private set

        abstract fun applyStyle()

        fun layout(bounds: Rect?) {
            this.bounds = bounds
            applyStyle()
        }
    }
}

enum class DraggingState {
    Move,
    ResizeFromTopLeft,
    ResizeFromBottomRight
}