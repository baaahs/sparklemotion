package baaahs.app.ui.layout

import baaahs.app.ui.AppContext
import baaahs.app.ui.appContext
import baaahs.app.ui.editor.AddControlToGrid
import baaahs.app.ui.editor.Editor
import baaahs.plugin.AddControlMenuItem
import baaahs.show.live.*
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutableShow
import baaahs.ui.*
import baaahs.ui.gridlayout.*
import baaahs.unknown
import baaahs.util.Logger
import baaahs.util.useResizeListener
import kotlinx.css.*
import materialui.icon
import mui.material.*
import react.*
import react.dom.div
import styled.StyleSheet
import web.dom.Element
import web.html.HTMLDivElement
import web.html.HTMLElement

private val GridTabLayoutView = xComponent<GridTabLayoutProps>("GridTabLayout") { props ->
    val appContext = useContext(appContext)
    val showManager = observe(appContext.showManager)
    val layoutStyles = appContext.allStyles.layout

    val gridLayoutContext = useContext(dragNDropContext).gridLayoutContext
    observe(gridLayoutContext)

    var layoutPxDimens by state<Pair<Int, Int>?> { null }
    val gridLayout = props.tab
    val columns = gridLayout.columns
    val rows = gridLayout.rows

    var showAddMenu by state<AddMenuContext?> { null }
    val closeAddMenu by handler { showAddMenu = null }

    val editMode = observe(appContext.showManager.editMode)
    var draggingItem by state<String?> { null }

    val gridLayoutEditor = props.tabEditor
//    val handleLayoutChange by handler(gridLayout, gridLayoutEditor) { newLayout: Layout, stillDragging: Boolean ->
//        if (stillDragging) return@handler
//        appContext.showManager.openShow?.edit {
//            val mutableShow = this
//            gridLayoutEditor.edit(mutableShow) {
//                applyChanges(gridLayout.items, newLayout, mutableShow)
//            }
//            appContext.showManager.onEdit(mutableShow)
//        }
//        Unit
//    }

//    val handleDragStart: ItemCallback by handler {
//            layout, oldItem, newItem, placeholder, e, element ->
//        draggingItem = newItem.i
//    }
//    val handleDragStop: ItemCallback by handler {
//            layout, oldItem, newItem, placeholder, e, element ->
//        draggingItem = null
//    }

    val handleEmptyGridCellClick by eventHandler { e ->
        val target = e.currentTarget as HTMLElement
        val dataset = target.dataset.asDynamic()
        val x = (dataset.cellX as String).toInt()
        val y = (dataset.cellY as String).toInt()
        showAddMenu = AddMenuContext(target, x, y, 1, 1)
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

//    val allViews = memo(gridLayout) {
//        gridLayout.visitItems { }
//        CacheBuilder<String, ReactNode?> { control ->
//            val item = allThings.find { item -> item.control == control }
//                ?: return@CacheBuilder "can't find!".asTextNode()
//
//            val gridCellStyles = +layoutStyles.gridCell and
//                    if (item.control is OpenButtonGroupControl) layoutStyles.groupGridCell else null
//            buildElement {
//                div(gridCellStyles) {
//                    key = item.control.id
//
//                    val gridItemId = item.control.id
//                    val editor = CellEditor(item.control, props.tabEditor)
//
//                    "${item.control.id} — ${item.column},${item.row} ${item.width}x${item.height}".asTextNode()
//                    gridItem {
//                        attrs.control = item.control
//                        attrs.controlProps = genericControlProps.withLayout(item.layout, editor, item.gridDimens)
//                        attrs.className = -layoutStyles.controlBox
//                    }
//                }
//            }
//        }
//    }

    val viewRoot = memo(gridLayout) {
        ViewRoot(gridLayout, 5, 5)
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
            val (layoutWidth, layoutHeight) = layoutDimens
            val gridRowHeight = (layoutHeight.toDouble() - margin) / rows - itemPadding

//            gridBackground {
//                attrs.layoutGrid = layoutGrid
//                attrs.margin = margin
//                attrs.itemPadding = itemPadding
//                attrs.layoutWidth = layoutWidth
//                attrs.gridRowHeight = gridRowHeight
//                attrs.onGridCellClick = handleEmptyGridCellClick
//            }

            if (true) {
                gridRoot {
                    attrs.viewRoot = viewRoot
                    attrs.controlProps = genericControlProps
                }
            } else {
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

class CellEditor(
    control: OpenControl,
    private val tabEditor: Editor<MutableIGridLayout>
) : Editor<MutableIGridLayout> {
    val gridItemId = control.id
    override val title: String = "Grid tab layout editor for $gridItemId"

    override fun edit(mutableShow: MutableShow, block: MutableIGridLayout.() -> Unit) {
        mutableShow.editLayouts {
            tabEditor.edit(mutableShow) {
                val gridItem = items.firstOrNull { it.control.asBuiltId == gridItemId }
                    ?: error(unknown("item", gridItemId, items.map { it.control.asBuiltId }))
                val layout = gridItem.layout
                if (layout != null) {
                    block(layout)
                } else {
                    logger.error { "No layout for $gridItemId." }
                }
            }
        }
    }

    override fun delete(mutableShow: MutableShow) {
        tabEditor.edit(mutableShow) {
            items.removeAll { it.control.asBuiltId == gridItemId }
        }
    }

    companion object {
        private val logger = Logger<CellEditor>()
    }
}

class AddMenuContext(
    val anchorEl: Element,
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