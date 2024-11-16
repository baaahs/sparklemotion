package baaahs.client.document

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.client.Notifier
import baaahs.doc.DocumentType
import baaahs.doc.FileType
import baaahs.io.Fs
import baaahs.io.RemoteFsSerializer
import baaahs.plugin.Plugins
import baaahs.show.mutable.EditHandler
import baaahs.ui.DialogHolder
import baaahs.ui.confirm
import baaahs.util.UndoStack
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule

abstract class DocumentManager<T, TState>(
    val documentType: DocumentType,
    pubSub: PubSub.Client,
    topic: PubSub.Topic<DocumentState<T, TState>?>,
    private val remoteFsSerializer: RemoteFsSerializer,
    private val plugins: Plugins,
    private val notifier: Notifier,
    private val fileDialog: IFileDialog,
    tSerializer: KSerializer<T>
) {
    abstract val facade: Facade
    abstract val documentTitle: String?

    private val fileType: FileType get() = documentType.fileType

    var file: Fs.File? = null
        private set
    var document: T? = null
        private set
    var isUnsaved: Boolean = false
        private set
    private var documentAsSaved: T? = null
    val isLoaded: Boolean
        get() = document != null
    var everSynced: Boolean = false
        private set
    private val isSynced: Boolean
        get() = editState == localState
    val editMode = EditMode(EditMode.Mode.Never)

    protected val undoStack = object : UndoStack<DocumentState<T, TState>>() {
        override fun undo(): DocumentState<T, TState> {
            return super.undo().also { (document, documentState) ->
                facade.onEdit(document, documentState, pushToUndoStack = false)
            }
        }

        override fun redo(): DocumentState<T, TState> {
            return super.redo().also { (document, documentState) ->
                facade.onEdit(document, documentState, pushToUndoStack = false)
            }
        }
    }

    private val stateChannels = mutableListOf<PubSub.Channel<*>>()

    protected var localState: DocumentState<T, TState>? = null
    protected var editState by
        pubSub.state(topic, null, stateChannels) { incoming ->
            localState = incoming
            switchTo(incoming, false)
            undoStack.reset(incoming)
            facade.notifyChanged()
        }

    private val serverCommands =
        documentType.getRpcImpl(tSerializer, SerializersModule {
            include(remoteFsSerializer.serialModule)
            include(plugins.serialModule)
        }).createSender(pubSub)

    abstract suspend fun onNew(dialogHolder: DialogHolder)

    suspend fun onNew(newDocument: T? = null) {
        serverCommands.new(newDocument)
    }

    suspend fun onOpen() {
        confirmCloseIfUnsaved() || return

        fileDialog.open(fileType, file)
            ?.withExtension(fileType.extension)
            ?.also { serverCommands.switchTo(it) }
    }

    suspend fun onOpen(file: Fs.File?) {
        serverCommands.switchTo(file)
    }

    suspend fun onSave() {
        if (file == null) {
            onSaveAs()
        } else {
            serverCommands.save()
        }
    }

    suspend fun onSaveAs() {
        fileDialog.saveAs(fileType, file, documentTitle)
            ?.withExtension(fileType.extension)
            ?.also { serverCommands.saveAs(it) }
    }

    suspend fun onSaveAs(file: Fs.File) {
        serverCommands.saveAs(file)
    }
    
    abstract suspend fun onDownload()

    abstract suspend fun onUpload(name: String, content: String)

    suspend fun onClose() {
        confirmCloseIfUnsaved() || return

        serverCommands.switchTo(null)
    }

    protected abstract fun switchTo(documentState: DocumentState<T, TState>?, isLocalEdit: Boolean)

    protected fun update(newDocument: T?, newFile: Fs.File?, newIsUnsaved: Boolean) {
        document = newDocument
        file = newFile
        isUnsaved = newIsUnsaved
        if (!newIsUnsaved) documentAsSaved = newDocument
        everSynced = true
    }

    fun isModified(newDocument: T): Boolean {
        return documentAsSaved?.equals(newDocument) != true
    }

    protected fun confirmCloseIfUnsaved(): Boolean {
        if (!isUnsaved) return true

        // TODO: Use react dialog instead.
        return confirm("${documentType.title} is unsaved, okay to close it?")
    }

    protected fun launch(block: suspend () -> Unit) {
        notifier.facade.launchAndReportErrors(block)
    }

    fun release() {
        stateChannels.forEach { it.unsubscribe() }
    }

    abstract inner class Facade : baaahs.ui.Facade(), EditHandler<T, TState> {
        abstract val openDocument: OpenDocument?
        val documentType get() = this@DocumentManager.documentType
        val documentTypeTitle get() = this@DocumentManager.documentType.title
        val file get() = this@DocumentManager.file
        val isLoaded get() = this@DocumentManager.isLoaded
        val isSynced get() = this@DocumentManager.isSynced
        val everSynced get() = this@DocumentManager.everSynced
        val isUnsaved get() = this@DocumentManager.isUnsaved
        val canUndo get() = undoStack.canUndo()
        val canRedo get() = undoStack.canRedo()
        val editMode get() = this@DocumentManager.editMode

        suspend fun onNew(dialogHolder: DialogHolder) = this@DocumentManager.onNew(dialogHolder)
        suspend fun onNew(document: T) = this@DocumentManager.onNew(document)
        suspend fun onOpen() = this@DocumentManager.onOpen()
        suspend fun onOpen(file: Fs.File) = this@DocumentManager.onOpen(file)
        suspend fun onSave() = this@DocumentManager.onSave()
        suspend fun onSaveAs() = this@DocumentManager.onSaveAs()
        suspend fun onSaveAs(file: Fs.File) = this@DocumentManager.onSaveAs(file)
        suspend fun onDownload() = this@DocumentManager.onDownload()
        fun confirmCloseIfUnsaved() = this@DocumentManager.confirmCloseIfUnsaved()
        suspend fun onUpload(name: String, content: String) = this@DocumentManager.onUpload(name, content)
        suspend fun onClose() = this@DocumentManager.onClose()
        fun sync() {
            this@DocumentManager.editState = localState
        }
        fun undo() { if (undoStack.canUndo()) undoStack.undo() }
        fun redo() { if (undoStack.canRedo()) undoStack.redo() }

        override fun onEdit(document: T, documentState: TState, pushToUndoStack: Boolean, syncToServer: Boolean) {
            val isUnsaved = this@DocumentManager.isModified(document)
            val newEditState = DocumentState(document, documentState, isUnsaved, file)
            if (syncToServer) {
                this@DocumentManager.editState = newEditState
            }
            switchTo(newEditState, true)

            if (pushToUndoStack) {
                undoStack.changed(newEditState)
            }

            notifyChanged()
        }
    }
}