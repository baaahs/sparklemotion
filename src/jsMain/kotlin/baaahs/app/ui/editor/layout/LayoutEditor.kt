package baaahs.app.ui.editor.layout

import baaahs.app.ui.appContext
import baaahs.show.mutable.MutableShow
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.Display
import kotlinx.css.GridTemplateColumns
import kotlinx.css.display
import kotlinx.css.gridTemplateColumns
import materialui.components.tab.tab
import materialui.components.tabs.tabs
import react.*
import react.dom.div
import styled.inlineStyles

val LayoutEditor = xComponent<LayoutEditorProps>("LayoutEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layoutEditor

    val currentTabIndex = 0
    val mutableLayouts = props.mutableShow.layouts
    val currentTab = mutableLayouts.formats[props.format]!!.tabs[currentTabIndex]
    var panelIds = mutableLayouts.panels.keys

    div {
        tabs {
            attrs.value = props.format

            tab {
                attrs.value = "default"
                attrs.label { +"Default" }
            }
        }

        div(+styles.editorGrid) {
            // grid holder
            inlineStyles {
                display = Display.grid
                gridTemplateColumns = GridTemplateColumns(
                    MutableList(currentTab.columns.size + 1) { "1fr" }.joinToString(" ")
                )
//                        gridTemplateAreas = GridTemplateAreas(areas.joinToString(" ") { "\"$it\"" })
//                        gridTemplateColumns = GridTemplateColumns(currentTab.columns.joinToString(" "))
//                        gridTemplateRows = GridTemplateRows(currentTab.rows.joinToString(" "))
            }

            (-1 until currentTab.rows.size).forEach { rowIndex ->
                (-1 until currentTab.columns.size).forEach { columnIndex ->
                    if (rowIndex == -1 && columnIndex == -1) {
                        // Empty cell at top-left.
                        div {}
                    } else if (rowIndex == -1) {
                        // Cell along the top.
                        layoutSizeCell {
                            attrs.key = "col$columnIndex"
                            attrs.dimen = currentTab.columns[columnIndex]
                            attrs.onChange = props.onGridChange
                        }
                    } else if (columnIndex == -1) {
                        // Cell along the left.
                        layoutSizeCell {
                            attrs.key = "row$rowIndex"
                            attrs.dimen = currentTab.rows[rowIndex]
                            attrs.onChange = props.onGridChange
                        }
                    } else {
                        layoutAreaCell {
                            attrs.key = "area$rowIndex,$columnIndex"
                            attrs.layouts = mutableLayouts
                            attrs.tab = currentTab
                            attrs.columnIndex = columnIndex
                            attrs.rowIndex = rowIndex
                            attrs.onChange = props.onGridChange
                        }
                    }
                }
            }
        }
    }
}

external interface LayoutEditorProps : RProps {
    var mutableShow: MutableShow
    var format: String
    var onGridChange: () -> Unit
}

fun RBuilder.layoutEditor(handler: RHandler<LayoutEditorProps>) =
    child(LayoutEditor, handler = handler)