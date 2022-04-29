package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.editor.AddButtonToButtonGroupEditIntent
import baaahs.app.ui.layout.gridItem
import baaahs.control.OpenButtonGroupControl
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenGridLayout
import baaahs.ui.gridlayout.CompactType
import baaahs.ui.gridlayout.Layout
import baaahs.ui.gridlayout.LayoutItem
import baaahs.ui.gridlayout.gridLayout
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import external.react_resizable.buildResizeHandle
import kotlinx.js.jso
import materialui.icon
import mui.material.Card
import mui.material.IconButton
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val GridButtonGroupView = xComponent<GridButtonGroupProps>("GridButtonGroup") { props ->
    val appContext = useContext(appContext)
    val layoutStyles = appContext.allStyles.layout
    val editMode = observe(appContext.showManager.editMode)

    val buttonGroupControl = props.buttonGroupControl
    val dropTarget = props.controlProps.controlDisplay?.dropTargetFor(buttonGroupControl)

    val onShowStateChange = props.controlProps.onShowStateChange

    val showPreview = appContext.uiSettings.renderButtonPreviews

    var layoutDimens by state { 100 to 100 }
    val gridLayout = props.controlProps.layout
        ?: OpenGridLayout(1, 1, emptyList())
    val columns = gridLayout.columns
    val rows = gridLayout.rows
    val (layoutWidth, layoutHeight) = layoutDimens
    val margin = 5
    val itemPadding = 5
    val gridRowHeight = (layoutHeight.toDouble() - margin) / rows - itemPadding

    val cardRef = ref<Element>()
    useResizeListener(cardRef) {
        with(cardRef.current!!) {
            console.log("resized ${props.buttonGroupControl.id}!", clientWidth, clientHeight, this)
            layoutDimens = clientWidth to clientHeight
        }
    }

    val handleLayoutChange by handler(/*props.tabEditor*/) { newLayout: Layout ->
        console.log("newLayout", newLayout)
    }

    val handleEditButtonClick = callback(buttonGroupControl) { event: Event, index: Int ->
        val button = buttonGroupControl.buttons[index]
        button.getEditIntent()?.let { appContext.openEditor(it) }
        event.preventDefault()
    }

    val layout = Layout(gridLayout.items.map { gridItem ->
        LayoutItem(gridItem.column, gridItem.row, gridItem.width, gridItem.height, gridItem.control.id)
    })
    val controls = gridLayout.items.associate { it.control.id to it.control }
    val layouts = gridLayout.items.associate { it.control.id to it.layout }


    Card {
        ref = cardRef
        attrs.classes = jso { root = -Styles.buttonGroupCard }

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

            layout.items.forEach { layoutItem ->
                div(+layoutStyles.gridCell) {
                    key = layoutItem.i

                    gridItem {
                        attrs.control = controls[layoutItem.i]!!
                        attrs.controlProps = props.controlProps.withLayout(layouts[layoutItem.i])
                        attrs.className = -layoutStyles.controlBox
                    }
                }
            }
//            attrs.onDragStart = handleDragStart.unsafeCast<ItemCallback>()
//            attrs.onDragStop = handleDragStop.unsafeCast<ItemCallback>()
//            attrs.onDropDragOver = handleDropDragOver

            if (editMode.isOn) {
                IconButton {
                    icon(mui.icons.material.AddCircleOutline)
                    attrs.onClick = {
                        appContext.openEditor(AddButtonToButtonGroupEditIntent(buttonGroupControl.id))
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