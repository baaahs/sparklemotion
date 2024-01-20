package baaahs.ui

import baaahs.app.ui.appContext
import baaahs.app.ui.jsIcon
import baaahs.doc.FileDisplay
import baaahs.io.Fs
import baaahs.ui.Styles.fileDialogFileList
import baaahs.util.globalLaunch
import js.objects.jso
import materialui.icon
import mui.icons.material.Folder
import mui.icons.material.InsertDriveFile
import mui.material.*
import mui.system.Breakpoint
import org.w3c.dom.events.Event
import react.*
import react.dom.events.FormEvent
import react.dom.onChange

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

    val handleConfirm by mouseEventHandler(selectedFile, fileDialog) {
        selectedFile?.let {
            globalLaunch { fileDialog.onSelect(it) }
        }; Unit
    }

    val handleClose = callback(fileDialog) { _: Event, _: String ->
        globalLaunch { fileDialog.onCancel() }
        Unit
    }

    val handleCancel by mouseEventHandler(fileDialog) {
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


    Dialog {
        ref = dialogEl
        attrs {
            open = true
            onClose = handleClose
            fullWidth = true
            maxWidth = Breakpoint.md
        }

        DialogTitle { +request.title }

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
                attrs.classes = jso { root = -fileDialogFileList }
                val parent = currentDir!!.parent
                if (parent != null) {
                    ListItemButton {
                        attrs.onClick = { _ -> handleFileSingleClick(parent) }
                        attrs.onDoubleClick = { _ -> handleFileDoubleClick(parent) }
                        ListItemIcon { icon(Folder) }
                        ListItemText { attrs.primary = buildElement  { +".." } }
                    }
                }
                filesInDir.forEach { file ->
                    println("file.fullPath = ${file.fullPath}")
                    val icon = if (file.isDirectory == true) Folder else InsertDriveFile
                    val fileDisplay = FileDisplay(file.name, jsIcon(icon), file.name.startsWith("."))
                    fileDialog.adjustFileDisplay(file, fileDisplay)

                    if (!fileDisplay.isHidden) {
                        ListItemButton {
                            attrs.dense = true
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
                    attrs.onChange = handleFileNameChange
                    attrs.value = selectedFile?.name ?: request.defaultTarget?.name ?: ""
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
                        attrs.onClick = handleConfirm
                    attrs.disabled = selectedFile == null
                }
            }
        }
    }
}

fun RBuilder.fileDialog(handler: RHandler<Props>) =
    child(FileDialogView, handler = handler)
