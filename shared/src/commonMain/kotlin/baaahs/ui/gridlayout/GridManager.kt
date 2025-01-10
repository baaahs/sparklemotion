package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.getBang
import baaahs.show.ImpossibleLayoutException
import baaahs.show.NoChangesException

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

    var margin = 5
    var gap = 5

    abstract fun createNodeWrapper(node: Node): NodeWrapper

    fun editable(isEditable: Boolean) {
        this.isEditable = isEditable
        for (nodeWrapper in nodeWrappers.values) {
            nodeWrapper.updateEditable()
        }
    }

    fun onResize(width: Int, height: Int) {
        rootNodeWrapper.layout(Rect(0, 0, width, height))
    }

    fun dragging(draggingNodeWrapper: NodeWrapper, center: Vector2I?, previousPosition: Vector2I?): GridModel {
        debug("Dragging ${draggingNodeWrapper.node.id} center=$center")
        if (center == null) {
            draggingNode = null
            // No longer dragging.
            return model
        }

        val targetNodeWrapper = findDeepestContainingNode(center)
        if (draggingNodeWrapper.node.id == targetNodeWrapper.node.id) {
            debug("huh?!??!?")
        }
        val draggingNodeTopLeft = draggingNodeWrapper.effectiveBounds!!.let {
            Vector2I(it.left, it.top)
        }
        val targetPosition = targetNodeWrapper.findPositionForPx(draggingNodeTopLeft)!!
        debug("-> to ${targetNodeWrapper.node.id} $targetPosition")
        val directions = Direction.rankedPushOptions(center - (previousPosition ?: center))
        return move(draggingNodeWrapper.node, targetNodeWrapper.node, targetPosition.cell, directions)
            .also {
                debug(it.stringify())
            }
    }

    fun move(movingNode: Node, intoNode: Node, cell: Vector2I, directions: Array<Direction>): GridModel {
        return try {
            debug("move on current model")
            baseModel.moveElement(movingNode, intoNode, cell, directions)
        } catch (e: ImpossibleLayoutException) {
            debug("move failed: ${e.message}")
            null
        } ?: try {
            debug("move on base model")
            model.moveElement(movingNode, intoNode, cell, directions)
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
        debug(rootNodeWrapper.dump())
        rootNodeWrapper.relayout()
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
            if (overItem?.node?.isContainer == true && overItem != draggingNode) {
                currentContainer = overItem
            } else break
        }
        return currentContainer
    }

    private fun wrapperFor(childNode: Node): NodeWrapper =
        nodeWrappers.getBang(childNode.id, "node wrappers")

    abstract fun debug(s: String)

    abstract inner class NodeWrapper(
        node: Node
    ) {
        var node: Node = node
            private set

        internal var layoutBounds: Rect? = null
        internal var layer: Int = 0
        private var gridContainer: GridContainer? = null
        protected var pointerDown: Vector2I? = null
        private var dragOffset: Vector2I? = null
        val isDragging get() = dragOffset != null

        val effectiveBounds
            get() =
                layoutBounds?.let { layoutBounds ->
                    dragOffset?.let { dragOffset ->
                        layoutBounds + dragOffset
                    } ?: layoutBounds
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

        fun layout(bounds: Rect, layer: Int = 0) {
            if (isDragging) {
                placeholder.layout(bounds)
                return
            }

            if (node.id == "ripple") {
                println("layout ${node.id} $bounds")
            }
            this.layoutBounds = bounds
            applyStyle()

            if (node.isContainer) {
                val layout = node.layout!!
                val gridContainer = GridContainer(layout.columns, layout.rows, bounds.inset(margin), gap)
                this.gridContainer = gridContainer
                forChildren { child ->
                    val childBounds = try {
                        with (child.node) {
                            gridContainer.calculateRegionBounds(left, top, width, height)
                        }
                    } catch (e: Exception) {
                        throw Exception("Failed to calculate region bounds for ${child.node.id} in ${node.id}: ${e.message}", e)
                    }
                    child.layout(childBounds, layer + 1)
                }
            }
        }

        fun findPositionForPx(pxCoord: Vector2I): GridPosition? =
            gridContainer?.findCell(pxCoord.x, pxCoord.y)

        fun boundsOfTopLeftCell(): Rect? =
            gridContainer?.boundsOfTopLeftCell()

        fun onPointerDown(point: Vector2I): Boolean {
            if (!isEditable) return false
            pointerDown = point
            placeholder.layout(layoutBounds)
            return true
        }

        fun onPointerMove(point: Vector2I) {
            pointerDown?.let { pointerDown ->
                debug("\npointer move on ${node.id}")
                draggingNode = this
                draggedBy(point - pointerDown)
            }
        }

        fun onPointerUp(point: Vector2I) {
            pointerDown?.let { pointerDown ->
                droppedAt(point - pointerDown)
            }
            draggingNode = null
            pointerDown = null
            placeholder.layout(null)
        }

        fun onPointerCancel() {
            draggedBy(null)
            pointerDown = null
            placeholder.layout(null)
        }

        private fun draggedByInternal(offset: Vector2I?) {
            dragOffset = offset
            if (node.layout != null) {
                forChildren { child ->
                    child.draggedByInternal(offset)
                }
            }
            applyStyle()
        }


        /** @param offset Distance from the node's original position when dragging started. */
        fun draggedBy(offset: Vector2I?) {
            val previousPosition = effectiveBounds?.center
            draggedByInternal(offset)
            try {
                val updatedModel = dragging(this, effectiveBounds?.center, previousPosition)
                updateFromModel(updatedModel)
            } catch (_: NoChangesException) {
                // No changes, ignore.
            }
            applyStyle()
        }

        /** @param offset Distance from original position. */
        fun droppedAt(offset: Vector2I) {
            val previousPosition = effectiveBounds?.center
            draggedByInternal(offset)
            try {
                val updatedModel = dragging(this, effectiveBounds?.center, previousPosition)
                println("XXX updatedModel: ${updatedModel.stringify()}")
                updateFromModel(updatedModel)
            } catch (_: NoChangesException) {
                println("XXX no changes!")
                // No changes, ignore.
            }
            if (model != baseModel) {
                onChange(model)
            }
            draggedByInternal(null)
            applyStyle()
        }

        fun findChildAt(cell: Vector2I): NodeWrapper? =
            childrenByCell[cell]?.let { wrapperFor(it) }

        private fun forChildren(callback: (NodeWrapper) -> Unit) {
            val layout = node.layout ?: return
            for (childNode in layout.children) {
                val childWrapper = wrapperFor(childNode)
                callback(childWrapper)
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