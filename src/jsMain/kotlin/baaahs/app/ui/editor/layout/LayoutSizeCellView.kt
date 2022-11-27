package baaahs.app.ui.editor.layout

import baaahs.app.ui.appContext
import baaahs.show.mutable.MutableLayoutDimen
import baaahs.ui.unaryPlus
import baaahs.ui.value
import baaahs.ui.xComponent
import csstype.px
import dom.Element
import kotlinx.js.jso
import materialui.icon
import mui.material.*
import org.w3c.dom.events.Event
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.FormEvent
import react.dom.html.InputType
import react.dom.onChange
import react.dom.onClick
import react.dom.span
import react.useContext

private val LayoutSizeCellView = xComponent<LayoutSizeCellProps>("LayoutSizeCell") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layoutEditor

    var gridSizeMenuAnchor by state<Element?> { null }

    val showGridSizeMenu by mouseEventHandler { event -> gridSizeMenuAnchor = event.currentTarget as Element? }
    val hideGridSizeMenu = callback { _: Event?, _: String? -> gridSizeMenuAnchor = null}

    val handleMenuUnitClick = callback(gridSizeMenuAnchor, props.dimen) { unit: String ->
        props.dimen .unit = unit
        props.onChange(true)
        gridSizeMenuAnchor = null
    }
    val handleMenuEmClick by mouseEventHandler(handleMenuUnitClick) { handleMenuUnitClick("em") }
    val handleMenuPxClick by mouseEventHandler(handleMenuUnitClick) { handleMenuUnitClick("px") }
    val handleMenuFrClick by mouseEventHandler(handleMenuUnitClick) { handleMenuUnitClick("fr") }
    val handleMenuDuplicateClick by mouseEventHandler(handleMenuUnitClick) {
        props.onDuplicate()
        gridSizeMenuAnchor = null
    }
    val handleMenuDeleteClick by mouseEventHandler(handleMenuUnitClick) {
        props.onDelete()
        gridSizeMenuAnchor = null
    }

    val handleGridSizeScalarChange by newEventHandler(props.dimen) { event: FormEvent<*> ->
        props.dimen.scalar = event.target.value.toFloat()
        props.onChange(true)
        forceRender()
    }

    div(+styles.gridSizeEditor) {
        TextField(/*styles.gridSizeEditorTextField*/) {
            attrs.sx = jso {
                paddingTop = 5.px
            }
            attrs.size = Size.small
            attrs.margin = FormControlMargin.dense
            attrs.type = InputType.number
            attrs.value = props.dimen.scalar
            attrs.onChange = handleGridSizeScalarChange
        }

        span {
            div(+styles.gridSizeEditorMenuAffordance) {
                attrs.onClick = showGridSizeMenu

                +props.dimen.unit
                icon(mui.icons.material.ArrowDropDown)
            }
        }

        gridSizeMenuAnchor?.let { anchor ->
            Menu {
                attrs.anchorEl = { anchor }
                attrs.anchorOrigin = jso {
                    horizontal = "left"
                    vertical = "top"
                }
                attrs.open = true
                attrs.onClose = hideGridSizeMenu

                MenuItem {
                    attrs.onClick = handleMenuEmClick
                    ListItemText { +"em ('m' widths)" }
                }

                MenuItem {
                    attrs.onClick = handleMenuPxClick
                    ListItemText { +"px (pixels)" }
                }

                MenuItem {
                    attrs.onClick = handleMenuFrClick
                    ListItemText { +"fr (fractional part of remaining space)" }
                }

                Divider {}

                MenuItem {
                    attrs.onClick = handleMenuDuplicateClick
                    ListItemText { +"Duplicate ${props.type}" }
                }

                MenuItem {
                    attrs.onClick = handleMenuDeleteClick
                    ListItemText { +"Delete ${props.type}" }
                }
            }
        }
    }
}

external interface LayoutSizeCellProps : Props {
    var dimen: MutableLayoutDimen
    var type: String
    var onChange: (pushToUndoStack: Boolean) -> Unit
    var onDuplicate: () -> Unit
    var onDelete: () -> Unit
}

fun RBuilder.layoutSizeCell(handler: RHandler<LayoutSizeCellProps>) =
    child(LayoutSizeCellView, handler = handler)