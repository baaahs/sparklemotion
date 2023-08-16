package baaahs.app.ui.editor.layout

import baaahs.app.ui.editor.textFieldEditor
import baaahs.show.mutable.MutableGridTab
import baaahs.show.mutable.MutableLayouts
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.h2
import web.html.InputType

private val GridLayoutEditorView = xComponent<GridLayoutEditorProps>("GridLayoutEditor") { props ->

    h2 { +"Grid layout!" }

    div {
        textFieldEditor {
            attrs.type = InputType.number
            attrs.label = "Columns"
            attrs.getValue = { props.tab.columns.toString() }
            attrs.setValue = { newValue -> props.tab.columns = newValue.toInt() }
            attrs.onChange = props.onLayoutChange
        }

        textFieldEditor {
            attrs.type = InputType.number
            attrs.label = "Rows"
            attrs.getValue = { props.tab.rows.toString() }
            attrs.setValue = { newValue -> props.tab.rows = newValue.toInt() }
            attrs.onChange = props.onLayoutChange
        }
    }
}

external interface GridLayoutEditorProps : Props {
    var layouts: MutableLayouts
    var tab: MutableGridTab
    var onLayoutChange: (pushToUndoStack: Boolean) -> Unit
}

fun RBuilder.gridLayoutEditor(handler: RHandler<GridLayoutEditorProps>) =
    child(GridLayoutEditorView, handler = handler)