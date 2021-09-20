package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.io.Fs
import baaahs.ui.Styles.fileDialogFileList
import baaahs.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onDoubleClickFunction
import materialui.Icon
import materialui.components.breadcrumbs.breadcrumbs
import materialui.components.button.button
import materialui.components.buttongroup.buttonGroup
import materialui.components.dialog.dialog
import materialui.components.dialog.enums.DialogMaxWidth
import materialui.components.dialogactions.dialogActions
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.link.link
import materialui.components.list.enums.ListStyle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.components.textfield.textField
import materialui.components.typography.typography
import materialui.icon
import org.w3c.dom.events.Event
import react.*
import react.dom.attrs

private val FileDialog = xComponent<FileDialogProps>("FileDialog") { props ->
    val appContext = useContext(appContext)
    observe(appContext.webClient)

    val fsRoot = appContext.webClient.fsRoot
        ?: return@xComponent // Too early to render, so bail.

    val scope = memo { GlobalScope }
    val dialogEl = useRef(null)
    var currentDir by state { fsRoot }
    var filesInDir by state { emptyList<Fs.File>() }
    var selectedFile by state { props.defaultTarget }

    onChange("default target", props.defaultTarget) {
        props.defaultTarget?.let {
            val job = scope.launch {
                currentDir = if (it.isDir()) it else it.parent!!
            }
            withCleanup {
                job.cancel()
            }
        }
        props.defaultTarget
    }

    val handleFileSingleClick = callback { file: Fs.File ->
        scope.launch {
            if (!file.fs.isDirectory(file)) {
                selectedFile = file
            }
        }
    }

    val handleFileDoubleClick = callback(props.isSaveAs, props.onSelect) { file: Fs.File ->
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

    val handleFileNameChange = callback(currentDir) { event: Event ->
        val str = event.target.value
        selectedFile = currentDir.resolve(str)
    }

    val handleConfirm = callback(selectedFile, props.onSelect) { _: Event ->
        selectedFile?.let { props.onSelect(it) }; Unit
    }

    val handleClose = callback(props.onCancel) { _: Event, _: String ->
        props.onCancel()
    }

    val handleCancel = callback(props.onCancel) { _: Event ->
        props.onCancel()
    }

    onChange("selected fs/dir", props.isOpen, currentDir) {
        val job = scope.launch {
            filesInDir = currentDir.listFiles()
                .sortedWith(compareBy({ !(it.isDirectory ?: false) }, { it.name }))
        }
        withCleanup { job.cancel() }
    }

    val breadcrumbs = arrayListOf<Fs.File>()
    var breadcrumbDir: Fs.File? = currentDir.parent
    while (breadcrumbDir != null) {
        breadcrumbs.add(breadcrumbDir)
        breadcrumbDir = breadcrumbDir.parent
    }
    breadcrumbs.reverse()


    dialog {
        ref = dialogEl
        attrs {
            open = props.isOpen
            onClose = handleClose
            fullWidth = true
            maxWidth = DialogMaxWidth.md
        }

        dialogTitle { +props.title }

        dialogContent {
            breadcrumbs {
                breadcrumbs.forEach { parentDir ->
                    link {
                        attrs.onClickFunction = { currentDir = parentDir }
                        +(if (parentDir.name.isEmpty()) "Filesystem Root" else parentDir.name)
                    }
                }
                typography { +currentDir.name }
            }

            list(fileDialogFileList on ListStyle.root) {
                val parent = currentDir.parent
                if (parent != null) {
                    listItem {
                        attrs.button = true
                        attrs.onClickFunction = { _ -> handleFileSingleClick(parent) }
                        attrs.onDoubleClickFunction = { _ -> handleFileDoubleClick(parent) }
                        listItemIcon { icon(materialui.icons.Folder) }
                        listItemText { attrs.primary { +".." } }
                    }
                }
                filesInDir.forEach { file ->
                    val icon = if (file.isDirectory == true) materialui.icons.Folder else materialui.icons.InsertDriveFile
                    val fileDisplay = FileDisplay(file.name, icon, file.name.startsWith("."))
                    props.fileDisplayCallback?.invoke(file, fileDisplay)

                    if (!fileDisplay.isHidden) {
                        listItem {
                            attrs.button = true
                            attrs.disabled = !fileDisplay.isSelectable
                            attrs.onClickFunction = { _ -> handleFileSingleClick(file) }
                            attrs.onDoubleClickFunction = { _ -> handleFileDoubleClick(file) }
                            fileDisplay.icon?.let { listItemIcon { icon(it) } }
                            listItemText { attrs.primary { +fileDisplay.name } }
                        }
                    }
                }
            }

            if (props.isSaveAs) {
                textField {
                    attrs.label { +"File name…" }
                    attrs.autoFocus = true
                    attrs.fullWidth = true
                    attrs.onChangeFunction = handleFileNameChange
                    attrs.value = selectedFile?.name ?: props.defaultTarget?.name ?: ""
                }
            }
        }

        dialogActions {
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

external interface FileDialogProps : Props {
    var title: String
    var isOpen: Boolean
    var isSaveAs: Boolean
    var fileDisplayCallback: ((Fs.File, FileDisplay) -> Unit)?
    var onSelect: (Fs.File) -> Unit
    var onCancel: () -> Unit
    var defaultTarget: Fs.File?
}

class FileDisplay(
    var name: String,
    var icon: Icon?,
    var isHidden: Boolean = false,
    var isSelectable: Boolean = true
)

fun RBuilder.fileDialog(handler: RHandler<FileDialogProps>) =
    child(FileDialog, handler = handler)
