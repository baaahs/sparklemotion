package baaahs.ui

import baaahs.io.Fs
import baaahs.ui.Styles.fileDialogFileList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onDoubleClickFunction
import materialui.components.button.button
import materialui.components.buttongroup.buttonGroup
import materialui.components.dialog.dialog
import materialui.components.dialog.enums.DialogMaxWidth
import materialui.components.dialogtitle.dialogTitle
import materialui.components.divider.divider
import materialui.components.list.enums.ListStyle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemtext.listItemText
import materialui.components.textfield.textField
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import kotlin.browser.window

private val FileDialog = xComponent<FileDialogProps>("FileDialog") { props ->
    val scope = memo { CoroutineScope(Dispatchers.Main) }
    val dialogEl = useRef(null)
    var selectedFs by state { props.defaultTarget?.fs ?: props.filesystems.first() }
    var currentDir by state { selectedFs.fs.rootFile }
    var filesInDir by state { emptyList<Fs.File>() }
    var selectedFile by state<Fs.File?> { null }

    val handleFsClick = useCallback { fs: SaveAsFs -> selectedFs = fs }

    val handleFileSingleClick = useCallback { file: Fs.File ->
        scope.launch {
            if (!file.fs.isDirectory(file)) {
                selectedFile = file
            }
        }
    }

    val handleFileDoubleClick = useCallback(props.isSaveAs, props.onSelect) { file: Fs.File ->
        scope.launch {
            if (file.fs.isDirectory(file)) {
                currentDir = file
            } else {
                if (props.isSaveAs && file.fs.exists(file)) {
                    if (window.confirm("Overwrite ${file.name}?")) {
                        props.onSelect(file)
                    }
                } else {
                    props.onSelect(file)
                }
            }
        }
    }

    val handleFileNameChange = useCallback(currentDir) { event: Event ->
        val str = event.target!!.asDynamic().value as String
        selectedFile = currentDir.resolve(str)
    }

    val handleConfirm = useCallback(selectedFile, props.onSelect) { event: Event ->
        selectedFile?.let { props.onSelect(it) }; Unit
    }

    val handleClose = useCallback(props.onCancel) { event: Event, reason: String ->
        props.onCancel()
    }

    val handleCancel = useCallback(props.onCancel) { event: Event ->
        props.onCancel()
    }

    onChange("selected fs/dir", props.isOpen, currentDir) {
        val job = scope.launch {
            filesInDir = selectedFs.fs.listFiles(currentDir).sorted()
        }
        withCleanup { job.cancel() }
    }

    dialog {
        ref = dialogEl
        attrs {
            open = props.isOpen
            onClose = handleClose
            fullWidth = true
            maxWidth = DialogMaxWidth.md
        }

        dialogTitle { +props.title }

        table {
            tbody {
                tr {
                    td {
                        list {
                            var first = true
                            props.filesystems.forEach { saveAsFs ->
                                if (!first) divider { } else first = false

                                listItem {
                                    attrs.button = true
                                    attrs.selected = selectedFs == saveAsFs
                                    attrs.onClickFunction = { event: Event -> handleFsClick(saveAsFs) }
                                    listItemText { attrs.primary = saveAsFs.name.asTextNode() }
                                }
                            }
                        }
                    }

                    td {
                        div {
                            list(ListStyle.root to fileDialogFileList.name) {
                                filesInDir.forEach { file ->
                                    listItem {
                                        attrs.button = true
                                        attrs.onClickFunction = { event -> handleFileSingleClick(file) }
                                        attrs.onDoubleClickFunction = { event -> handleFileDoubleClick(file) }
                                        listItemText { attrs.primary = file.name.asTextNode() }
                                    }
                                }
                            }

                            if (props.isSaveAs) {
                                textField {
                                    attrs.label = "File name…".asTextNode()
                                    attrs.onChangeFunction = handleFileNameChange
                                    attrs.value = selectedFile?.name ?: props.defaultTarget?.name ?: ""
                                }
                            }
                        }

                        buttonGroup {
                            button {
                                +"Cancel"
                                attrs.onClickFunction = handleCancel
                            }
                            button {
                                +if (props.isSaveAs) "Save" else "Open"
                                attrs.onClickFunction = handleConfirm
                                attrs.disabled = selectedFile == null
                            }
                        }
                    }
                }
            }
        }
    }
}

external interface FileDialogProps : RProps {
    var title: String
    var isOpen: Boolean
    var isSaveAs: Boolean
    var onSelect: (Fs.File) -> Unit
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

fun RBuilder.fileDialog(handler: RHandler<FileDialogProps>): ReactElement =
    child(FileDialog, handler = handler)
