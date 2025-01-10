package baaahs.ui.gridlayout

import baaahs.show.GridItem
import baaahs.show.GridTab
import baaahs.show.mutable.MutableGridItem
import baaahs.show.mutable.MutableGridTab

fun GridItem.createModel(): Node =
    Node(
        id,
        column, row,
        width, height,
        layout = layout?.let { layout ->
            Layout(layout.columns, layout.rows, layout.items.map { item ->
                item.createModel()
            })
        }
    )

fun GridTab.createModel(): GridModel =
    GridModel(
        Node(
            "_ROOT_",
            0, 0, 1, 1,
            layout = Layout(columns, rows, items.map { item ->
                item.createModel()
            })
        )
    )

fun MutableGridTab.applyChanges(model: GridModel) {
    val mutableRoot = this

    val mutableNodes = buildMap {
        visit { item -> put(item.control.asBuiltId, item) }
    }

    model.visit { node ->
        if (node == model.rootNode) {
            val layout = node.layout!!
            mutableRoot.columns = layout.columns
            mutableRoot.rows = layout.rows
        } else {
            val mutableNode = mutableNodes[node.id]!!
            mutableNode.column = node.left
            mutableNode.row = node.top
            mutableNode.width = node.width
            mutableNode.height = node.height
            mutableNode.layout?.also { mutableLayout ->
                node.layout?.also { layout ->
                    mutableLayout.columns = layout.columns
                    mutableLayout.rows = layout.rows
                    mutableLayout.items.clear()
                    for (child in layout.children) {
                        mutableLayout.items.add(mutableNodes[child.id]!!)
                    }
                }
            }
        }
    }
}