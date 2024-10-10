package baaahs.client.document

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.app.ui.UiActions
import baaahs.client.ClientStageManager
import baaahs.client.Notifier
import baaahs.doc.ShowDocumentType
import baaahs.gl.Toolchain
import baaahs.io.RemoteFsSerializer
import baaahs.io.resourcesFs
import baaahs.show.SampleData
import baaahs.show.Show
import baaahs.show.ShowMonitor
import baaahs.show.ShowState
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutableDocument
import baaahs.sm.webapi.Problem
import baaahs.sm.webapi.Topics
import baaahs.ui.DialogHolder
import baaahs.ui.DialogMenuItem
import baaahs.ui.DialogMenuItem.Divider
import baaahs.ui.DialogMenuItem.Option

class ShowManager(
    pubSub: PubSub.Client,
    remoteFsSerializer: RemoteFsSerializer,
    private val toolchain: Toolchain,
    notifier: Notifier,
    fileDialog: IFileDialog,
    private val showMonitor: ShowMonitor,
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

        fun makeNew(build: suspend () -> Show?) {
            launch {
                dialogHolder.closeDialog()
                onNew(build())
            }
        }

        dialogHolder.showMenuDialog("New ${documentType.title}…", listOf(
            Option("Empty Show") { makeNew { null } },
            Divider,
            DialogMenuItem.Header("From Template:"),
            Option("BRC 2024") {
                makeNew { fromResources("BRC 2024.sparkle") }
            },
            Option("BRC 2023") {
                makeNew { fromResources("BRC 2023.sparkle") }
            },
            Option("Eve Rafters") {
                makeNew { fromResources("Eve Rafters.sparkle") }
            },
            Option("Pasture Bedtime") {
                makeNew { fromResources("PastureBedtime.sparkle") }
            },
            Option("Sample template") {
                makeNew { SampleData.createSampleShow(withHeadlightsMode = true).getShow() }
            },
            Option("Fancy template (old layout)") {
                makeNew { fromResources("Honcho.sparkle") }
            }
        ))
    }

    private suspend fun fromResources(fileName: String): Show {
        val file = resourcesFs.resolve("templates", "shows", fileName)
        return toolchain.plugins.showStore.load(file)
            ?.copy(title = "New ${documentType.title}")
            ?: error("Couldn't find show")
    }

    override suspend fun onDownload() {
        UiActions.downloadShow(document!!, toolchain.plugins)
    }

    override suspend fun onUpload(name: String, content: String) {
        val show = toolchain.plugins.showStore.decode(content)
        onNew(show)
    }

    override fun switchTo(documentState: DocumentState<Show, ShowState>?, isLocalEdit: Boolean) {
        localState = documentState

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

        showMonitor.onChange(newOpenShow)
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
    }
}