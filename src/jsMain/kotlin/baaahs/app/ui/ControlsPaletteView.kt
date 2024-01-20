package baaahs.app.ui

import baaahs.app.ui.layout.gridItem
import baaahs.show.live.ControlDisplay
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenShow
import baaahs.ui.and
import baaahs.ui.gridlayout.Layout
import baaahs.ui.gridlayout.LayoutItem
import baaahs.ui.gridlayout.gridLayout
import baaahs.ui.unaryMinus
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import external.react_draggable.Draggable
import external.react_resizable.Resizable
import external.react_resizable.ResizeCallbackData
import external.react_resizable.buildResizeHandle
import js.objects.jso
import materialui.icon
import mui.material.Paper
import org.w3c.dom.events.MouseEvent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.header
import react.useContext
import web.html.HTMLElement

private val ControlsPaletteView = xComponent<ControlsPaletteProps>("ControlsPalette") { props ->
    val appContext = useContext(appContext)
    val editMode = observe(appContext.showManager.editMode)
    val unplacedControlPaletteDiv = ref<HTMLElement>()

    val editModeStyle =
        if (editMode.isOn) Styles.editModeOn else Styles.editModeOff

    var layoutDimens by state { 100 to 100 }
    val containerDiv = ref<HTMLElement>()
    useResizeListener(containerDiv) { width, height ->
        layoutDimens = width to height
    }

    val handleResize by handler { e: MouseEvent, data: ResizeCallbackData ->
        unplacedControlPaletteDiv.current?.style?.width = "${data.size.width}px"
        unplacedControlPaletteDiv.current?.style?.height = "${data.size.height}px"
    }

    Draggable {
        val styleForDragHandle = "ControlsPaletteDragHandle"
        attrs.handle = ".$styleForDragHandle"

        Resizable {
            attrs.handle = ::buildResizeHandle
            attrs.width = unplacedControlPaletteDiv.current?.clientWidth ?: 0
            attrs.height = unplacedControlPaletteDiv.current?.clientHeight ?: 0
            attrs.onResize = handleResize

            div(+editModeStyle and Styles.unplacedControlsPalette) {
                ref = unplacedControlPaletteDiv

                div(+Styles.dragHandle and styleForDragHandle) {
                    icon(mui.icons.material.DragIndicator)
                }

                Paper {
                    attrs.classes = jso { root = -Styles.unplacedControlsPaper }
                    attrs.elevation = 3

                    header { +"Unplaced Controls" }

                    div(+Styles.unplacedControlsDroppable) {
                        ref = containerDiv

                        if (editMode.isOn) {
                            val styles = appContext.allStyles.layout
                            val paletteWidth = layoutDimens.first
                            val columns = if (paletteWidth > 200) paletteWidth / 100 else paletteWidth / 75

                            val items = props.controlDisplay.relevantUnplacedControls
                            val rows = items.size / columns + 1
                            val gridRowHeight = paletteWidth / columns

                            val layout = Layout(items.mapIndexed { index, openControl ->
                                LayoutItem(index % columns, index / columns, 1, 1, openControl.id)
                            }, columns, rows)

                            gridLayout {
                                attrs.id = "_control_palette_"
                                attrs.className = +styles.gridContainer
                                attrs.width = paletteWidth.toDouble()
                                attrs.autoSize = false
                                attrs.cols = columns
                                attrs.rowHeight = gridRowHeight.toDouble()
                                attrs.maxRows = rows
                                attrs.margin = 5 to 5
                                attrs.layout = layout
//                                attrs.onLayoutChange = handleLayoutChange
                                attrs.disableDrag = !editMode.isOn
                                attrs.disableResize = true
                                attrs.isDroppable = editMode.isOn
//                                attrs.onDragStart = handleDragStart
//                                attrs.onDragStop = handleDragStop

                                items.forEachIndexed { index, item ->
                                    div(+styles.gridCell) {
                                        key = item.id

                                        gridItem {
                                            attrs.control = item
                                            attrs.controlProps = props.controlProps.withLayout(null, null, null)
                                            attrs.className = -styles.controlBox
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


external interface ControlsPaletteProps : Props {
    var controlDisplay: ControlDisplay
    var controlProps: ControlProps
    var show: OpenShow
}

fun RBuilder.controlsPalette(handler: RHandler<ControlsPaletteProps>) =
    child(ControlsPaletteView, handler = handler)