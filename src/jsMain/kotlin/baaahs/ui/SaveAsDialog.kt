package baaahs.ui

import baaahs.io.Fs
import kotlinx.html.js.onClickFunction
import materialui.components.dialog.dialog
import materialui.components.dialogtitle.dialogTitle
import materialui.components.divider.divider
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemtext.listItemText
import materialui.components.textfield.textField
import org.w3c.dom.events.Event
import react.*
import react.dom.div
import react.dom.table
import react.dom.td
import react.dom.tr

private val SaveAsDialog = functionalComponent<SaveAsDialogProps> { props ->
    val dialogEl = useRef(null)
    val preact = Preact()
    var selectedFs by preact.state { props.defaultTarget?.fs ?: props.filesystems.first() }
    var name by preact.state { props.defaultTarget?.name }
    var currentDir by preact.state { "" }
    var filesInDir by preact.state { emptyList<String>() }

    val handleClose = useCallback(props.onCancel) { event: Event, reason: String ->
        props.onCancel()
    }

    useEffect(selectedFs, currentDir) {
        filesInDir = selectedFs.fs.listFiles(currentDir)
    }

    dialog {
        ref = dialogEl
        attrs {
            open = props.open
            onClose = handleClose
        }

        dialogTitle { +"Save Fragment" }

        table {
            tr {
                td {
                    list {
                        var first = true
                        props.filesystems.forEach { saveAsFs ->
                            if (!first) divider { } else first = false

                            val handleClick = useCallback(saveAsFs) { event: Event -> selectedFs = saveAsFs }
                            listItem {
                                attrs.button = true
                                attrs.selected = selectedFs == saveAsFs
                                attrs.onClickFunction = handleClick
                                listItemText { attrs.primary = saveAsFs.name.asTextNode() }
                            }
                        }
                    }
                }

                td {
                    div {
                        list {
                            filesInDir.forEach {
                                listItem {
                                    listItemText { attrs.primary = it.asTextNode() }
                                }
                            }
                        }

                        textField {
                            attrs.label = "File name…".asTextNode()
                        }
                    }
                }
            }
        }
    }
}

external interface SaveAsDialogProps : RProps {
    var open: Boolean
    var onCancel: () -> Unit
    var filesystems: List<SaveAsFs>
    var defaultTarget: SaveAsTarget?
}

data class SaveAsFs(
    val name: String,
    val fs: Fs
)

data class SaveAsTarget(
    val fs: SaveAsFs?,
    val name: String?
)

fun RBuilder.saveAsDialog(handler: SaveAsDialogProps.() -> Unit): ReactElement =
    child(SaveAsDialog) { attrs { handler() } }
