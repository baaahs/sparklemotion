package baaahs.app.ui.document

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.appContext
import baaahs.client.document.DocumentManager
import baaahs.ui.*
import kotlinx.html.js.onClickFunction
import materialui.components.dialog.dialog
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.divider.divider
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemicon.listItemIcon
import materialui.components.listitemtext.listItemText
import materialui.icon
import react.Props
import react.RBuilder
import react.RHandler
import react.useContext

private val DocumentMenuView = xComponent<DocumentMenuProps>("DocumentMenu") { props ->
    val appContext = useContext(appContext)
    val documentManager = props.documentManager
    observe(documentManager)
    val typeTitle = documentManager.documentTypeTitle

    var renderDialog by state<(RBuilder.() -> Unit)?> { null }
    fun launch(block: suspend () -> Unit) {
        appContext.notifier.launchAndReportErrors(block)
    }

    val handleNew by eventHandler(documentManager) {
        launch {
            documentManager.onNew(object : DialogHolder {
                override fun showDialog(view: View) {
                    renderDialog = {
                        with (view) { render() }
                    }
                }

                override fun showMenuDialog(title: String, options: List<DialogMenuOption>) {
                    showDialog {
                        dialog {
                            attrs.open = true
                            attrs.onClose = { _, _ -> closeDialog() }

                            dialogTitle { +title }
                            dialogContent {
                                options.forEach { option ->
                                    if (option is DialogMenuOption.Divider) {
                                        divider {}
                                    } else {
                                        list {
                                            listItem {
                                                attrs.button = true
                                                attrs.onClickFunction = option.onSelect.withEvent()
                                                listItemText {
                                                    attrs.primary { +option.title }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                override fun closeDialog() { renderDialog = null }
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

    val handleDownload by eventHandler(documentManager) {
        launch { documentManager.onDownload() }
    }

    val handleClose = callback(documentManager) {
        launch { documentManager.onClose() }
    }

    listItem {
        attrs.button = true
        attrs.onClickFunction = handleNew
        listItemIcon { icon(materialui.icons.Add) }
        listItemText { attrs.primary { +"New $typeTitle…" } }
    }

    listItem {
        attrs.button = true
        attrs.onClickFunction = handleOpen.withEvent()
        listItemIcon { icon(materialui.icons.OpenInBrowser) }
        listItemText {
            attrs.primary {
                +if (documentManager.isLoaded) "Switch To $typeTitle…" else "Open $typeTitle…"
            }
        }
    }

    listItem {
        attrs.button = true
        attrs.disabled = !documentManager.isUnsaved || !documentManager.isLoaded
        attrs.onClickFunction = handleSave.withEvent()
        listItemIcon { icon(materialui.icons.Save) }
        listItemText { attrs.primary { +"Save $typeTitle" } }
    }

    listItem {
        attrs.button = true
        attrs.disabled = !documentManager.isLoaded
        attrs.onClickFunction = handleSaveAs.withEvent()
        listItemIcon { icon(materialui.icons.FileCopy) }
        listItemText { attrs.primary { +"Save $typeTitle As…" } }
    }

    listItem {
        attrs.button = true
        attrs.disabled = !documentManager.isLoaded
        attrs.onClickFunction = handleDownload
        listItemIcon { icon(CommonIcons.Download) }
        listItemText { attrs.primary { +"Download $typeTitle" } }
    }

    listItem {
        attrs.button = true
        attrs.disabled = !documentManager.isLoaded
        attrs.onClickFunction = handleClose.withEvent()
        listItemIcon { icon(materialui.icons.Close) }
        listItemText { attrs.primary { +"Close $typeTitle" } }
    }

    renderDialog?.invoke(this)
}

external interface DocumentMenuProps : Props {
    var documentManager: DocumentManager<*, *>.Facade
}

fun RBuilder.documentMenu(handler: RHandler<DocumentMenuProps>) =
    child(DocumentMenuView, handler = handler)