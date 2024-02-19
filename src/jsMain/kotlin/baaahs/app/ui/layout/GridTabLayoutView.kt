package baaahs.app.ui.layout

import baaahs.app.ui.AppContext
import baaahs.app.ui.appContext
import baaahs.app.ui.controlsPalette
import baaahs.app.ui.editor.AddControlToGrid
import baaahs.app.ui.editor.Editor
import baaahs.control.OpenButtonGroupControl
import baaahs.plugin.AddControlMenuItem
import baaahs.show.live.ControlProps
import baaahs.show.live.GridLayoutControlDisplay
import baaahs.show.live.OpenIGridLayout
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutableShow
import baaahs.ui.and
import baaahs.ui.gridlayout.ItemCallback
import baaahs.ui.gridlayout.Layout
import baaahs.ui.gridlayout.LayoutGrid
import baaahs.ui.gridlayout.gridLayout
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.unknown
import baaahs.util.useResizeListener
import external.react_resizable.buildResizeHandle
import kotlinx.css.*
import materialui.icon
import mui.base.Portal
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

    var layoutDimens by state<Pair<Int, Int>?> { null }
    val gridLayout = props.tab
    val columns = gridLayout.columns
    val rows = gridLayout.rows

    var showAddMenu by state<AddMenuContext?> { null }
    val closeAddMenu by handler { showAddMenu = null }

    val editMode = observe(appContext.showManager.editMode)
    var draggingItem by state<String?> { null }

    val gridLayoutEditor = props.tabEditor
    val handleLayoutChange by handler(gridLayout, gridLayoutEditor) { newLayout: Layout, stillDragging: Boolean ->
        if (stillDragging) return@handler
        appContext.showManager.openShow?.edit {
            val mutableShow = this
            gridLayoutEditor.edit(mutableShow) {
                applyChanges(gridLayout.items, newLayout, mutableShow)
            }
            appContext.showManager.onEdit(mutableShow)
        }
        Unit
    }

    val handleDragStart: ItemCallback by handler {
            layout, oldItem, newItem, placeholder, e, element ->
        draggingItem = newItem.i
    }
    val handleDragStop: ItemCallback by handler {
            layout, oldItem, newItem, placeholder, e, element ->
        draggingItem = null
    }

    val handleEmptyGridCellClick by eventHandler { e ->
        val target = e.currentTarget as HTMLElement
        val dataset = target.dataset.asDynamic()
        val x = (dataset.cellX as String).toInt()
        val y = (dataset.cellY as String).toInt()
        showAddMenu = AddMenuContext(target, x, y, 1, 1)
    }

    val containerDiv = ref<HTMLDivElement>()
    useResizeListener(containerDiv) { width, height ->
        layoutDimens = width to height
    }
    val margin = 5
    val itemPadding = 5

    val layoutGrid = memo(columns, rows, gridLayout, draggingItem) {
        LayoutGrid(columns, rows, gridLayout.items, draggingItem)
    }

    val openShow = showManager.openShow!!
    val enabledSwitchState = openShow.getEnabledSwitchState()
    val controlDisplay = memo(openShow, enabledSwitchState) {
        GridLayoutControlDisplay(openShow)
            .also { withCleanup { it.release() } }
    }

    val genericControlProps = memo(controlDisplay) {
        ControlProps(controlDisplay)
    }


    div(+layoutStyles.gridOuterContainer and
            (+if (editMode.isOn) layoutStyles.editModeOn else layoutStyles.editModeOff) and
            +if (gridLayoutContext.dragging) layoutStyles.dragging else layoutStyles.notDragging
    ) {
        ref = containerDiv

        layoutDimens?.let { layoutDimens ->
            val (layoutWidth, layoutHeight) = layoutDimens
            val gridRowHeight = (layoutHeight.toDouble() - margin) / rows - itemPadding

            gridBackground {
                attrs.layoutGrid = layoutGrid
                attrs.margin = margin
                attrs.itemPadding = itemPadding
                attrs.layoutWidth = layoutWidth
                attrs.gridRowHeight = gridRowHeight
                attrs.onGridCellClick = handleEmptyGridCellClick
            }

            gridLayout {
                attrs.id = "top"
                attrs.className = +layoutStyles.gridContainer
                attrs.width = layoutWidth.toDouble()
                attrs.autoSize = false
                attrs.cols = columns
                attrs.rowHeight = gridRowHeight
                attrs.maxRows = rows
                attrs.margin = 5 to 5
                attrs.layout = layoutGrid.layout
                attrs.onLayoutChange = handleLayoutChange
                attrs.resizeHandle = ::buildResizeHandle
                attrs.disableDrag = !editMode.isOn
                attrs.disableResize = !editMode.isOn
                attrs.isEverEditable = editMode.isAvailable
                attrs.isDroppable = editMode.isOn
                attrs.onDragStart = handleDragStart
                attrs.onDragStop = handleDragStop

                gridLayout.items.forEachIndexed { index, item ->
                    val gridCellStyles = +layoutStyles.gridCell and
                            if (item.control is OpenButtonGroupControl) layoutStyles.groupGridCell else null

                    div(gridCellStyles) {
                        key = item.control.id

                        val gridItemId = item.control.id
                        val editor = object : Editor<MutableIGridLayout> {
                            override val title: String = "Grid tab layout editor for $gridItemId"

                            override fun edit(mutableShow: MutableShow, block: MutableIGridLayout.() -> Unit) {
                                mutableShow.editLayouts {
                                    props.tabEditor.edit(mutableShow) {
                                        val gridItem = items.firstOrNull { it.control.asBuiltId == gridItemId }
                                            ?: error(unknown("item", gridItemId, items.map { it.control.asBuiltId }))
                                        val layout = gridItem.layout
                                        if (layout != null) {
                                            block(layout)
                                        } else {
                                            this@xComponent.logger.error { "No layout for $gridItemId." }
                                        }
                                    }
                                }
                            }

                            override fun delete(mutableShow: MutableShow) {
                                props.tabEditor.edit(mutableShow) {
                                    items.removeAt(index)
                                }
                            }
                        }

                        gridItem {
                            attrs.control = item.control
                            attrs.controlProps = genericControlProps.withLayout(item.layout, editor, item.gridDimens)
                            attrs.className = -layoutStyles.controlBox
                        }
                    }
                }
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

    if (editMode.isAvailable) {
        Portal {
            controlsPalette {
                attrs.controlDisplay = controlDisplay
                attrs.controlProps = genericControlProps
                attrs.show = openShow
            }
        }
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
    var tab: OpenIGridLayout
    var controlProps: ControlProps
    var tabEditor: Editor<MutableIGridLayout>
}

fun RBuilder.gridTabLayout(handler: RHandler<GridTabLayoutProps>) =
    child(GridTabLayoutView, handler = handler)