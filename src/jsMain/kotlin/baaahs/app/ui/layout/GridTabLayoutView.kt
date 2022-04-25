package baaahs.app.ui.layout

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.AddControlToGrid
import baaahs.app.ui.editor.Editor
import baaahs.app.ui.layout.LayoutGrid.Companion.isEmpty
import baaahs.control.OpenButtonGroupControl
import baaahs.show.GridItem
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenGridItem
import baaahs.show.live.OpenGridTab
import baaahs.show.mutable.MutableGridItem
import baaahs.show.mutable.MutableGridTab
import baaahs.ui.and
import baaahs.ui.gridlayout.*
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import baaahs.window
import csstype.ClassName
import external.react_resizable.ResizeHandleAxes
import external.react_resizable.ResizeHandleAxis
import kotlinx.css.*
import kotlinx.css.properties.border
import kotlinx.html.Draggable
import kotlinx.html.draggable
import kotlinx.html.js.onClickFunction
import kotlinx.js.jso
import materialui.icon
import mui.icons.material.Add
import mui.material.*
import org.w3c.dom.DragEvent
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import react.*
import react.dom.div
import react.dom.events.DragEventHandler
import react.dom.onDragStart
import react.dom.svg.ReactSVG.path
import styled.StyleSheet
import styled.inlineStyles

class LayoutGrid(
    private val columns: Int,
    private val rows: Int,
    private val items: List<OpenGridItem>,
    private val draggingItem: String?
) {
    val layouts: Array<LayoutItem> = buildList<LayoutItem> {
        items.forEach { item ->
            add(jso {
                i = item.controlId
                x = item.column
                y = item.row
                w = item.width
                h = item.height
                static = draggingItem != null && draggingItem != i && item.control is OpenButtonGroupControl
            })
        }
    }.toTypedArray()

    fun forEachCell(block: (column: Int, row: Int) -> Unit) {
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                block(column, row)
            }
        }
    }

    companion object {
        fun LayoutItem.isEmpty() = i.startsWith("::empty-")

        val newControlId = "__new_control__"
    }
}

private val GridTabLayoutView = xComponent<GridTabLayoutProps>("GridTabLayout") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layout

    var layoutDimens by state { window.innerWidth to window.innerHeight }
    val columns = props.tab.columns
    val rows = props.tab.rows

    var showAddMenu by state<Pair<GridItem, HTMLElement?>?> { null }

    val editMode = observe(appContext.showManager.editMode)
    var draggingItem by state<String?> { null }

    val handleLayoutChange by handler(props.tabEditor) { newLayout: Layout ->
        val gridItems = newLayout.map { newLayoutItem ->
            GridItem(
                newLayoutItem.i,
                newLayoutItem.x, newLayoutItem.y,
                newLayoutItem.w, newLayoutItem.h,
                newLayoutItem.isEmpty()
            )
        }

        var anyAddedControls = false
        for (gridItem in gridItems) {
            if (gridItem.controlId == LayoutGrid.newControlId) {
                showAddMenu = gridItem to null
                anyAddedControls = true
            }
        }

        if (!anyAddedControls) {
            appContext.showManager.openShow?.edit {
                val mutableShow = this
                props.tabEditor.edit(mutableShow) {
                    this.items.clear()
                    this.items.addAll(
                        gridItems
                            .filterNot { it.isEmpty }
                            .map { MutableGridItem(it, mutableShow) }
                    )
                }
                appContext.showManager.onEdit(mutableShow)
            }
        }
    }

    val handleAddControlDragStart by handler { e: DragEvent ->
        e.dataTransfer?.setData("text/plain", "")
    }

    val handleDragStart: ItemCallback by handler {
            layout, oldItem, newItem, placeholder, e, element ->
        console.log("onDragStart", layout, oldItem, newItem, placeholder, e, element)
        draggingItem = newItem.i
    }
    val handleDragStop by handler { draggingItem = null }

    val handleEmptyGridCellClick by eventHandler { e ->
        val target = e.currentTarget as HTMLElement
        val dataset = target.dataset.asDynamic()
        val x = (dataset.cellX as String).toInt()
        val y = (dataset.cellY as String).toInt()
        showAddMenu = GridItem(LayoutGrid.newControlId, x, y) to target
    }

    val handleDropDragOver by handler { e: DragOverEvent ->
        jso<DroppingItem> {
            i = LayoutGrid.newControlId
            w = 1
            h = 1
        }
    }

    val containerDiv = ref<HTMLDivElement>()
    useResizeListener(containerDiv) {
        layoutDimens = with(containerDiv.current!!) { clientWidth to clientHeight }
    }
    val (layoutWidth, layoutHeight) = layoutDimens
    val margin = 5
    val itemPadding = 5
    val gridRowHeight = (layoutHeight.toDouble() - margin) / rows - itemPadding

    val layoutGrid = memo(columns, rows, props.tab, draggingItem) {
        LayoutGrid(columns, rows, props.tab.items, draggingItem)
    }

    val genericControlProps = memo(props.onShowStateChange) {
        ControlProps(props.onShowStateChange, null)
    }


    div(+styles.gridOuterContainer and
            +if (editMode.isOn) styles.editModeOn else styles.editModeOff
    ) {
        ref = containerDiv

        if (editMode.isAvailable) {
            div(+styles.gridBackground) {
                val positionParams = jso<PositionParams> {
                    this.margin = arrayOf(margin, margin)
                    containerPadding = arrayOf(itemPadding, itemPadding)
                    containerWidth = layoutWidth
                    cols = columns
                    rowHeight = gridRowHeight
                    maxRows = rows
                }

                layoutGrid.forEachCell { column, row ->
                    val position = calcGridItemPosition(positionParams, column, row, 1, 1)

                    div(+styles.emptyGridCell) {
                        inlineStyles {
                            top = position.top.px
                            left = position.left.px
                            width = position.width.px
                            height = position.height.px
                        }
                        attrs["data-cell-x"] = column
                        attrs["data-cell-y"] = row
                        attrs.onClickFunction = handleEmptyGridCellClick

                        icon(Add)
                    }
                }
            }
        }

//        ReactGridLayout {
        println("editMode = ${editMode.isOn}")

        child(GridLayout::class) {
            attrs.id = "top"
            attrs.className = +styles.gridContainer
            attrs.width = layoutDimens.first.toDouble()
            attrs.autoSize = false
            attrs.cols = columns
            attrs.rowHeight = gridRowHeight
            attrs.maxRows = rows
            attrs.margin = arrayOf(5, 5)
            attrs.layout = layoutGrid.layouts.toList()
            attrs.onLayoutChange = handleLayoutChange
            attrs.compactType = CompactType.none
            attrs.resizeHandles = ResizeHandleAxes.toList()
            attrs.resizeHandle = { axis, ref -> buildResizeHandle(axis) }
            attrs.isDraggable = editMode.isOn
            attrs.isResizable = editMode.isOn
            attrs.isDroppable = editMode.isOn
            attrs.onDragStart = handleDragStart.unsafeCast<ItemCallback>()
            attrs.onDragStop = handleDragStop.unsafeCast<ItemCallback>()
            attrs.onDropDragOver = handleDropDragOver

            props.tab.items.forEach { item ->
                div(+styles.gridCell) {
                    key = item.controlId

                    gridItem {
                        attrs.control = item.control
                        attrs.controlProps = genericControlProps
                        attrs.className = -styles.controlBox
                    }
                }
            }
        }

        if (editMode.isOn) {
            div(+styles.addControl) {
                attrs.draggable = Draggable.htmlTrue

                // this is a hack for firefox
                // Firefox requires some kind of initialization
                // which we can do by adding this attribute
                // @see https://bugzilla.mozilla.org/show_bug.cgi?id=568313
                attrs.onDragStart = handleAddControlDragStart as DragEventHandler<*>

                inlineStyles {
                    width = (layoutDimens.first / columns).px
                    height = (layoutDimens.second / rows).px
                }

                +"New Control…"
            }
        }
    }

    showAddMenu?.let { (gridItem, anchorEl) ->
        Menu {
            attrs.anchorEl = { anchorEl ?: containerDiv.current!! }
            attrs.open = true
            attrs.onClose = { showAddMenu = null }

            println("gridItem.column = ${gridItem.column}")
            println("gridItem.row = ${gridItem.row}")
            appContext.plugins.addControlMenuItems.forEach { addControlMenuItem ->
                MenuItem {
                    attrs.onClick = {
                        val editIntent = AddControlToGrid(
                            props.tabEditor,
                            gridItem.column, gridItem.row,
                            gridItem.width, gridItem.height,
                            addControlMenuItem.createControlFn
                        )
                        appContext.openEditor(editIntent)
                        showAddMenu = null
                    }

                    ListItemIcon { icon(addControlMenuItem.icon) }
                    ListItemText { +addControlMenuItem.label }
                }
            }
        }
    }
}

fun buildResizeHandle(axis: ResizeHandleAxis) = buildElement {
    SvgIcon {
        attrs.viewBox = "0 0 20 20"
        attrs.classes = jso {
            this.root = ClassName("app-ui-layout-resize-handle app-ui-layout-resize-handle-$axis")
        }

        when (axis.length) {
            1 -> { // edge (south)
                path {
                    attrs.stroke = "#111"
                    attrs.fill = "#aaa"
                    attrs.d = "M0,19 L19,19 L19,17 L0,17 Z"
                }
                path {
                    attrs.stroke = "#111"
                    attrs.fill = "#666"
                    attrs.d = "M10,19 L4,9 L16,9 Z"
                }
            }
            2 -> { // corner (south-east)
                path {
                    attrs.stroke = "#111"
                    attrs.fill = "#aaa"
                    attrs.d = "M5,19 L19,19 L19,5 L17,5 L17,17 L5,17 Z"
                }
                path {
                    attrs.stroke = "#111"
                    attrs.fill = "#666"
                    attrs.d = "M15,15 L5,15 L15,5 Z"
                }
            }
        }
    }
}

object Styles : StyleSheet("ui-layout-grid", isStatic = true) {
    val gridItem by css {
        border(1.px, BorderStyle.solid, Color.orange)
    }
}

external interface GridTabLayoutProps : Props {
    var tab: OpenGridTab
    var controlProps: ControlProps
    var tabEditor: Editor<MutableGridTab>
    var onShowStateChange: () -> Unit
}

fun RBuilder.gridTabLayout(handler: RHandler<GridTabLayoutProps>) =
    child(GridTabLayoutView, handler = handler)