package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.Editor
import baaahs.app.ui.layout.AddMenuContext
import baaahs.app.ui.layout.gridItem
import baaahs.control.OpenButtonControl
import baaahs.control.OpenButtonGroupControl
import baaahs.forEach
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenGridLayout
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutableShow
import baaahs.ui.*
import baaahs.ui.gridlayout.*
import baaahs.util.useResizeListener
import external.react_resizable.buildResizeHandle
import kotlinx.css.*
import kotlinx.dom.hasClass
import kotlinx.js.jso
import materialui.icon
import mui.icons.material.Add
import mui.material.Card
import mui.material.Menu
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.header
import react.dom.html.ReactHTML
import react.dom.onClick
import react.dom.onMouseDown
import react.useContext
import styled.inlineStyles

private val GridButtonGroupControlView = xComponent<GridButtonGroupProps>("GridButtonGroupControl") { props ->
    val appContext = useContext(appContext)
    val controlStyles = appContext.allStyles.controls
    val layoutStyles = appContext.allStyles.layout
    val editMode = observe(appContext.showManager.editMode)

    val buttonGroupControl = props.buttonGroupControl
    val onShowStateChange = props.controlProps.onShowStateChange

    var layoutDimens by state { 100 to 100 }
    val gridLayout = props.controlProps.layout
        ?: OpenGridLayout(1, 1, true, emptyList())
    val editor = props.controlProps.layoutEditor
        ?: error("Huh? No editor provided?")
    val parentDimens = props.controlProps.parentDimens
    val gridDimens = if (gridLayout.matchParent) parentDimens ?: gridLayout.gridDimens else gridLayout.gridDimens
    val (columns, rows) = gridDimens
    val (layoutWidth, layoutHeight) = layoutDimens
    val margin = 5
    val itemPadding = 5
    val gridRowHeight = (layoutHeight.toDouble() - margin) / rows - itemPadding

    val cardRef = ref<Element>()
    useResizeListener(cardRef) { _, _ ->
        cardRef.current?.children?.forEach {
            if (it.hasClass("react-grid-layout")) {
                with(it) {
                    console.log("resized ${props.buttonGroupControl.id}!", clientWidth, clientHeight, this)
                    layoutDimens = clientWidth to clientHeight
                }
            }
        }
    }

    val gridLayoutEditor = props.controlProps.layoutEditor
        ?: error("No layout editor!")

    val handleLayoutChange by handler(gridLayout, editor) { newLayout: Layout, stillDragging: Boolean ->
        if (stillDragging) return@handler
        appContext.showManager.openShow?.edit {
            val mutableShow = this
            editor.edit(mutableShow) {
                applyChanges(gridLayout.items, newLayout, mutableShow)
            }
            appContext.showManager.onEdit(mutableShow)
        }
        Unit
    }

    var showAddMenu by state<AddMenuContext?> { null }
    val closeAddMenu by handler { showAddMenu = null }

    var draggingItem by state<String?> { null }
    val layoutGrid = memo(columns, rows, gridLayout, draggingItem) {
        LayoutGrid(columns, rows, gridLayout.items, draggingItem)
    }

    val handleEmptyGridCellMouseDown by eventHandler { e ->
        e.stopPropagation()
    }

    val handleEmptyGridCellClick by eventHandler { e ->
        val target = e.currentTarget as HTMLElement
        val dataset = target.dataset.asDynamic()
        val x = (dataset.cellX as String).toInt()
        val y = (dataset.cellY as String).toInt()
        showAddMenu = AddMenuContext(target, x, y, 1, 1)
    }

    val layout = Layout(gridLayout.items.map { gridItem ->
        LayoutItem(gridItem.column, gridItem.row, gridItem.width, gridItem.height, gridItem.control.id)
    }, gridLayout.columns, gridLayout.rows)
    val controls = gridLayout.items.associate { it.control.id to it.control }
    val layouts = gridLayout.items.associate { it.control.id to it.layout }

    val handleGridItemClick by mouseEventHandler(gridLayout.items) { e ->
        if (cardRef.current?.isParentOf(e.target as Element) == true) {
            val clickedItemIndex = (e.currentTarget as HTMLElement)
                .dataset["gridIndex"]
                ?.toInt()
            val clickedItem = clickedItemIndex?.let { gridLayout.items[it] }
            if (clickedItem?.control is OpenButtonControl) {
                gridLayout.items.forEachIndexed { index, it ->
                    (it.control as? OpenButtonControl)?.isPressed = index == clickedItemIndex
                }
                onShowStateChange()
                e.stopPropagation()
            }
        }
    }

    Card {
        ref = cardRef
        attrs.classes = jso {
            root = -layoutStyles.buttonGroupCard and
                    (+if (editMode.isOn) layoutStyles.editModeOn else layoutStyles.editModeOff) // and
//                    +if (gridLayoutContext.dragging) layoutStyles.dragging else layoutStyles.notDragging
        }

        if (buttonGroupControl.title.isNotBlank() && buttonGroupControl.showTitle) {
            header(+layoutStyles.buttonGroupHeader) {
                +buttonGroupControl.title
            }
        }

        if (editMode.isAvailable) {
            div(+layoutStyles.gridBackground) {
                val positionParams = PositionParams(
                    margin to margin,
                    itemPadding to itemPadding,
                    layoutWidth,
                    columns,
                    gridRowHeight,
                    rows
                )

                layoutGrid.forEachCell { column, row ->
                    val position = positionParams.calcGridItemPosition(column, row, 1, 1)

                    div(+layoutStyles.emptyGridCell) {
                        inlineStyles {
                            top = position.top.px
                            left = position.left.px
                            width = position.width.px
                            height = position.height.px
                        }
                        attrs["data-cell-x"] = column
                        attrs["data-cell-y"] = row
//                        attrs.onClickFunction = handleEmptyGridCellClick
                        attrs.onMouseDown = handleEmptyGridCellMouseDown.withMouseEvent()
                        attrs.onClick = handleEmptyGridCellClick.withMouseEvent()

                        icon(Add)
                    }
                }
            }
        }

        gridLayout {
            attrs.id = buttonGroupControl.id
            attrs.className = +layoutStyles.gridContainer
            attrs.width = layoutWidth.toDouble()
            attrs.autoSize = false
            attrs.cols = columns
            attrs.rowHeight = gridRowHeight
            attrs.maxRows = rows
            attrs.margin = 5 to 5
            attrs.layout = layout
            attrs.onLayoutChange = handleLayoutChange
            attrs.resizeHandle = ::buildResizeHandle
            attrs.disableDrag = !editMode.isOn
            attrs.disableResize = !editMode.isOn
            attrs.isDroppable = editMode.isOn
            attrs.isBounded = false

            layout.items.forEachIndexed { index, layoutItem ->
                child(ReactHTML.div) {
                    key = layoutItem.i
                    attrs.className = -layoutStyles.gridCell
                    if (editMode.isOff) {
                        attrs.asDynamic()["data-grid-index"] = index

                        if (!props.buttonGroupControl.allowMultiple) {
                            attrs.onClickCapture = handleGridItemClick
                        }
                    }

                    val subEditor = object : Editor<MutableIGridLayout> {
                        override val title: String = "Grid buttongroup layout editor for ${layoutItem.i}"

                        override fun edit(mutableShow: MutableShow, block: MutableIGridLayout.() -> Unit) {
                            mutableShow.editLayouts {
                                editor.edit(mutableShow) {
                                    block(items[index].layout
                                        ?: error("Couldn't find item for ${layoutItem.i}."))
                                }
                            }
                        }

                        override fun delete(mutableShow: MutableShow) {
                            mutableShow.editLayouts {
                                editor.edit(mutableShow) {
                                    items.removeAt(index)
                                }
                            }
                        }
                    }

                    gridItem {
                        attrs.control = controls[layoutItem.i]!!
                        attrs.controlProps = props.controlProps.withLayout(
                            layouts[layoutItem.i], subEditor, layoutItem.gridDimens)
                        attrs.className = -layoutStyles.controlBox
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

            appContext.plugins.addControlMenuItems
                .filter { it.validForButtonGroup }
                .forEach { addControlMenuItem ->
                    addMenuContext.apply {
                        createMenuItem(gridLayoutEditor, addControlMenuItem, appContext, closeAddMenu)
                    }
                }
        }
    }
}

external interface GridButtonGroupProps : Props {
    var controlProps: ControlProps
    var buttonGroupControl: OpenButtonGroupControl
}

fun RBuilder.gridButtonGroupControl(handler: RHandler<GridButtonGroupProps>) =
    child(GridButtonGroupControlView, handler = handler)