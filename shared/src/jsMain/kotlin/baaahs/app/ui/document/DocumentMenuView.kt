package baaahs.app.ui.document

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.client.document.DocumentManager
import baaahs.ui.*
import baaahs.ui.DialogMenuItem.*
import js.objects.jso
import materialui.icon
import mui.material.*
import org.w3c.files.FileReader
import react.*
import web.cssom.*

private val DocumentMenuView = xComponent<DocumentMenuProps>("DocumentMenu") { props ->
    val appContext = useContext(appContext)
    val documentManager = props.documentManager
    observe(documentManager)
    val typeTitle = documentManager.documentTypeTitle

    var renderDialog by state<(RBuilder.() -> Unit)?> { null }
    fun launch(block: suspend () -> Unit) {
        appContext.notifier.launchAndReportErrors(block)
    }

    val handleNew by mouseEventHandler(documentManager) {
        launch {
            documentManager.onNew(object : DialogHolder {
                override fun showDialog(view: View) {
                    renderDialog = {
                        view.render(this)
                    }
                }

                override fun showMenuDialog(title: String, options: List<DialogMenuItem>) {
                    showDialog {
                        Dialog {
                            attrs.open = true
                            attrs.onClose = { _, _ -> closeDialog() }

                            DialogTitle { +title }
                            DialogContent {
                                List {
                                    attrs.dense = true

                                    options.forEach { option ->
                                        when (option) {
                                            is Divider -> {
                                                Divider {}
                                            }
                                            is Header -> {
                                                ListSubheader {
                                                    attrs.disableGutters = true
                                                    +option.title
                                                }
                                            }
                                            is Option -> {
                                                ListItem {
                                                    attrs.disableGutters = true

                                                    ListItemButton {
                                                        attrs.onClick = option.onSelect.withMouseEvent()

                                                        ListItemText { +option.title }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                override fun closeDialog() {
                    renderDialog = null
                }
            })
        }
    }

    val handleOpen = callback(documentManager) {
        launch { documentManager.onOpen() }
    }

    val handleSave = callback(documentManager) {
        launch { documentManager.onSave() }
    }

    val handleSaveAs = callback(documentManager) {
        launch { documentManager.onSaveAs() }
    }

    val handleDownload by mouseEventHandler(documentManager) {
        launch { documentManager.onDownload() }
    }

    var showUpload by state { false }
    val handleUpload by mouseEventHandler(documentManager) {
        launch { showUpload = documentManager.confirmCloseIfUnsaved() }
    }

    val handleClose = callback(documentManager) {
        launch { documentManager.onClose() }
    }

    ListItemButton {
        attrs.onClick = handleNew
        ListItemIcon { icon(mui.icons.material.Add) }
        ListItemText { attrs.primary = buildElement { +"New $typeTitle…" } }
    }

    ListItemButton {
        attrs.onClick = handleOpen.withMouseEvent()
        ListItemIcon { icon(mui.icons.material.OpenInBrowser) }
        ListItemText {
            attrs.primary = buildElement {
                +if (documentManager.isLoaded) "Switch To $typeTitle…" else "Open $typeTitle…"
            }
        }
    }

    ListItemButton {
        attrs.disabled = !documentManager.isUnsaved || !documentManager.isLoaded
        attrs.onClick = handleSave.withMouseEvent()
        ListItemIcon { icon(mui.icons.material.Save) }
        ListItemText { attrs.primary = buildElement { +"Save $typeTitle" } }
    }

    ListItemButton {
        attrs.disabled = !documentManager.isLoaded
        attrs.onClick = handleSaveAs.withMouseEvent()
        ListItemIcon { icon(mui.icons.material.FileCopy) }
        ListItemText { attrs.primary = buildElement { +"Save $typeTitle As…" } }
    }

    ListItemButton {
        attrs.disabled = !documentManager.isLoaded
        attrs.onClick = handleDownload
        ListItemIcon { icon(CommonIcons.Download) }
        ListItemText { attrs.primary = buildElement { +"Download $typeTitle" } }
    }

    ListItemButton {
        attrs.onClick = handleUpload
        ListItemIcon { icon(CommonIcons.Upload) }
        ListItemText { attrs.primary = buildElement { +"Upload $typeTitle…" } }
    }

    ListItemButton {
        attrs.disabled = !documentManager.isLoaded
        attrs.onClick = handleClose.withMouseEvent()
        ListItemIcon { icon(mui.icons.material.Close) }
        ListItemText { attrs.primary = buildElement { +"Close $typeTitle" } }
    }

    renderDialog?.invoke(this)

    if (showUpload) {
        Modal {
            attrs.open = true

            Box {
                attrs.sx = jso {
                    this.position = Position.absolute
                    this.top = 50.pct
                    this.left = 50.pct
                    this.transform = translate((-50).pct)
                    this.width = 400.px
                    this.backgroundColor = Color("background.paper")
                    this.border = Border(2.px, LineStyle.solid, NamedColor.black)
                    this.boxShadow = "24".unsafeCast<BoxShadow>()
                    this.padding = 4.px
                }

                fileUpload {
                    attrs.fileType = documentManager.documentType.fileType
                    attrs.onUpload = { files, rejections ->
                        console.log("Uploaded!", files)

                        files.forEach { file ->
                            val reader = FileReader()

                            reader.onabort = { console.log("file reading was aborted") }
                            reader.onerror = { console.log("file reading has failed") }
                            reader.onload = {
                                // Do whatever you want with the file contents
                                val result = reader.result
                                launch {
                                    documentManager.onUpload(file.name, result)
                                    showUpload = false
                                }
                            }
                            reader.readAsText(file)
                        }
                    }
                    attrs.onCancel = { showUpload = false }
                }
            }
        }
    }
}

external interface DocumentMenuProps : Props {
    var documentManager: DocumentManager<*, *>.Facade
}

fun RBuilder.documentMenu(handler: RHandler<DocumentMenuProps>) =
    child(DocumentMenuView, handler = handler)