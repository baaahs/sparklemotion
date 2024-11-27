package baaahs.client.document

import baaahs.DocumentState
import baaahs.DocumentUndoState
import baaahs.PubSub
import baaahs.app.settings.DocumentFeatureFlags
import baaahs.client.Notifier
import baaahs.doc.DocumentType
import baaahs.doc.FileType
import baaahs.io.Fs
import baaahs.io.RemoteFsSerializer
import baaahs.plugin.Plugins
import baaahs.show.mutable.EditHandler
import baaahs.ui.DialogHolder
import baaahs.ui.confirm
import baaahs.util.RefCounted
import baaahs.util.UndoStack
import baaahs.util.globalLaunch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule

abstract class DocumentManager<T, TState, OpenT : OpenDocument<T>>(
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
    abstract val featureFlags: DocumentFeatureFlags

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
    protected var openDocument: OpenT? = null

    protected val undoStack = object : UndoStack<DocumentUndoState<T, TState>>() {
        override fun undo(): DocumentUndoState<T, TState> {
            return super.undo().also { (document, documentState) ->
                facade.onEdit(document, documentState, pushToUndoStack = false)
            }
        }

        override fun redo(): DocumentUndoState<T, TState> {
            return super.redo().also { (document, documentState) ->
                facade.onEdit(document, documentState, pushToUndoStack = false)
            }
        }
    }

    private val stateChannels = mutableListOf<PubSub.Channel<*>>()

    protected var localState: DocumentState<T, TState>? = null
    protected var editState by
        pubSub.state(topic, null, stateChannels) { incoming ->
            switchTo(incoming, isRemoteChange = true)
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

    protected abstract fun openDocument(newDocument: T, newDocumentState: TState?): OpenT

    protected fun switchTo(incomingDocumentState: DocumentState<T, TState>?, isRemoteChange: Boolean) {
        if (!isRemoteChange && featureFlags.autoSync) {
            // Push document to server.
            editState = incomingDocumentState
        }

        val oldLocalState = localState
        localState = incomingDocumentState

        val newDocument = incomingDocumentState?.document
        val newDocumentState = incomingDocumentState?.state
        val newIsUnsaved = incomingDocumentState?.isUnsaved == true
        val newFile = incomingDocumentState?.file

        file = newFile
        val docChanged = document != newDocument
        document = newDocument
        isUnsaved = newIsUnsaved
        if (!newIsUnsaved) documentAsSaved = newDocument
        if (docChanged) {
            val newOpenDocument = newDocument?.let {
                openDocument(newDocument, newDocumentState)
            }
            (openDocument as? RefCounted)?.disuse()
            openDocument = newOpenDocument.also { (it as? RefCounted)?.use() }
        } else if (oldLocalState?.state != newDocumentState) {
            updateState(newDocument, newDocumentState)
        }
        everSynced = true

        onSwitch(isRemoteChange)

        if (isRemoteChange) {
            if (oldLocalState?.file != incomingDocumentState?.file) {
                // If the current file changed on the server side, reset the undo stack.
                undoStack.reset(incomingDocumentState?.toUndoState())
            } else if (
                oldLocalState?.document != incomingDocumentState?.document &&
                incomingDocumentState?.document != null
            ) {
                // If the document was changed by another client, push the change to the undo stack;
                // changes to the document state don't trigger an undo stack push though.
                undoStack.changed(incomingDocumentState.toUndoState())
            }
        }

        facade.notifyChanged()

        if (featureFlags.autoSave && file != null && isUnsaved) {
            globalLaunch { serverCommands.save() }
        }
    }

    protected open fun updateState(t: T?, state: TState?) {}

    open fun onSwitch(isRemoteChange: Boolean) {}

    fun isModified(newDocument: T): Boolean =
        documentAsSaved?.equals(newDocument) != true

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
        val openDocument: OpenDocument<T>? get() = this@DocumentManager.openDocument
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
        val featureFlags get() = this@DocumentManager.featureFlags

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

        override fun onEdit(document: T, documentState: TState, pushToUndoStack: Boolean) {
            val isUnsaved = this@DocumentManager.isModified(document)
            val newEditState = DocumentState(document, documentState, isUnsaved, file)
            switchTo(newEditState, isRemoteChange = false)

            if (pushToUndoStack) {
                undoStack.changed(newEditState.toUndoState())
            }

            notifyChanged()
        }
    }
}