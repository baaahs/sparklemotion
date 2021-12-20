package baaahs.client.document

import baaahs.DocumentState
import baaahs.PubSub
import baaahs.app.ui.dialog.FileDialog
import baaahs.app.ui.document.DialogHolder
import baaahs.client.Notifier
import baaahs.doc.DocumentType
import baaahs.doc.FileType
import baaahs.gl.Toolchain
import baaahs.io.Fs
import baaahs.io.RemoteFsSerializer
import baaahs.show.mutable.EditHandler
import baaahs.sm.webapi.*
import baaahs.util.UndoStack
import baaahs.window
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule

abstract class DocumentManager<T, TState>(
    val documentType: DocumentType,
    private val pubSub: PubSub.Client,
    private val topic: PubSub.Topic<DocumentState<T, TState>?>,
    private val remoteFsSerializer: RemoteFsSerializer,
    private val toolchain: Toolchain,
    private val notifier: Notifier,
    private val fileDialog: FileDialog,
    private val tSerializer: KSerializer<T>
) {
    abstract val facade: Facade

    private val fileType: FileType get() = documentType.fileType

    var file: Fs.File? = null
        private set
    var document: T? = null
        private set
    var isUnsaved: Boolean = false
        private set
    private var documentAsSaved: T? = null
        private set
    val isLoaded: Boolean
        get() = document != null
    var isSynched: Boolean = false
        private set

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

    protected val editStateChannel =
        pubSub.subscribe(topic) { incoming ->
            switchTo(incoming)
            undoStack.reset(incoming)
            facade.notifyChanged()
        }

    private val serverCommands = object {
        private val commands = Topics.DocumentCommands(documentType.channelName, tSerializer, SerializersModule {
            include(remoteFsSerializer.serialModule)
            include(toolchain.plugins.serialModule)
        })
        val newCommand = pubSub.commandSender(commands.newCommand)
        val switchToCommand = pubSub.commandSender(commands.switchToCommand)
        val saveCommand = pubSub.commandSender(commands.saveCommand)
        val saveAsCommand = pubSub.commandSender(commands.saveAsCommand)
    }

    abstract suspend fun onNew(dialogHolder: DialogHolder)

    suspend fun onNew(newDocument: T? = null) {
        serverCommands.newCommand(NewCommand(newDocument))
    }

    suspend fun onOpen() {
        confirmCloseIfUnsaved() || return

        fileDialog.open(fileType, file)
            ?.withExtension(fileType.extension)
            ?.also { serverCommands.switchToCommand(SwitchToCommand(it)) }
    }

    suspend fun onOpen(file: Fs.File?) {
        serverCommands.switchToCommand(SwitchToCommand(file))
    }

    suspend fun onSave() {
        serverCommands.saveCommand(SaveCommand())
    }

    suspend fun onSaveAs() {
        fileDialog.saveAs(fileType, file)
            ?.withExtension(fileType.extension)
            ?.also { serverCommands.saveAsCommand(SaveAsCommand(it)) }
    }

    suspend fun onSaveAs(file: Fs.File) {
        serverCommands.saveAsCommand(SaveAsCommand(file))
    }
    
    abstract suspend fun onDownload()

    suspend fun onClose() {
        confirmCloseIfUnsaved() || return

        serverCommands.switchToCommand(SwitchToCommand(null))
    }

    abstract fun switchTo(documentState: DocumentState<T, TState>?)

    protected fun update(newDocument: T?, newFile: Fs.File?, newIsUnsaved: Boolean) {
        document = newDocument
        file = newFile
        isUnsaved = newIsUnsaved
        if (!newIsUnsaved) documentAsSaved = newDocument
        isSynched = true
    }

    fun isModified(newDocument: T): Boolean {
        return documentAsSaved?.equals(newDocument) != true
    }

    protected fun confirmCloseIfUnsaved(): Boolean {
        if (!isUnsaved) return true

        // TODO: Use react dialog instead.
        return window.confirm("${documentType.title} is unsaved, okay to close it?")
    }

    protected fun launch(block: suspend () -> Unit) {
        notifier.facade.launchAndReportErrors(block)
    }

    fun release() {
        editStateChannel.unsubscribe()
    }

    abstract inner class Facade : baaahs.ui.Facade(), EditHandler<T, TState> {
        val documentTypeTitle get() = this@DocumentManager.documentType.title
        val file get() = this@DocumentManager.file
        val isLoaded get() = this@DocumentManager.isLoaded
        val isUnsaved get() = this@DocumentManager.isUnsaved
        val canUndo get() = undoStack.canUndo()
        val canRedo get() = undoStack.canRedo()

        suspend fun onNew(dialogHolder: DialogHolder) = this@DocumentManager.onNew(dialogHolder)
        suspend fun onNew(document: T) = this@DocumentManager.onNew(document)
        suspend fun onOpen() = this@DocumentManager.onOpen()
        suspend fun onOpen(file: Fs.File) = this@DocumentManager.onOpen(file)
        suspend fun onSave() = this@DocumentManager.onSave()
        suspend fun onSaveAs() = this@DocumentManager.onSaveAs()
        suspend fun onSaveAs(file: Fs.File) = this@DocumentManager.onSaveAs(file)
        suspend fun onDownload() = this@DocumentManager.onDownload()
        suspend fun onClose() = this@DocumentManager.onClose()
        fun undo() = undoStack.undo()
        fun redo() = undoStack.redo()

        override fun onEdit(document: T, documentState: TState, pushToUndoStack: Boolean) {
            val isUnsaved = this@DocumentManager.isModified(document)
            val editState = DocumentState(document, documentState, isUnsaved, file)
            editStateChannel.onChange(editState)
            switchTo(editState)

            if (pushToUndoStack) {
                undoStack.changed(editState)
            }

            notifyChanged()
        }
    }
}