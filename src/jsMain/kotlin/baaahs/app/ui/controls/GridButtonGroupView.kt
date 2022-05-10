package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.Editor
import baaahs.app.ui.layout.gridItem
import baaahs.control.OpenButtonControl
import baaahs.control.OpenButtonGroupControl
import baaahs.forEach
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenGridLayout
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutableShow
import baaahs.ui.gridlayout.CompactType
import baaahs.ui.gridlayout.Layout
import baaahs.ui.gridlayout.LayoutItem
import baaahs.ui.gridlayout.gridLayout
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import external.react_resizable.buildResizeHandle
import kotlinx.dom.hasClass
import kotlinx.js.jso
import mui.material.Card
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.header
import react.dom.html.ReactHTML
import react.useContext

private val GridButtonGroupView = xComponent<GridButtonGroupProps>("GridButtonGroup") { props ->
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
    val columns = gridLayout.columns
    val rows = gridLayout.rows
    val (layoutWidth, layoutHeight) = layoutDimens
    val margin = 5
    val itemPadding = 5
    val gridRowHeight = (layoutHeight.toDouble() - margin) / rows - itemPadding

    val cardRef = ref<Element>()
    useResizeListener(cardRef) {
        cardRef.current?.children?.forEach {
            if (it.hasClass("react-grid-layout")) {
                with(it) {
                    console.log("resized ${props.buttonGroupControl.id}!", clientWidth, clientHeight, this)
                    layoutDimens = clientWidth to clientHeight
                }
            }
        }
    }

    val handleLayoutChange by handler(gridLayout, editor) { newLayout: Layout ->
        appContext.showManager.openShow?.edit {
            val mutableShow = this
            editor.edit(mutableShow) {
                applyChanges(gridLayout.items, newLayout, mutableShow)
            }
            appContext.showManager.onEdit(mutableShow)
        }
        Unit
    }

    val layout = Layout(gridLayout.items.map { gridItem ->
        LayoutItem(gridItem.column, gridItem.row, gridItem.width, gridItem.height, gridItem.control.id)
    })
    val controls = gridLayout.items.associate { it.control.id to it.control }
    val layouts = gridLayout.items.associate { it.control.id to it.layout }

    val handleGridItemClick by mouseEventHandler(gridLayout.items) { e ->
        (e.currentTarget as HTMLElement).dataset["gridIndex"]?.toInt()?.let { clickedIndex ->
            gridLayout.items.forEachIndexed { index, it ->
                (it.control as? OpenButtonControl)?.isPressed = index == clickedIndex
            }
            onShowStateChange()
            e.stopPropagation()
        }
    }

    Card {
        ref = cardRef
        attrs.classes = jso { root = -controlStyles.buttonGroupCard }

        if (buttonGroupControl.title.isNotBlank() && buttonGroupControl.showTitle) {
            header(+layoutStyles.buttonGroupHeader) {
                +buttonGroupControl.title
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
            attrs.compactType = CompactType.none
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
                        attrs.onClickCapture = handleGridItemClick
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
                        attrs.controlProps = props.controlProps.withLayout(layouts[layoutItem.i], subEditor)
                        attrs.className = -layoutStyles.controlBox
                    }
                }
            }
        }
    }
}

external interface GridButtonGroupProps : Props {
    var controlProps: ControlProps
    var buttonGroupControl: OpenButtonGroupControl
}

fun RBuilder.gridButtonGroup(handler: RHandler<GridButtonGroupProps>) =
    child(GridButtonGroupView, handler = handler)