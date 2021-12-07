package baaahs.client.document

import baaahs.PubSub
import baaahs.app.ui.UiActions
import baaahs.app.ui.dialog.FileDialog
import baaahs.app.ui.dialog.FileType
import baaahs.app.ui.document.DialogHolder
import baaahs.client.Notifier
import baaahs.gl.Toolchain
import baaahs.io.RemoteFsSerializer
import baaahs.io.resourcesFs
import baaahs.mapper.Storage
import baaahs.show.SampleData
import baaahs.show.Show
import kotlinx.html.js.onClickFunction
import materialui.components.dialog.dialog
import materialui.components.dialogcontent.dialogContent
import materialui.components.dialogtitle.dialogTitle
import materialui.components.list.list
import materialui.components.listitem.listItem
import materialui.components.listitemtext.listItemText

class ShowManager(
    pubSub: PubSub.Client,
    remoteFsSerializer: RemoteFsSerializer,
    private val toolchain: Toolchain,
    notifier: Notifier,
    fileDialog: FileDialog
) : DocumentManager<Show>(
    "show", "Show", pubSub, remoteFsSerializer, toolchain, notifier, fileDialog
) {
    override val fileType: FileType
        get() = FileType.Show

    override suspend fun onNew(dialogHolder: DialogHolder) {
        if (!confirmCloseIfUnsaved()) return

        dialogHolder.showDialog {
            dialog {
                attrs.open = true
                attrs.onClose = { _, _ -> dialogHolder.closeDialog() }

                dialogTitle { +"New ${documentTypeTitle}…" }
                dialogContent {
                    list {
                        listItem {
                            attrs.button = true
                            attrs.onClickFunction = { _ ->
                                launch { onNew() }
                                dialogHolder.closeDialog()
                            }
                            listItemText {
                                attrs.primary { +"Blank" }
                            }
                        }
                    }
                    list {
                        listItem {
                            attrs.button = true
                            attrs.onClickFunction = { _ ->
                                launch {
                                    onNew(SampleData.createSampleShow(withHeadlightsMode = true).getShow())
                                }
                                dialogHolder.closeDialog()
                            }
                            listItemText {
                                attrs.primary { +"Sample template" }
                            }
                        }
                    }
                    list {
                        listItem {
                            attrs.button = true
                            attrs.onClickFunction = { _ ->
                                launch {
                                    val file = resourcesFs.resolve("Honcho.sparkle")
                                    val show = Storage(resourcesFs, toolchain.plugins).loadShow(file)
                                        ?.copy(title = "New $documentTypeTitle")
                                        ?: error("Couldn't find show")
                                    onNew(show)
                                    dialogHolder.closeDialog()
                                }
                            }
                            listItemText {
                                attrs.primary { +"Fancy template" }
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun onDownload() {
        UiActions.downloadShow(document!!, toolchain.plugins)
    }
}