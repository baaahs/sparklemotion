package baaahs.app.ui.editor.layout

import baaahs.app.ui.appContext
import baaahs.show.mutable.MutableLayoutDimen
import baaahs.ui.unaryPlus
import baaahs.ui.value
import baaahs.ui.xComponent
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

val LayoutSizeCell = xComponent<LayoutSizeCellProps>("LayoutSizeCell") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layoutEditor

    var gridSizeMenuAnchor by state<EventTarget?> { null }

    val showGridSizeMenu = useCallback { event: Event -> gridSizeMenuAnchor = event.target!! }
    val hideGridSizeMenu = useCallback { _: Event?, _: String? -> gridSizeMenuAnchor = null}

    val handleGridSizeMenuUnitClick = useCallback(gridSizeMenuAnchor, props.dimen) { unit: String ->
        props.dimen .unit = unit
        props.onChange()
        forceRender()
        gridSizeMenuAnchor = null
    }
    val handleGridSizeMenuEmClick = useCallback(handleGridSizeMenuUnitClick) { _: Event ->
        handleGridSizeMenuUnitClick("em")
    }
    val handleGridSizeMenuPxClick = useCallback(handleGridSizeMenuUnitClick) { _: Event ->
        handleGridSizeMenuUnitClick("px")
    }
    val handleGridSizeMenuFrClick = useCallback(handleGridSizeMenuUnitClick) { _: Event ->
        handleGridSizeMenuUnitClick("fr")
    }

    val handleGridSizeScalarChange = useCallback(props.dimen) { event: Event ->
        props.dimen.scalar = event.target!!.value.toFloat()
        props.onChange()
        forceRender()
    }

    div(+styles.gridSizeEditor) {
        textField {
            attrs.type = InputType.number
            attrs.value = props.dimen.scalar
            attrs.onChangeFunction = handleGridSizeScalarChange
        }

        span {
            div(+styles.gridSizeMenuAffordance) {
                attrs.onClickFunction = showGridSizeMenu

                +props.dimen.unit
                icon(Icons.ArrowDropDown)
            }
        }

        gridSizeMenuAnchor?.let { gridSizeMenu ->
            menu {
                attrs.anchorEl(gridSizeMenuAnchor)
                attrs.anchorOrigin {
                    horizontal(PopoverOriginHorizontal.left)
                    vertical(PopoverOriginVertical.top)
                }
                attrs.open = true
                attrs.onClose = hideGridSizeMenu

                menuItem {
                    attrs.onClickFunction = handleGridSizeMenuEmClick
                    listItemText { +"em ('m' widths)" }
                }

                menuItem {
                    attrs.onClickFunction = handleGridSizeMenuPxClick
                    listItemText { +"px (pixels)" }
                }

                menuItem {
                    attrs.onClickFunction = handleGridSizeMenuFrClick
                    listItemText { +"fr (fractional part of remaining space)" }
                }

                divider {}

                menuItem {
                    attrs.disabled = true

                    listItemText { +"Duplicate…" }
                }

                menuItem {
                    attrs.disabled = true

                    listItemText { +"Delete…" }
                }
            }
        }
    }
}

external interface LayoutSizeCellProps : RProps {
    var dimen: MutableLayoutDimen
    var onChange: () -> Unit
}

fun RBuilder.layoutSizeCell(handler: RHandler<LayoutSizeCellProps>) =
    child(LayoutSizeCell, handler = handler)