package baaahs.app.ui.editor.layout

import baaahs.app.ui.appContext
import baaahs.getBang
import baaahs.show.mutable.MutableLayouts
import baaahs.show.mutable.MutableTab
import baaahs.ui.unaryPlus
import baaahs.ui.value
import baaahs.ui.withSelectEvent
import baaahs.ui.xComponent
import mui.material.*
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.FormEvent
import react.useContext

val LayoutAreaCell = xComponent<LayoutAreaCellProps>("LayoutAreaCell") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.layoutEditor

    val handlePanelAreaChange by formEventHandler() { event: FormEvent<*> ->
        val panel = props.layouts.panels.getBang(event.target.value, "panel")
        props.tab.areas[props.rowIndex * props.tab.columns.size + props.columnIndex] = panel
        props.onChange()
        forceRender()
    }

    div(+styles.gridAreaEditor) {
        Select<SelectProps<String>> {
            attrs.size = Size.small
            attrs.margin = InputBaseMargin.dense
            val currentPanel = props.tab.areas[props.rowIndex * props.tab.columns.size + props.columnIndex]
            val panelId = props.layouts.panels.entries.find { (_, panel) -> panel == currentPanel }!!.key
            attrs.value = panelId
            attrs.onChange = handlePanelAreaChange.withSelectEvent()

            props.layouts.panels.entries.sortedBy { (_, v) -> v.title }.forEach { (panelId, panel) ->
                MenuItem {
                    attrs.value = panelId
                    ListItemText { +panel.title }
                }
            }
        }
    }
}

external interface LayoutAreaCellProps : Props {
    var layouts: MutableLayouts
    var tab: MutableTab
    var columnIndex: Int
    var rowIndex: Int
    var onChange: () -> Unit
}

fun RBuilder.layoutAreaCell(handler: RHandler<LayoutAreaCellProps>) =
    child(LayoutAreaCell, handler = handler)