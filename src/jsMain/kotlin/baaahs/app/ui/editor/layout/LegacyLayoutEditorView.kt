package baaahs.app.ui.editor.layout

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.show.mutable.MutableLayouts
import baaahs.show.mutable.MutableLegacyTab
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.Display
import kotlinx.css.GridTemplateColumns
import kotlinx.css.display
import kotlinx.css.gridTemplateColumns
import materialui.icon
import mui.material.IconButton
import react.*
import react.dom.div
import styled.inlineStyles

private val LegacyLayoutEditorView = xComponent<LegacyLayoutEditorProps>("LegacyLayoutEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layoutEditor

    val tab = props.tab

    div(+styles.editorGrid) {
        // grid holder
        inlineStyles {
            display = Display.grid
            gridTemplateColumns = GridTemplateColumns(
                MutableList(tab.columns.size + 1) { "1fr" }.joinToString(" ") + " auto"
            )
//                        gridTemplateAreas = GridTemplateAreas(areas.joinToString(" ") { "\"$it\"" })
//                        gridTemplateColumns = GridTemplateColumns(currentTab.columns.joinToString(" "))
//                        gridTemplateRows = GridTemplateRows(currentTab.rows.joinToString(" "))
        }

        (-1 until tab.rows.size).forEach { rowIndex ->
            (-1 until tab.columns.size).forEach { columnIndex ->
                if (rowIndex == -1 && columnIndex == -1) {
                    // Empty cell at top-left.
                    div {}
                } else if (rowIndex == -1) {
                    // Cell along the top.
                    layoutSizeCell {
                        attrs.key = "col$columnIndex"
                        attrs.dimen = tab.columns[columnIndex]
                        attrs.type = "Column"
                        attrs.onChange = props.onLayoutChange
                        attrs.onDuplicate = { tab.duplicateColumn(columnIndex); props.onLayoutChange(true) }
                        attrs.onDelete = { tab.deleteColumn(columnIndex); props.onLayoutChange(true) }
                    }
                } else if (columnIndex == -1) {
                    // Extra cell on right side for "add column".
                    div(+styles.gridAreaEdge) {
                        if (rowIndex == 0) {
                            IconButton {
                                attrs.onClick = {
                                    tab.appendColumn()
                                    props.onLayoutChange(true)
                                }
                                icon(CommonIcons.Add)
                            }
                        }
                    }

                    // Cell along the left.
                    layoutSizeCell {
                        attrs.key = "row$rowIndex"
                        attrs.dimen = tab.rows[rowIndex]
                        attrs.type = "Row"
                        attrs.onChange = props.onLayoutChange
                        attrs.onDuplicate = { tab.duplicateRow(rowIndex); props.onLayoutChange(true) }
                        attrs.onDelete = { tab.deleteRow(rowIndex); props.onLayoutChange(true) }
                    }
                } else {
                    layoutAreaCell {
                        attrs.key = "area$rowIndex,$columnIndex"
                        attrs.layouts = props.layouts
                        attrs.tab = tab
                        attrs.columnIndex = columnIndex
                        attrs.rowIndex = rowIndex
                        attrs.onChange = props.onLayoutChange
                    }
                }
            }
        }

        // Fill out "add column" column.
        div(+styles.gridAreaEdge) {}

        // Extra cell on bottom left for "add row".
        div(+styles.gridAreaEdge) {
            IconButton {
                attrs.onClick = {
                    tab.appendRow()
                    props.onLayoutChange(true)
                }
                icon(CommonIcons.Add)
            }
        }
    }
}

external interface LegacyLayoutEditorProps : Props {
    var layouts: MutableLayouts
    var tab: MutableLegacyTab
    var onLayoutChange: (pushToUndoStack: Boolean) -> Unit
}

fun RBuilder.legacyLayoutEditor(handler: RHandler<LegacyLayoutEditorProps>) =
    child(LegacyLayoutEditorView, handler = handler)