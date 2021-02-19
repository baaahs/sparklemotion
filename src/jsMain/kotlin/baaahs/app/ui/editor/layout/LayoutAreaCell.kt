package baaahs.app.ui.editor.layout

import baaahs.app.ui.appContext
import baaahs.getBang
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
        val panel = props.layouts.panels.getBang(event.target.value, "panel")
        props.tab.areas[props.rowIndex * props.tab.columns.size + props.columnIndex] = panel
        props.onChange()
        forceRender()
    }

    div(+styles.gridAreaEditor) {
        select {
            val currentPanel = props.tab.areas[props.rowIndex * props.tab.columns.size + props.columnIndex]
            val panelId = props.layouts.panels.entries.find { (_, panel) -> panel == currentPanel }!!.key
            attrs.value(panelId)
            attrs.onChangeFunction = handlePanelAreaChange

            props.layouts.panels.entries.sortedBy { (_, v) -> v.title }.forEach { (panelId, panel) ->
                menuItem {
                    attrs["value"] = panelId
                    listItemText { +panel.title }
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