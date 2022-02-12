package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.app.ui.jsIcon
import baaahs.doc.FileDisplay
import baaahs.io.Fs
import baaahs.ui.Styles.fileDialogFileList
import baaahs.util.globalLaunch
import baaahs.window
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onDoubleClickFunction
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

private val FileDialogView = xComponent<Props>("FileDialog") { props ->
    val appContext = useContext(appContext)
    val fileDialog = appContext.fileDialog
    observe(fileDialog)

    val fsRoot = appContext.webClient.fsRoot
    observe(appContext.webClient)
    var currentDir by state { fsRoot }
    if (currentDir == null && fsRoot != null) currentDir = fsRoot

    val request = fileDialog.fileRequest

    val dialogEl = useRef(null)
    var filesInDir by state { emptyList<Fs.File>() }
    var selectedFile by state { request?.defaultTarget }
    val isSaveAs = request?.isSaveAs ?: false

    onChange("default target", request?.defaultTarget) {
        request?.defaultTarget?.let {
            val job = globalLaunch {
                currentDir = if (it.isDir()) it else it.parent!!
            }
            withCleanup {
                job.cancel()
            }
        }
    }

    val handleFileSingleClick = callback { file: Fs.File ->
        globalLaunch {
            if (!file.fs.isDirectory(file)) {
                selectedFile = file
            }
        }
    }

    val handleFileDoubleClick = callback(isSaveAs) { file: Fs.File ->
        globalLaunch {
            if (file.fs.isDirectory(file)) {
                currentDir = file
            } else {
                if (isSaveAs && file.fs.exists(file)) {
                    if (window.confirm("Overwrite ${file.name}?")) {
                        fileDialog.onSelect(file)
                    }
                } else {
                    fileDialog.onSelect(file)
                }
            }
        }
    }

    val handleFileNameChange = callback(currentDir) { event: Event ->
        val str = event.target.value
        selectedFile = currentDir?.resolve(str)
    }

    val handleConfirm = callback(selectedFile) { _: Event ->
        selectedFile?.let {
            globalLaunch { fileDialog.onSelect(it) }
        }; Unit
    }

    val handleClose = callback { _: Event, _: String ->
        globalLaunch { fileDialog.onCancel() }
        Unit
    }

    val handleCancel = callback { _: Event ->
        globalLaunch { fileDialog.onCancel() }
        Unit
    }

    onChange("selected fs/dir", request, currentDir) {
        currentDir?.let { currentDir ->
            val job = globalLaunch {
                filesInDir = currentDir.listFiles()
                    .sortedWith(compareBy({ !(it.isDirectory ?: false) }, { it.name }))
            }
            withCleanup { job.cancel() }
        }
    }


    if (fsRoot == null || currentDir == null) return@xComponent // Too early to render, so bail.
    if (request == null) return@xComponent

    val breadcrumbs = arrayListOf<Fs.File>()
    var breadcrumbDir: Fs.File? = currentDir?.parent
    while (breadcrumbDir != null) {
        breadcrumbs.add(breadcrumbDir)
        breadcrumbDir = breadcrumbDir.parent
    }
    breadcrumbs.reverse()


    dialog {
        ref = dialogEl
        attrs {
            open = true
            onClose = handleClose
            fullWidth = true
            maxWidth = DialogMaxWidth.md
        }

        dialogTitle { +request.title }

        dialogContent {
            breadcrumbs {
                breadcrumbs.forEach { parentDir ->
                    link {
                        attrs.onClickFunction = { currentDir = parentDir }
                        +(if (parentDir.name.isEmpty()) "Filesystem Root" else parentDir.name)
                    }
                }
                typography { +currentDir!!.name }
            }

            list(fileDialogFileList on ListStyle.root) {
                val parent = currentDir!!.parent
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
                    val fileDisplay = FileDisplay(file.name, jsIcon(icon), file.name.startsWith("."))
                    fileDialog.adjustFileDisplay(file, fileDisplay)

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

            if (isSaveAs) {
                textField {
                    attrs.label { +"File name…" }
                    attrs.autoFocus = true
                    attrs.fullWidth = true
                    attrs.onChangeFunction = handleFileNameChange
                    attrs.value = selectedFile?.name ?: request.defaultTarget?.name ?: ""
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
                        +if (isSaveAs) "Save" else "Open"
                        attrs.onClickFunction = handleConfirm
                    attrs.disabled = selectedFile == null
                }
            }
        }
    }
}

fun RBuilder.fileDialog(handler: RHandler<Props>) =
    child(FileDialogView, handler = handler)
