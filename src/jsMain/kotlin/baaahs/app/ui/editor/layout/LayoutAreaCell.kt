package baaahs.app.ui.editor.layout

import baaahs.app.ui.appContext
import baaahs.show.mutable.MutableLayouts
import baaahs.show.mutable.MutableTab
import baaahs.ui.unaryPlus
import baaahs.ui.value
import baaahs.ui.xComponent
import kotlinx.html.js.onChangeFunction
import materialui.components.listitemtext.listItemText
import materialui.components.menuitem.menuItem
import materialui.components.select.select
import org.w3c.dom.events.Event
import react.*
import react.dom.div

val LayoutAreaCell = xComponent<LayoutAreaCellProps>("LayoutAreaCell") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layoutEditor

    val handlePanelAreaChange = handler("handlePanelAreaChange") { event: Event ->
        props.tab.areas[props.rowIndex * props.tab.columns.size + props.columnIndex] = event.target.value
        props.onChange()
        forceRender()
    }

    div(+styles.gridAreaEditor) {
        select {
            attrs.value(props.tab.areas[props.rowIndex * props.tab.columns.size + props.columnIndex])
            attrs.onChangeFunction = handlePanelAreaChange

            menuItem {
                attrs["value"] = "-"
            }

            props.layouts.panelNames.forEach { panelName ->
                menuItem {
                    attrs["value"] = panelName
                    listItemText { +panelName }
                }
            }
        }
    }
}

external interface LayoutAreaCellProps : RProps {
    var layouts: MutableLayouts
    var tab: MutableTab
    var columnIndex: Int
    var rowIndex: Int
    var onChange: () -> Unit
}

fun RBuilder.layoutAreaCell(handler: RHandler<LayoutAreaCellProps>) =
    child(LayoutAreaCell, handler = handler)