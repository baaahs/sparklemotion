package baaahs.app.ui.editor.layout

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.show.mutable.MutableLegacyTab
import baaahs.show.mutable.MutableShow
import baaahs.ui.Prompt
import baaahs.ui.unaryPlus
import baaahs.ui.value
import baaahs.ui.xComponent
import kotlinx.css.Display
import kotlinx.css.GridTemplateColumns
import kotlinx.css.display
import kotlinx.css.gridTemplateColumns
import materialui.icon
import mui.material.IconButton
import mui.material.Tab
import mui.material.Tabs
import react.*
import react.dom.div
import react.dom.events.SyntheticEvent
import styled.inlineStyles

val LayoutEditor = xComponent<LayoutEditorProps>("LayoutEditor") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layoutEditor

    var currentTabIndex by state { 0 }
    val mutableLayouts = props.mutableShow.layouts
    val mutableLayout = mutableLayouts.formats[props.format]!!
    val currentTab = mutableLayout.tabs[currentTabIndex] as MutableLegacyTab

    val handleTabChange by syntheticEventHandler { e: SyntheticEvent<*, *>, _: Boolean ->
        currentTabIndex = e.target.value.toInt()
    }

    val handleNewTabClick by mouseEventHandler {
        appContext.prompt(
            Prompt(
                "Create New Tab",
                "Enter a name for your new tab.",
                "",
                fieldLabel = "Tab Name",
                cancelButtonLabel = "Cancel",
                submitButtonLabel = "Create",
                isValid = { name ->
                    if (name.isBlank()) return@Prompt "No name given."

                    if (mutableLayout.tabs.any { it.title == name }) {
                        "Looks like there's already a tab named named \"$name\"."
                    } else null
                },
                onSubmit = { name ->
                    val newTab = MutableLegacyTab(name)
                    mutableLayout.tabs.add(newTab)
                    props.onGridChange()
                }
            )
        )
    }

    div {
        Tabs {
            attrs.value = currentTabIndex.toString()
            attrs.onChange = handleTabChange

            mutableLayout.tabs.forEachIndexed { index, tab ->
                Tab {
                    attrs.value = index.toString()
                    attrs.label = buildElement { +currentTab.title }
                }
            }

            Tab {
                attrs.value = currentTabIndex.toString()
                attrs.icon = buildElement { icon(CommonIcons.Add) }
                attrs.disabled = true
                attrs.onClick = handleNewTabClick
            }
        }

        div(+styles.editorGrid) {
            // grid holder
            inlineStyles {
                display = Display.grid
                gridTemplateColumns = GridTemplateColumns(
                    MutableList(currentTab.columns.size + 1) { "1fr" }.joinToString(" ") + " auto"
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
                            attrs.type = "Column"
                            attrs.onChange = props.onGridChange
                            attrs.onDuplicate = { currentTab.duplicateColumn(columnIndex); props.onGridChange() }
                            attrs.onDelete = { currentTab.deleteColumn(columnIndex); props.onGridChange() }
                        }
                    } else if (columnIndex == -1) {
                        // Extra cell on right side for "add column".
                        div(+styles.gridAreaEdge) {
                            if (rowIndex == 0) {
                                IconButton {
                                    attrs.onClick = {
                                        currentTab.appendColumn()
                                        props.onGridChange()
                                    }
                                    icon(CommonIcons.Add)
                                }
                            }
                        }

                        // Cell along the left.
                        layoutSizeCell {
                            attrs.key = "row$rowIndex"
                            attrs.dimen = currentTab.rows[rowIndex]
                            attrs.type = "Row"
                            attrs.onChange = props.onGridChange
                            attrs.onDuplicate = { currentTab.duplicateRow(rowIndex); props.onGridChange() }
                            attrs.onDelete = { currentTab.deleteRow(rowIndex); props.onGridChange() }
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

            // Fill out "add column" column.
            div(+styles.gridAreaEdge) {}

            // Extra cell on bottom left for "add row".
            div(+styles.gridAreaEdge) {
                IconButton {
                    attrs.onClick = {
                        currentTab.appendRow()
                        props.onGridChange()
                    }
                    icon(CommonIcons.Add)
                }
            }
        }
    }
}

external interface LayoutEditorProps : Props {
    var mutableShow: MutableShow
    var format: String
    var onGridChange: () -> Unit
}

fun RBuilder.layoutEditor(handler: RHandler<LayoutEditorProps>) =
    child(LayoutEditor, handler = handler)