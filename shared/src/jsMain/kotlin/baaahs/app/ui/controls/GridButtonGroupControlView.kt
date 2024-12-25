package baaahs.app.ui.controls

import baaahs.control.OpenButtonGroupControl
import baaahs.show.live.ControlProps
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler

private val GridButtonGroupControlView = xComponent<GridButtonGroupProps>("GridButtonGroupControl") { props ->
//    val appContext = useContext(appContext)
//    val layoutStyles = appContext.allStyles.layout
//    val editMode = observe(appContext.showManager.editMode)
//
//    val buttonGroupControl = props.buttonGroupControl
//
//    var layoutDimens by state<Pair<Int, Int>?> { null }
//    val gridLayout = props.controlProps.layout
//        ?: OpenGridLayout(1, 1, true, emptyList())
//    val editor = props.controlProps.layoutEditor
//        ?: error("Huh? No editor provided?")
//    val parentDimens = props.controlProps.parentDimens
//    val gridDimens = if (gridLayout.matchParent) parentDimens ?: gridLayout.gridDimens else gridLayout.gridDimens
//    val (columns, rows) = gridDimens
//    val margin = 5
//    val itemPadding = 5
//
//    val containerRef = ref<Element>()
//    useResizeListener(containerRef) { width, height ->
//        layoutDimens = width to height
//    }
//
//    val gridLayoutEditor = props.controlProps.layoutEditor
//        ?: error("No layout editor!")
//
//    val handleLayoutChange by handler(gridLayout, editor) { newLayout: Layout, stillDragging: Boolean ->
//        if (stillDragging) return@handler
//        appContext.showManager.openShow?.edit {
//            val mutableShow = this
//            editor.edit(mutableShow) {
//                applyChanges(gridLayout.items, newLayout, mutableShow)
//            }
//            appContext.showManager.onEdit(mutableShow)
//        }
//        Unit
//    }
//
//    var showAddMenu by state<AddMenuContext?> { null }
//    val closeAddMenu by handler {
//        showAddMenu = null
//        appContext.gridLayoutContext.draggingDisabled = false
//    }
//
//    var draggingItem by state<String?> { null }
//    val layoutGrid = memo(columns, rows, gridLayout, draggingItem) {
//        LayoutGrid(columns, rows, gridLayout.items, draggingItem)
//    }
//
//    // Don't bubble event up to container grid.
//    val handleEmptyGridCellMouseDown by eventHandler { e ->
//        e.stopPropagation()
//    }
//
//    val handleEmptyGridCellClick by eventHandler { e ->
//        val target = e.currentTarget as HTMLElement
//        val dataset = target.dataset.asDynamic()
//        val x = (dataset.cellX as String).toInt()
//        val y = (dataset.cellY as String).toInt()
//        showAddMenu = AddMenuContext(target, x, y, 1, 1)
//
//        // Ignore dragstart events in GridItem while a menu is visible.
//        appContext.gridLayoutContext.draggingDisabled = true
//        e.stopPropagation()
//    }
//
//    val layout = Layout(gridLayout.items.map { gridItem ->
//        LayoutItem(gridItem.column, gridItem.row, gridItem.width, gridItem.height, gridItem.control.id)
//    }, gridLayout.columns, gridLayout.rows)
//    val controls = gridLayout.items.associate { it.control.id to it.control }
//    val layouts = gridLayout.items.associate { it.control.id to it.layout }
//
//    val handleGridItemClick by mouseEventHandler(gridLayout.items) { e ->
//        if (containerRef.current?.isParentOf(e.target as Element) == true) {
//            val clickedItemIndex = (e.currentTarget as HTMLElement)
//                .dataset["gridIndex"]
//                ?.toInt()
//            val clickedItem = clickedItemIndex?.let { gridLayout.items[it] }
//            if (clickedItem?.control is OpenButtonControl) {
//                gridLayout.items.forEachIndexed { index, it ->
//                    (it.control as? OpenButtonControl)?.isPressed = index == clickedItemIndex
//                }
//                e.stopPropagation()
//            }
//        }
//    }
//
//    Card {
//        attrs.className = -layoutStyles.buttonGroupCard and
//                (+if (editMode.isOn) layoutStyles.editModeOn else layoutStyles.editModeOff) // and
////                    +if (gridLayoutContext.dragging) layoutStyles.dragging else layoutStyles.notDragging
//
//        if (buttonGroupControl.title.isNotBlank() && buttonGroupControl.showTitle) {
//            header(+layoutStyles.buttonGroupHeader) {
//                +buttonGroupControl.title
//            }
//        }
//
//        div(+layoutStyles.buttonGroupGrid) {
//            ref = containerRef
//
//            layoutDimens?.let { layoutDimens ->
//                val (layoutWidth, layoutHeight) = layoutDimens
//                val gridRowHeight = (layoutHeight.toDouble() - margin) / rows - itemPadding
//
//                gridBackground {
//                    attrs.layoutGrid = layoutGrid
//                    attrs.margin = margin
//                    attrs.itemPadding = itemPadding
//                    attrs.layoutWidth = layoutWidth
//                    attrs.gridRowHeight = gridRowHeight
//                    attrs.onGridCellMouseDown = handleEmptyGridCellMouseDown
//                    attrs.onGridCellClick = handleEmptyGridCellClick
//                }
//
//                gridLayout {
//                    attrs.id = buttonGroupControl.id
//                    attrs.className = +layoutStyles.gridContainer
//                    attrs.width = layoutWidth.toDouble()
//                    attrs.autoSize = false
//                    attrs.cols = columns
//                    attrs.rowHeight = gridRowHeight
//                    attrs.maxRows = rows
//                    attrs.margin = 5 to 5
//                    attrs.layout = layout
//                    attrs.onLayoutChange = handleLayoutChange
//                    attrs.resizeHandle = ::buildResizeHandle
//                    attrs.disableDrag = !editMode.isOn
//                    attrs.disableResize = !editMode.isOn
//                    attrs.isEverEditable = editMode.isAvailable
//                    attrs.isDroppable = editMode.isOn
//                    attrs.isBounded = false
//
//                    layout.items.forEachIndexed { index, layoutItem ->
//                        child(ReactHTML.div) {
//                            key = layoutItem.i
//                            attrs.className = -layoutStyles.gridCell
//                            if (editMode.isOff) {
//                                attrs.asDynamic()["data-grid-index"] = index
//
//                                if (!props.buttonGroupControl.allowMultiple) {
//                                    attrs.onClickCapture = handleGridItemClick
//                                }
//                            }
//
//                            val subEditor = object : Editor<MutableIGridLayout> {
//                                override val title: String = "Grid buttongroup layout editor for ${layoutItem.i}"
//
//                                override fun edit(mutableShow: MutableShow, block: MutableIGridLayout.() -> Unit) {
//                                    mutableShow.editLayouts {
//                                        editor.edit(mutableShow) {
//                                            block(items[index].layout
//                                                ?: error("Couldn't find item for ${layoutItem.i}."))
//                                        }
//                                    }
//                                }
//
//                                override fun delete(mutableShow: MutableShow) {
//                                    mutableShow.editLayouts {
//                                        editor.edit(mutableShow) {
//                                            items.removeAt(index)
//                                        }
//                                    }
//                                }
//                            }
//
//                            gridItem {
//                                attrs.control = controls[layoutItem.i]!!
//                                attrs.controlProps = props.controlProps.withLayout(
//                                    layouts[layoutItem.i], subEditor, layoutItem.gridDimens)
//                                attrs.className = -layoutStyles.controlBox
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    showAddMenu?.let { addMenuContext ->
//        Menu {
//            attrs.anchorEl = { addMenuContext.anchorEl }
//            attrs.open = true
//            attrs.onClose = closeAddMenu
//
//            appContext.plugins.addControlMenuItems
//                .filter { it.validForButtonGroup }
//                .forEach { addControlMenuItem ->
//                    addMenuContext.apply {
//                        createMenuItem(gridLayoutEditor, addControlMenuItem, appContext, closeAddMenu)
//                    }
//                }
//        }
//    }
}

external interface GridButtonGroupProps : Props {
    var controlProps: ControlProps
    var buttonGroupControl: OpenButtonGroupControl
}

fun RBuilder.gridButtonGroupControl(handler: RHandler<GridButtonGroupProps>) =
    child(GridButtonGroupControlView, handler = handler)