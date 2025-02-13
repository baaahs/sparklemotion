package baaahs.ui.gridlayout

fun GridModel.stringify(): String = rootNode.stringify()

fun Node.stringify(): String {
    val gridWidth = layout?.columns ?: 1
    val gridHeight = layout?.rows ?: 1
    val items = layout?.children ?: emptyList()
    val cells = Array(gridHeight) { Array(gridWidth) { "." } }
    println("$id $width,$height")
    val nestedGrids = mutableMapOf<String, Node>()
    items.forEach { item ->
        (0 until item.height).forEach { row ->
            (0 until item.width).forEach { col ->
                val iCol = item.left + col
                val iRow = item.top + row
                if (iCol + 1 > gridWidth || iRow + 1 > gridHeight) {
                    println("Item ${item.id} size $iCol,$iRow > parent $id size $gridWidth,$gridHeight")
                    return@forEach
                }

                if (cells[iRow][iCol] != ".") {
                    cells[iRow][iCol] = "!"
                } else {
                    cells[iRow][iCol] = item.id.substring(0, 1)
                    if (item.layout != null) {
                        nestedGrids[item.id] = item
                    }
                }
            }
        }
    }
    return buildString {
        append(cells.joinToString("\n", transform = { it.joinToString("") }))
        nestedGrids.forEach { (gridName, nestedGrid) ->
            append("\n\n# $gridName:\n")
            append(nestedGrid.stringify())
        }
    }
}
