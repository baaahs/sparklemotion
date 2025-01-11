package baaahs.app.ui.layout

import baaahs.app.ui.AppContext
import baaahs.app.ui.appContext
import baaahs.app.ui.editor.AddControlToGrid
import baaahs.app.ui.editor.Editor
import baaahs.plugin.AddControlMenuItem
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenGridTab
import baaahs.show.mutable.MutableGridTab
import baaahs.show.mutable.MutableIGridLayout
import baaahs.ui.*
import baaahs.ui.gridlayout.*
import baaahs.util.useResizeListener
import kotlinx.css.*
import materialui.icon
import mui.icons.material.Add
import mui.material.*
import react.*
import react.dom.div
import react.dom.onClick
import styled.StyleSheet
import web.dom.Element
import web.html.HTMLDivElement
import web.html.HTMLElement

private val GridTabLayoutView = xComponent<GridTabLayoutProps>("GridTabLayout") { props ->
    console.log("GridTabLayoutView render ", renderCounter)

    val appContext = useContext(appContext)
    val showManager = observe(appContext.showManager)
    val layoutStyles = appContext.allStyles.layout

    val gridLayoutContext = useContext(dragNDropContext).gridLayoutContext
    observe(gridLayoutContext)

    var layoutPxDimens by state<Pair<Int, Int>?> { null }
    val gridLayout = props.tab

    var showAddMenu by state<AddMenuContext?> { null }
    val closeAddMenu by handler { showAddMenu = null }

    val editMode = observe(appContext.showManager.editMode)

    val gridLayoutEditor = props.tabEditor
    val handleLayoutChange by handler(gridLayout, gridLayoutEditor) { newGridModel: GridModel/*, stillDragging: Boolean*/ ->
//        if (stillDragging) return@handler
        appContext.showManager.openShow?.edit {
            val mutableShow = this
            gridLayoutEditor.edit(mutableShow) {
                val mutableTab = (this as MutableGridTab)
                mutableTab.applyChanges(newGridModel)

//                applyChanges(gridLayout.items, newLayout, mutableShow)
            }
            appContext.showManager.onEdit(mutableShow)
        }
        Unit
    }

//    val handleDragStart: ItemCallback by handler {
//            layout, oldItem, newItem, placeholder, e, element ->
//        draggingItem = newItem.i
//    }
//    val handleDragStop: ItemCallback by handler {
//            layout, oldItem, newItem, placeholder, e, element ->
//        draggingItem = null
//    }

    val handleEmptyGridCellClick by eventHandler(editMode) { e ->
        if (editMode.isOff) return@eventHandler

        val target = e.currentTarget as HTMLElement
        val dataset = target.dataset.asDynamic()
        val nodeId = dataset.cellParentNodeId as String
        val x = (dataset.cellX as String).toInt()
        val y = (dataset.cellY as String).toInt()
        showAddMenu = AddMenuContext(target, nodeId, x, y, 1, 1)
    }

    val containerDiv = ref<HTMLDivElement>()
    useResizeListener(containerDiv) { width, height ->
        layoutPxDimens = width to height
    }
    val margin = 5
    val itemPadding = 5

//    val layoutGrid = memo(columns, rows, gridLayout, draggingItem) {
//        LayoutGrid(columns, rows, gridLayout.items, draggingItem)
//    }

    val openShow = showManager.openShow!!
    val controlDisplay = openShow.getSnapshot().controlsInfo

    val genericControlProps = memo(controlDisplay) { ControlProps(openShow) }

    val gridModel = memo(props.tab.gridTab) { props.tab.gridTab.createModel() }

    val doRender: (String) -> ReactNode by handler(openShow, props.controlProps) { id: String ->
        val openControl = openShow.allControls.find { it.id == id }
        if (openControl == null)
            println("GridRootView: No control found with id \"$id\"")

        openControl?.let { openControl ->
            buildElement {
                gridItem {
                    attrs.control = openControl
                    attrs.controlProps = genericControlProps
                }
                (openControl.getView(props.controlProps) as JsView)
                    .render(this)
            }
        } ?: "".asTextNode()
    }

    val doRenderEmptyCell: RenderEmptyCell = memo(handleEmptyGridCellClick) {
        RenderEmptyCell { parentNode, cell, ref ->
            buildElement {
                div(+layoutStyles.emptyGridCell) {
                    this.ref = ref

                    attrs["data-cell-parent-node-id"] = parentNode.id
                    attrs["data-cell-x"] = cell.x
                    attrs["data-cell-y"] = cell.y

                    attrs.onClick = handleEmptyGridCellClick.withMouseEvent()

                    icon(Add)
                }
            }

        }
    }
//    gridModel.change {
//        this.columns = columns
//        this.rows = rows
//    }
    div(+layoutStyles.gridOuterContainer and
            (+if (editMode.isOn) layoutStyles.editModeOn else layoutStyles.editModeOff) and
            +if (gridLayoutContext.dragging) layoutStyles.dragging else layoutStyles.notDragging
    ) {
        ref = containerDiv

        layoutPxDimens?.let { layoutDimens ->
            gridManager {
                attrs.gridModel = gridModel
                attrs.render = doRender
                attrs.isEditable = editMode.isOn
                attrs.onChange = handleLayoutChange
                attrs.renderEmptyCell = doRenderEmptyCell
            }

//                gridLayout {
//                    attrs.id = "top"
//                    attrs.className = +layoutStyles.gridContainer
//                    attrs.width = layoutWidth.toDouble()
//                    attrs.autoSize = false
//                    attrs.cols = columns
//                    attrs.rowHeight = gridRowHeight
//                    attrs.maxRows = rows
//                    attrs.margin = 5 to 5
//                    attrs.layout = layoutGrid.layout
//                    attrs.onLayoutChange = handleLayoutChange
//                    attrs.resizeHandle = ::buildResizeHandle
//                    attrs.disableDrag = !editMode.isOn
//                    attrs.disableResize = !editMode.isOn
//                    attrs.isEverEditable = editMode.isAvailable
//                    attrs.isDroppable = editMode.isOn
//                    attrs.onDragStart = handleDragStart
//                    attrs.onDragStop = handleDragStop
//
//                    gridLayout.items.forEachIndexed { index, item ->
//                        val gridCellStyles = +layoutStyles.gridCell and
//                                if (item.control is OpenButtonGroupControl) layoutStyles.groupGridCell else null
//
//                        div(gridCellStyles) {
//                            key = item.control.id
//                            val editor = CellEditor(item.control, props.tabEditor)
//
//                            gridItem {
//                                attrs.control = item.control
//                                attrs.controlProps = genericControlProps.withLayout(item.layout, editor, item.gridDimens)
//                                attrs.className = -layoutStyles.controlBox
//                            }
//                        }
//                    }
//                }
        }
    }

    showAddMenu?.let { addMenuContext ->
        Menu {
            attrs.anchorEl = { addMenuContext.anchorEl }
            attrs.open = true
            attrs.onClose = closeAddMenu

            appContext.plugins.addControlMenuItems.forEach { addControlMenuItem ->
                addMenuContext.apply {
                    createMenuItem(gridLayoutEditor, addControlMenuItem, appContext, closeAddMenu)
                }
            }
        }
    }

    // There's no such thing as unplaced controls anymore really.
//    if (editMode.isAvailable) {
//        Portal {
//            controlsPalette {
//                attrs.controlsInfo = controlDisplay
//                attrs.controlProps = genericControlProps
//                attrs.show = openShow
//            }
//        }
//    }
}


class AddMenuContext(
    val anchorEl: Element,
    val parentNodeId: String,
    val column: Int,
    val row: Int,
    val width: Int = 1,
    val height: Int = 1
) {
    fun RElementBuilder<MenuProps>.createMenuItem(
        gridLayoutEditor: Editor<MutableIGridLayout>,
        addControlMenuItem: AddControlMenuItem,
        appContext: AppContext,
        closeAddMenu: () -> Unit,
    ) {
        MenuItem {
            attrs.onClick = {
                val editIntent = AddControlToGrid(
                    gridLayoutEditor,
                    column, row,
                    width, height,
                    addControlMenuItem.createControlFn
                )
                appContext.openEditor(editIntent)
                closeAddMenu()
            }

            ListItemIcon { icon(addControlMenuItem.icon) }
            ListItemText { +addControlMenuItem.label }
        }
    }
}

object Styles : StyleSheet("ui-layout-grid", isStatic = true) {
    val gridItem by css {
        border = Border(1.px, BorderStyle.solid, Color.orange)
    }
}

external interface GridTabLayoutProps : Props {
    var tab: OpenGridTab
    var controlProps: ControlProps
    var tabEditor: Editor<MutableIGridLayout>
}

fun RBuilder.gridTabLayout(handler: RHandler<GridTabLayoutProps>) =
    child(GridTabLayoutView, handler = handler)