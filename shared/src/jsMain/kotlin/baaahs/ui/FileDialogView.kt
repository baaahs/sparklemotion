package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.app.ui.jsIcon
import baaahs.doc.FileDisplay
import baaahs.io.Fs
import baaahs.ui.Styles.fileDialogFileList
import baaahs.util.globalLaunch
import materialui.icon
import mui.icons.material.Folder
import mui.icons.material.InsertDriveFile
import mui.material.*
import mui.system.Breakpoint
import mui.system.sx
import react.*
import react.dom.events.FormEvent
import react.dom.onChange
import web.cssom.em

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
    var filesInDir by state { emptyList<FileDisplay>() }
    val defaultFile = request?.defaultFile
        ?: request?.defaultFileName?.let { fsRoot?.resolve(it) }
    var selectedFile by state { defaultFile }
    val isSaveAs = request?.isSaveAs == true

    onChange("default file", defaultFile) {
        defaultFile?.let {
            val job = globalLaunch {
                currentDir = if (it.isDir()) it else it.parent!!
                if (!it.isDir()) selectedFile = it
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

    val handleFileDoubleClick = callback(isSaveAs, fileDialog) { file: Fs.File ->
        globalLaunch {
            if (file.fs.isDirectory(file)) {
                currentDir = file
            } else {
                if (isSaveAs && file.fs.exists(file)) {
                    if (confirm("Overwrite ${file.name}?")) {
                        fileDialog.onSelect(file)
                    }
                } else {
                    fileDialog.onSelect(file)
                }
            }
        }
    }

    val handleFileNameChange by formEventHandler(currentDir) { event: FormEvent<*> ->
        val str = event.target.value
        selectedFile = currentDir?.resolve(str)
    }

    val handleTextFieldKeyDown by keyboardEventHandler(selectedFile, fileDialog) { event ->
        if (event.key == "Enter") {
            event.preventDefault()
            selectedFile?.let {
                globalLaunch { fileDialog.onSelect(it) }
            }
        }
    }

    val handleConfirm by mouseEventHandler(selectedFile, fileDialog) {
        selectedFile?.let {
            globalLaunch { fileDialog.onSelect(it) }
        }; Unit
    }

    val handleClose = callback(fileDialog) { _: Any, _: String ->
        globalLaunch { fileDialog.onCancel() }
        Unit
    }

    val handleCancel by mouseEventHandler(fileDialog) {
        globalLaunch { fileDialog.onCancel() }
        Unit
    }

    onChange("selected fs/dir", request, currentDir) {
        currentDir?.let { currentDir ->
            selectedFile = null
            val job = globalLaunch {
                filesInDir = currentDir.listFiles()
                    .sortedWith(compareBy({ it.isDirectory != true }, { it.name }))
                    .map { file ->
                        val icon = if (file.isDirectory == true) Folder else InsertDriveFile
                        FileDisplay(file, file.name, jsIcon(icon), file.name.startsWith("."))
                            .let { fileDialog.adjustFileDisplay(it) }
                    }
            }
            withCleanup { job.cancel() }
        }
    }

    val keyboard = appContext.keyboard
    onMount(keyboard) {
        val handler = keyboard.handle { keypress, _ ->
            var result: KeypressResult? = null

            when (keypress) {
                Keypress("Enter") -> selectedFile?.let {
                    globalLaunch { fileDialog.onSelect(it) }
                }
                Keypress("ArrowUp") -> selectedFile = run {
                    val selectableFiles = filesInDir.filter { it.isSelectable }
                    val idx = selectableFiles.indexOfFirst { it.file == selectedFile }
                    if (idx > 0) selectableFiles[idx - 1] else selectableFiles.firstOrNull()
                }?.file
                Keypress("ArrowDown") -> selectedFile = run {
                    val selectableFiles = filesInDir.filter { it.isSelectable }
                    val idx = selectableFiles.indexOfFirst { it.file == selectedFile }
                    if (idx < selectableFiles.size - 1) selectableFiles[idx + 1] else selectableFiles.lastOrNull()
                }?.file
                else -> result = KeypressResult.NotHandled
            }

            result ?: KeypressResult.Handled
        }

        withCleanup {
            handler.remove()
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


    Dialog {
        ref = dialogEl
        attrs {
            open = true
            onClose = handleClose
            fullWidth = true
            maxWidth = Breakpoint.md
        }

        DialogTitle { +request.dialogTitle }

        DialogContent {
            Breadcrumbs {
                breadcrumbs.forEach { parentDir ->
                    Link {
                        attrs.onClick = { currentDir = parentDir }
                        +(parentDir.name.ifEmpty { "Filesystem Root" })
                    }
                }
                Typography { +currentDir!!.name }
            }

            List {
                attrs.className = -fileDialogFileList
                val parent = currentDir!!.parent
                if (parent != null) {
                    ListItemButton {
                        attrs.onClick = { _ -> handleFileSingleClick(parent) }
                        attrs.onDoubleClick = { _ -> handleFileDoubleClick(parent) }
                        ListItemIcon { icon(Folder) }
                        ListItemText { attrs.primary = buildElement  { +".." } }
                    }
                }
                filesInDir.forEach { fileDisplay ->
                    if (!fileDisplay.isHidden) {
                        val file = fileDisplay.file
                        ListItemButton {
                            attrs.dense = true
                            attrs.selected = selectedFile == file
                            attrs.autoFocus = selectedFile == file
                            attrs.disabled = !fileDisplay.isSelectable
                            attrs.onClick = { _ -> handleFileSingleClick(file) }
                            attrs.onDoubleClick = { _ -> handleFileDoubleClick(file) }
                            fileDisplay.icon?.let { ListItemIcon { icon(it) } }
                            ListItemText { attrs.primary = buildElement { +fileDisplay.name } }
                        }
                    }
                }
            }

            if (isSaveAs) {
                TextField {
                    attrs.label = buildElement { +"File name…" }
                    attrs.autoFocus = true
                    attrs.fullWidth = true
                    attrs.margin = FormControlMargin.normal
                    attrs.onChange = handleFileNameChange
                    attrs.onKeyDown = handleTextFieldKeyDown
                    attrs.value = selectedFile?.name
                        ?: request.defaultFile?.name ?: ""
                    attrs.sx { marginTop = 1.em }
                }
            }
        }

        DialogActions {
            ButtonGroup {
                Button {
                    +"Cancel"
                    attrs.onClick = handleCancel
                }
                Button {
                    +if (isSaveAs) "Save" else "Open"
                    attrs.disabled = selectedFile == null && defaultFile == null
                    attrs.onClick = handleConfirm
                }
            }
        }
    }
}

fun RBuilder.fileDialog(handler: RHandler<Props>) =
    child(FileDialogView, handler = handler)
