package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.app.ui.layout.LayoutGrid
import baaahs.app.ui.layout.buildResizeHandle
import baaahs.app.ui.layout.gridItem
import baaahs.control.ButtonControl
import baaahs.control.OpenButtonControl
import baaahs.control.OpenButtonGroupControl
import baaahs.show.live.ControlProps
import baaahs.show.live.EmptyOpenContext
import baaahs.ui.gridlayout.CompactType
import baaahs.ui.gridlayout.GridLayout
import baaahs.ui.gridlayout.Layout
import baaahs.ui.gridlayout.LayoutItem
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import kotlinx.js.jso
import mui.material.Card
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
    val columns = 2
    val rows = 3
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

    val layoutGrid = memo(columns, rows) {
        LayoutGrid(columns, rows, listOf(), null)
    }

    val layout = listOf(
        LayoutItem(1, 1, 1, 1, "first", Unit),
        LayoutItem(1, 3, 1, 1, "second", Unit)
    )


    Card {
        ref = cardRef
        attrs.classes = jso { root = -Styles.buttonGroupCard }

        child(GridLayout::class) {
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
            attrs.resizeHandle = { axis, ref -> buildResizeHandle(axis) }
            attrs.disableDrag = !editMode.isOn
            attrs.disableResize = !editMode.isOn
            attrs.isDroppable = editMode.isOn
            attrs.isBounded = false

            layout.forEach { layoutItem ->
                div(+layoutStyles.gridCell) {
                    key = layoutItem.i

                    gridItem {
                        attrs.control = OpenButtonControl(
                            layoutItem.i,
                            ButtonControl(layoutItem.i),
                            EmptyOpenContext
                        )
                        attrs.controlProps = ControlProps({}, null)
                        attrs.className = -layoutStyles.controlBox
                    }
                }
            }
//            attrs.onDragStart = handleDragStart.unsafeCast<ItemCallback>()
//            attrs.onDragStop = handleDragStop.unsafeCast<ItemCallback>()
//            attrs.onDropDragOver = handleDropDragOver

//            props.tab.items.forEach { item ->
//                div(+styles.gridCell) {
//                    key = item.controlId
//
//                    gridItem {
//                        attrs.control = item.control
//                        attrs.controlProps = genericControlProps
//                        attrs.className = -styles.controlBox
//                    }
//                }
//            }

        }

//        droppable({
//            if (dropTarget != null) {
//                droppableId = dropTarget.dropTargetId
//                type = dropTarget.type
//            } else {
//                isDropDisabled = true
//            }
//            direction = buttonGroupControl.direction
//                .decode(Direction.horizontal, Direction.vertical).name
//            isDropDisabled = !editMode.isOn
//        }) { sceneDropProvided, _ ->
//            buildElement {
//                ToggleButtonGroup {
//                    attrs.classes = jso {
//                        root = -buttonGroupControl.direction
//                            .decode(Styles.horizontalButtonList, Styles.verticalButtonList)
//                    }
//                    attrs.color = ToggleButtonGroupColor.primary
//
//                    install(sceneDropProvided)
//
//                    attrs.orientation = buttonGroupControl.direction
//                        .decode(Orientation.horizontal, Orientation.vertical)
//                    attrs.exclusive = true
////                    attrs.value = props.selected // ... but this is busted.
////                    attrs.onChangeFunction = eventHandler { value: Int -> props.onSelect(value) }
//
//                    buttonGroupControl.buttons.forEachIndexed { index, buttonControl ->
//                        val shaderForPreview = if (showPreview) buttonControl.shaderForPreview() else null
//
//                        draggable({
//                            this.key = buttonControl.id
//                            this.draggableId = buttonControl.id
//                            this.isDragDisabled = !editMode.isOn
//                            this.index = index
//                        }) { sceneDragProvided, _ ->
////                            div {
////                                +"Handle"
//                            buildElement {
//                                div(+Styles.controlButton) {
//                                    ref = sceneDragProvided.innerRef
//                                    copyFrom(sceneDragProvided.draggableProps)
//
//                                    problemBadge(buttonControl as OpenControl)
//
//                                    div(+Styles.editButton) {
//                                        if (editMode.isOn) {
//                                            attrs.onClickFunction = { event -> handleEditButtonClick(event, index) }
//                                        }
//
//                                        icon(mui.icons.material.Edit)
//                                    }
//                                    div(+Styles.dragHandle) {
//                                        copyFrom(sceneDragProvided.dragHandleProps)
//                                        icon(mui.icons.material.DragIndicator)
//                                    }
//
//                                    if (shaderForPreview != null) {
//                                        div(+Styles.buttonShaderPreviewContainer) {
//                                            shaderPreview {
//                                                attrs.shader = shaderForPreview.shader
//                                            }
//                                        }
//                                    }
//
//                                    ToggleButton {
//                                        if (showPreview) {
//                                            attrs.classes = jso {
//                                                root = -Styles.buttonLabelWhenPreview
//                                                selected = -Styles.buttonSelectedWhenPreview
//                                            }
//                                        }
//
//                                        attrs.value = index.toString()
//                                        attrs.selected = buttonControl.isPressed
//                                        attrs.onClick = { _: MouseEvent<HTMLElement, *>, _: dynamic ->
//                                            buttonGroupControl.clickOn(index)
//                                            onShowStateChange()
//                                        }
//
//                                        +buttonControl.title
//                                    }
////                            }
//                                }
//                            }
//                        }
//
////                            }
//                    }
//
//                    child(sceneDropProvided.placeholder)
//
//                    if (editMode.isOn) {
//                        IconButton {
//                            icon(mui.icons.material.AddCircleOutline)
//                            attrs.onClick = {
//                                appContext.openEditor(AddButtonToButtonGroupEditIntent(buttonGroupControl.id))
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}

external interface GridButtonGroupProps : Props {
    var controlProps: ControlProps
    var buttonGroupControl: OpenButtonGroupControl
}

fun RBuilder.gridButtonGroup(handler: RHandler<GridButtonGroupProps>) =
    child(GridButtonGroupView, handler = handler)