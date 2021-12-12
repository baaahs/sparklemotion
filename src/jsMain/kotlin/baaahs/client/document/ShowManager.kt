package baaahs.client.document

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.app.ui.UiActions
import baaahs.app.ui.dialog.FileDialog
import baaahs.app.ui.dialog.FileType
import baaahs.app.ui.document.DialogHolder
import baaahs.client.ClientStageManager
import baaahs.client.Notifier
import baaahs.gl.Toolchain
import baaahs.io.RemoteFsSerializer
import baaahs.io.resourcesFs
import baaahs.mapper.Storage
import baaahs.show.SampleData
import baaahs.show.Show
import baaahs.show.ShowState
import baaahs.show.live.OpenShow
import baaahs.show.mutable.EditHandler
import baaahs.show.mutable.MutableShow
import baaahs.sm.webapi.ShowProblem
import baaahs.sm.webapi.Topics
import baaahs.util.UndoStack
import baaahs.withState
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
) : DocumentManager<Show>(
    "show", "Show", pubSub, remoteFsSerializer, toolchain, notifier, fileDialog,
    Show.serializer()
) {
    val facade = Facade()

    override val fileType: FileType
        get() = FileType.Show

    var openShow: OpenShow? = null
        private set
    val undoStack = UndoStack<DocumentState<Show, ShowState>>()

    private val showEditStateChannel =
        pubSub.subscribe(
            DocumentState.createTopic(
                toolchain.plugins.serialModule,
                remoteFsSerializer,
                Show.serializer(),
                ShowState.serializer()
            )
        ) { incoming ->
            switchTo(incoming)
            undoStack.reset(incoming)
            facade.notifyChanged()
        }

    private val showProblems = arrayListOf<ShowProblem>().apply {
        pubSub.subscribe(Topics.showProblems) {
            clear()
            addAll(it)
            facade.notifyChanged()
        }
    } as List<ShowProblem>


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

    fun release() {
        showEditStateChannel.unsubscribe()
    }

    private fun switchTo(documentState: DocumentState<Show, ShowState>?) {
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

    inner class Facade : DocumentManager<Show>.Facade(), EditHandler {
        val show get() = this@ShowManager.document
        val openShow get() = this@ShowManager.openShow
        val showProblems get() = this@ShowManager.showProblems
        val undoStack get() = this@ShowManager.undoStack

        override fun onShowEdit(mutableShow: MutableShow, pushToUndoStack: Boolean) {
            onShowEdit(mutableShow.getShow(), openShow!!.getShowState(), pushToUndoStack)
        }

        override fun onShowEdit(show: Show, pushToUndoStack: Boolean) {
            onShowEdit(show, openShow!!.getShowState(), pushToUndoStack)
        }

        override fun onShowEdit(show: Show, showState: ShowState, pushToUndoStack: Boolean) {
            val isUnsaved = this@ShowManager.isModified(show)
            val showEditState = show.withState(showState, isUnsaved, file)
            showEditStateChannel.onChange(showEditState)
            switchTo(showEditState)

            if (pushToUndoStack) {
                undoStack.changed(showEditState)
            }

            notifyChanged()
        }


        fun onShowStateChange() {
            notifyChanged()
        }
    }
}