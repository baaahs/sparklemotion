package baaahs.ui.gridlayout

import baaahs.control.OpenButtonGroupControl
import baaahs.show.live.OpenGridItem

class LayoutGrid(
    private val columns: Int,
    private val rows: Int,
    private val items: List<OpenGridItem>,
    private val draggingItem: String?
) {
    val layout = Layout(buildList {
        items.forEach { item ->
            val isStatic = draggingItem != null &&
                    draggingItem != item.control.id &&
                    item.control is OpenButtonGroupControl

            add(
                LayoutItem(
                    item.column, item.row,
                    item.width, item.height,
                    item.control.id, isStatic = isStatic
                )
            )
        }
    }, columns, rows)

    fun forEachCell(block: (column: Int, row: Int) -> Unit) {
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                block(column, row)
            }
        }
    }
}