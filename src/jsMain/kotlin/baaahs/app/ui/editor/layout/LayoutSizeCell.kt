package baaahs.app.ui.editor.layout

import baaahs.app.ui.appContext
import baaahs.show.mutable.MutableLayoutDimen
import baaahs.ui.unaryPlus
import baaahs.ui.value
import baaahs.ui.xComponent
import kotlinx.css.paddingTop
import kotlinx.css.px
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import materialui.components.divider.divider
import materialui.components.listitemtext.listItemText
import materialui.components.menu.menu
import materialui.components.menuitem.menuItem
import materialui.components.popover.enums.PopoverOriginHorizontal
import materialui.components.popover.enums.PopoverOriginVertical
import materialui.components.popover.horizontal
import materialui.components.popover.vertical
import materialui.components.textfield.textField
import materialui.icon
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.span
import react.useContext
import styled.inlineStyles

val LayoutSizeCell = xComponent<LayoutSizeCellProps>("LayoutSizeCell") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layoutEditor

    var gridSizeMenuAnchor by state<EventTarget?> { null }

    val showGridSizeMenu = callback { event: Event -> gridSizeMenuAnchor = event.target!! }
    val hideGridSizeMenu = callback { _: Event?, _: String? -> gridSizeMenuAnchor = null}

    val handleMenuUnitClick = callback(gridSizeMenuAnchor, props.dimen) { unit: String ->
        props.dimen .unit = unit
        props.onChange()
        gridSizeMenuAnchor = null
    }
    val handleMenuEmClick = callback(handleMenuUnitClick) { _: Event -> handleMenuUnitClick("em") }
    val handleMenuPxClick = callback(handleMenuUnitClick) { _: Event -> handleMenuUnitClick("px") }
    val handleMenuFrClick = callback(handleMenuUnitClick) { _: Event -> handleMenuUnitClick("fr") }
    val handleMenuDuplicateClick = callback(handleMenuUnitClick) { _: Event ->
        props.onDuplicate()
        gridSizeMenuAnchor = null
    }
    val handleMenuDeleteClick = callback(handleMenuUnitClick) { _: Event ->
        props.onDelete()
        gridSizeMenuAnchor = null
    }

    val handleGridSizeScalarChange = callback(props.dimen) { event: Event ->
        props.dimen.scalar = event.target!!.value.toFloat()
        props.onChange()
        forceRender()
    }

    div(+styles.gridSizeEditor) {
        textField(/*styles.gridSizeEditorTextField*/) {
            inlineStyles { paddingTop = 5.px }
            attrs.type = InputType.number
            attrs.value = props.dimen.scalar
            attrs.onChangeFunction = handleGridSizeScalarChange
        }

        span {
            div(+styles.gridSizeEditorMenuAffordance) {
                attrs.onClickFunction = showGridSizeMenu

                +props.dimen.unit
                icon(materialui.icons.ArrowDropDown)
            }
        }

        gridSizeMenuAnchor?.let {
            menu {
                attrs.anchorEl(gridSizeMenuAnchor)
                attrs.anchorOrigin {
                    horizontal(PopoverOriginHorizontal.left)
                    vertical(PopoverOriginVertical.top)
                }
                attrs.open = true
                attrs.onClose = hideGridSizeMenu

                menuItem {
                    attrs.onClickFunction = handleMenuEmClick
                    listItemText { +"em ('m' widths)" }
                }

                menuItem {
                    attrs.onClickFunction = handleMenuPxClick
                    listItemText { +"px (pixels)" }
                }

                menuItem {
                    attrs.onClickFunction = handleMenuFrClick
                    listItemText { +"fr (fractional part of remaining space)" }
                }

                divider {}

                menuItem {
                    attrs.onClickFunction = handleMenuDuplicateClick
                    listItemText { +"Duplicate ${props.type}" }
                }

                menuItem {
                    attrs.onClickFunction = handleMenuDeleteClick
                    listItemText { +"Delete ${props.type}" }
                }
            }
        }
    }
}

external interface LayoutSizeCellProps : Props {
    var dimen: MutableLayoutDimen
    var type: String
    var onChange: () -> Unit
    var onDuplicate: () -> Unit
    var onDelete: () -> Unit
}

fun RBuilder.layoutSizeCell(handler: RHandler<LayoutSizeCellProps>) =
    child(LayoutSizeCell, handler = handler)