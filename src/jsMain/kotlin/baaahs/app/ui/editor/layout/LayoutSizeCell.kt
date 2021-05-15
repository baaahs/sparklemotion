package baaahs.app.ui.editor.layout

import baaahs.app.ui.appContext
import baaahs.show.mutable.MutableLayoutDimen
import baaahs.ui.unaryPlus
import baaahs.ui.useCallback
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
import materialui.icons.Icons
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget
import react.*
import react.dom.div
import react.dom.span
import styled.inlineStyles

val LayoutSizeCell = xComponent<LayoutSizeCellProps>("LayoutSizeCell") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layoutEditor

    var gridSizeMenuAnchor by state<EventTarget?> { null }

    val showGridSizeMenu = useCallback { event: Event -> gridSizeMenuAnchor = event.target!! }
    val hideGridSizeMenu = useCallback { _: Event?, _: String? -> gridSizeMenuAnchor = null}

    val handleMenuUnitClick = useCallback(gridSizeMenuAnchor, props.dimen) { unit: String ->
        props.dimen .unit = unit
        props.onChange()
        gridSizeMenuAnchor = null
    }
    val handleMenuEmClick = useCallback(handleMenuUnitClick) { _: Event -> handleMenuUnitClick("em") }
    val handleMenuPxClick = useCallback(handleMenuUnitClick) { _: Event -> handleMenuUnitClick("px") }
    val handleMenuFrClick = useCallback(handleMenuUnitClick) { _: Event -> handleMenuUnitClick("fr") }
    val handleMenuDuplicateClick = useCallback(handleMenuUnitClick) { _: Event ->
        props.onDuplicate()
        gridSizeMenuAnchor = null
    }
    val handleMenuDeleteClick = useCallback(handleMenuUnitClick) { _: Event ->
        props.onDelete()
        gridSizeMenuAnchor = null
    }

    val handleGridSizeScalarChange = useCallback(props.dimen) { event: Event ->
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
                icon(Icons.ArrowDropDown)
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

external interface LayoutSizeCellProps : RProps {
    var dimen: MutableLayoutDimen
    var type: String
    var onChange: () -> Unit
    var onDuplicate: () -> Unit
    var onDelete: () -> Unit
}

fun RBuilder.layoutSizeCell(handler: RHandler<LayoutSizeCellProps>) =
    child(LayoutSizeCell, handler = handler)