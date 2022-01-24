package baaahs.client.document

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.app.ui.UiActions
import baaahs.app.ui.dialog.FileDialog
import baaahs.app.ui.document.DialogHolder
import baaahs.client.ClientStageManager
import baaahs.client.Notifier
import baaahs.doc.ShowDocumentType
import baaahs.gl.Toolchain
import baaahs.io.RemoteFsSerializer
import baaahs.io.resourcesFs
import baaahs.mapper.Storage
import baaahs.show.SampleData
import baaahs.show.Show
import baaahs.show.ShowState
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutableDocument
import baaahs.sm.webapi.Problem
import baaahs.sm.webapi.Topics
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
    fileDialog: FileDialog,
    private val stageManager: ClientStageManager
) : DocumentManager<Show, ShowState>(
    ShowDocumentType, pubSub, ShowState.createTopic(toolchain.plugins.serialModule, remoteFsSerializer),
    remoteFsSerializer, toolchain.plugins, notifier, fileDialog, Show.serializer()
) {
    override val facade = Facade()

    private var openShow: OpenShow? = null

    private val problems = arrayListOf<Problem>().apply {
        pubSub.subscribe(Topics.showProblems) {
            clear()
            addAll(it)
            facade.notifyChanged()
        }
    } as List<Problem>


    override suspend fun onNew(dialogHolder: DialogHolder) {
        if (!confirmCloseIfUnsaved()) return

        dialogHolder.showDialog {
            dialog {
                attrs.open = true
                attrs.onClose = { _, _ -> dialogHolder.closeDialog() }

                dialogTitle { +"New ${documentType.title}…" }
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
                                        ?.copy(title = "New ${documentType.title}")
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

    override fun switchTo(documentState: DocumentState<Show, ShowState>?) {
        val newShow = documentState?.document
        val newShowState = documentState?.state
        val newIsUnsaved = documentState?.isUnsaved ?: false
        val newFile = documentState?.file
        val newOpenShow = newShow?.let {
            stageManager.openShow(newShow, newShowState)
        }
        openShow?.disuse()
        openShow = newOpenShow?.also { it.use() }

        update(newShow, newFile, newIsUnsaved)
    }

    inner class Facade : DocumentManager<Show, ShowState>.Facade() {
        val show get() = this@ShowManager.document
        val openShow get() = this@ShowManager.openShow
        val showProblems get() = this@ShowManager.problems

        override fun onEdit(mutableDocument: MutableDocument<Show>, pushToUndoStack: Boolean) {
            onEdit(mutableDocument.build(), openShow!!.getShowState(), pushToUndoStack)
        }

        override fun onEdit(document: Show, pushToUndoStack: Boolean) {
            onEdit(document, openShow!!.getShowState(), pushToUndoStack)
        }

        fun onShowStateChange() {
            notifyChanged()
        }
    }
}